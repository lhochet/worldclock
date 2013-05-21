package lh.worldclock.saver;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import lh.worldclock.core.WorldClockBoard;
import org.jdesktop.jdic.screensaver.ScreensaverSettings;
import org.jdesktop.jdic.screensaver.SimpleScreensaver;


/**
 * <p>Title: WorldClockSaver</p>
 *
 * <p>Description: Screen Saver class</p>
 *
 * <p>Copyright: Copyright (c) 2004-2008 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class WorldClockSaver extends SimpleScreensaver
{
  private static final Logger LOGGER = Logger.getLogger(WorldClockSaver.class.getName());

  private final ResourceBundle res = ResourceBundle.getBundle("lh/worldclock/saver/saverbundle");

  static Logger getLogger()
  {
    return LOGGER;
  }
  private static enum Special
  {
    NOTHING,
    XMAS,
    STPADDY,
    DEBUG
 
  }

  private WorldClockBoard board = null;

  private int width;
  private int height;

  private Avion[] avions = null;
  private Avion avion = null;

  private List<City> cities = new ArrayList<>(0);

  private Component component = null;
  
  // board
  private Image offScreenBoard;
  private Graphics offScreenBoardGraphics;
  // static background
  private Image offScreenStatic;
  private Graphics offScreenStaticGraphics;
  // off screen frame
  private Image offScreenPainter;
  private Graphics2D offScreenPainterGraphics;

  private boolean isFullScreen = true; // full screen display (full screen saver mode as oposed to the view in the windows config dialog)
  private boolean isRandom = false; // random next plane
  private boolean isShowingPlaneInfo = false; // shows the plane information (ie the name of the current plane and of plane being loaded
  private boolean isDebug = false; // shows the debug information (the time between 2 frames)
  private Special special = Special.NOTHING; // special 'effects' (currently only the shadow has such 'effect'), default is no special 'effect'

  private int sleep = 100; // time to sleep between frames (decrease CPU usage)

  private boolean isLoaded = false; // indicates that the parameters have been loaded and off screens created
  private boolean isLoading = false; // indicates that the screen saver is being initiated, must be false
  private String loading = res.getString("LOADING_BASE"); // display message during the loading phase

  // used to calculate if the static board needs to be upated (every minutes or so -- calculation being made to see if the minute has changed since the last call)
  private long lastMinutesCount = 0;
  private long currentMinutesCount = 0;

  // flag to indicate to the paint code that the board has been updated by its renderer thread
  private boolean boardUpdated = false;

  // index of the next plane in the planes array
  private int nextPlaneIndex = 0;
  
  private Avion loadingPlane = null; // reference to a plane being loaded (the image of it that is), flags if another plane can be loaded
  private int nbLoaded = 0; // number of loaded planes, if 0 then no point moving


  // information messages
  private static final int offInfoX = 5; // x offset for the info messages
  private int infoY = 20; // "interval" between 2 info lines
  private int infoMaxX = 0; // max x for the info messages
  private String usingTemplate = res.getString("USING_TEMPLATE");
  private String loadingTemplate = res.getString("LOADING_TEMPLATE");
  private String frameTemplate = res.getString("FRAME_TEMPLATE");

  // debug, used to calculate the time taken to paint a frame
  private long prevFrame = System.currentTimeMillis();
  private long nowFrame = prevFrame;
  private long lastBoardUpdate = System.currentTimeMillis();
  
  // renders the board in a separate thread every 30s (this is for the 'special effects' rather than to update the daylight which could be less often)
  private final Runnable asynchRenderer = new Runnable()
  {
    private final boolean isRunning = true;
    @Override
    public void run()
    {
      while (isRunning)
      {
        try
        {
          Thread.sleep(30000);
        } 
        catch (InterruptedException ex)
        {
          LOGGER.log(Level.FINE, null, ex);
        }

        updateBoard();
        lastBoardUpdate = System.currentTimeMillis();
        boardUpdated = true;
      }
    }
  };
  
  /**
   * Constructor, creates the world clock board object
   */
  public WorldClockSaver()
  {
    super();
    board = new WorldClockBoard();
  }

  /**
   * Initialise the screen saver. In particular load the cities and planes as well as creates the off screen images (also create the log file in ~/.saverbeans/)
   */
  @Override
  public void init()
  {
    // prevent multiple calls
    if (isLoading)
    {
      return;
    }
    isLoading = true;
    isLoaded = false;
    loading = res.getString("LOADING_BASE");

    isFullScreen = getContext().isFullScreen();
    LOGGER.log(Level.INFO, "isFullScreen: {0}", isFullScreen);
    if (!isFullScreen)
    {
      loading = res.getString("LOADING_BASE_SHORT");
      usingTemplate = res.getString("USING_TEMPLATE_SHORT");
      loadingTemplate = res.getString("LOADING_TEMPLATE_SHORT");
      frameTemplate = res.getString("FRAME_TEMPLATE_SHORT");
    }

    try
    {
      FileHandler fh = new FileHandler("%h/.worldclocksaver/worldclocksaver.log");
      LOGGER.addHandler(fh);
    }
    catch (IOException | SecurityException ex)
    {
      LOGGER.log(Level.FINE, ex.getMessage(), ex);
    }

    final ScreensaverSettings settings = getContext().getSettings();
    component = getContext().getComponent();
    
    // hack to make sure a mouse move ends the screen saver
    component.addMouseMotionListener(new MouseMotionAdapter()
    {
      @Override
      public void mouseMoved(MouseEvent e)
      {
        try
        {
          Robot r = new Robot();
          r.keyPress(KeyEvent.VK_DOWN);
        }
        catch (AWTException ex)
        {
          LOGGER.log(Level.FINE, ex.getMessage(), ex);
        }
      }

    });

    // run the main initialisation sequence in it own thread to make the screen saver look 'responsive'
    Runnable imagesCreatorRunnable = () ->
    {
      LOGGER.info("loading...");
      // init setting only when there is a valid screen
      while (width <= 0 || height <= 0)
      {
        width = component.getWidth();
        height = component.getHeight();

        try
        {
          Thread.sleep(25);
        }
        catch (InterruptedException ex)
        {
        }
      }

      // load the config file
      loadConfig(settings.getProperty("planespath"));

      // initialise the speed, sleep time used to delay the showing of the next frame (decrease CPU usage)
      String sspeed = settings.getProperty("speed");
      LOGGER.log(Level.INFO, "base speed = {0}", sspeed);
      if (sspeed != null && !sspeed.equals(""))
      {
        try
        {
          sleep = Integer.parseInt(sspeed);
        }
        catch (NumberFormatException ex)
        {
          LOGGER.log(Level.FINE, ex.getMessage(), ex);
        }
      }
      LOGGER.log(Level.INFO, "sleep = {0}ms", sleep);

      // random or sequencial planes
      if (null != settings.getProperty("random"))
      {
        isRandom = true;
      }
      LOGGER.log(Level.INFO, "isRandom: {0}", isRandom);

      // shows current plane and loading plane names
      if (null != settings.getProperty("info"))
      {
        isShowingPlaneInfo = true;
      }
      LOGGER.log(Level.INFO, "isShowingPlaneInfo: {0}", isShowingPlaneInfo);

      // shows debug info, time between 2 frames
      if (null != settings.getProperty("debug"))
      {
        isDebug = true;
      }
      LOGGER.log(Level.INFO, "isDebug: {0}", isDebug);
      
      // check for special 'effects'
      special = Special.NOTHING;
      if (null != settings.getProperty("xmas"))
      {
        special = Special.XMAS;
      }
      else if (null != settings.getProperty("stpaddy"))
      {
        special = Special.STPADDY;
      }
      else if (null != settings.getProperty("debugshadow"))
      {
        special = Special.DEBUG;
      }
      LOGGER.log(Level.INFO, "special: {0}", special.toString());

      // set the elements location on the board
      board.updateSizeValues(width, height);

      // board
      offScreenBoard = getContext().getComponent().createImage(width, height);
      offScreenBoardGraphics = offScreenBoard.getGraphics();
      board.updateTimeValues();
      board.paintComponent(offScreenBoardGraphics);
      // main
      offScreenStatic = getContext().getComponent().createImage(width, height);
      offScreenStaticGraphics = offScreenStatic.getGraphics();
      // painter
      offScreenPainter = getContext().getComponent().createImage(width, height);
      offScreenPainterGraphics = (Graphics2D)offScreenPainter.getGraphics();

      renderOffscreenStatic(offScreenStaticGraphics);
      offScreenPainterGraphics.drawImage(offScreenStatic, 0, 0, null);

      LOGGER.info("static board loaded");
      isLoaded = true;
      isLoading = false;
      
      Thread asynchRendererThread = new Thread(asynchRenderer);
      asynchRendererThread.start();
    };
    Thread staticBoardLoader = new Thread(imagesCreatorRunnable);
    staticBoardLoader.start();
  }

  /**
   * Load the configuration file, initialise the planes and cities arrays/lists
   * @param path path to the configuration file, if null or empty, only the world clock board will be shown
   */
  private void loadConfig(String path)
  {
    LOGGER.log(Level.INFO, "path = {0}", path);
    if (path == null) {return;}
    if (path.equals("")) {return;}
    if (Files.notExists(Paths.get(path))) {return;}

    ConfigLoader cl = new ConfigLoader();
    cl.load(path);

    List<Avion> lst = cl.getPlanes();
    if (lst.isEmpty())
    {
      avions = null;
    }
    else
    {
      avions = lst.toArray(new Avion[lst.size()]);
    }

    cities = cl.getCities();
  }

  /**
   * Paint a frame onto the screen saver graphic
   * @param graphics screen saver graphic
   */
  @Override
  public void paint(final Graphics graphics)
  {
    // while the static board is not loaded, paint the loading string adding a '.' each iteration
    if (!isLoaded)
    {
      loading += ".";
      graphics.setColor(Color.RED);
      graphics.drawString(loading, 5, 20);
      return;
    }

    try
    {
      // unpaint additional info
      if (isShowingPlaneInfo || isDebug)
      {
        if (infoY > 0)
        {
          Graphics g2 = offScreenPainterGraphics.create();
          g2.setClip(0, 0, infoMaxX + offInfoX + 10, infoY); // 10: extra margin
          g2.drawImage(offScreenStatic, 0, 0, null);
          g2.dispose();
        }
      }

      // paint the frame
      paintFrame();
      
      // paint additional info
      offScreenPainterGraphics.setColor(Color.RED);
      infoY = 20;
      infoMaxX = 0;

      if (isShowingPlaneInfo)
      {
        if (avion != null)
        {
          String str = String.format(usingTemplate, avion.getName());
          int mx = 1 + (int)(new TextLayout(str, offScreenPainterGraphics.getFont(), offScreenPainterGraphics.getFontRenderContext())).getBounds().getWidth();
          if (mx > infoMaxX) infoMaxX = mx;
          offScreenPainterGraphics.drawString(str, offInfoX, infoY);
          infoY += 20;
        }
        if (loadingPlane != null)
        {
          String str = String.format(loadingTemplate, loadingPlane.getName());
          int mx = 1 + (int)(new TextLayout(str, offScreenPainterGraphics.getFont(), offScreenPainterGraphics.getFontRenderContext())).getBounds().getWidth();
          if (mx > infoMaxX) infoMaxX = mx;
          offScreenPainterGraphics.drawString(str, offInfoX, infoY);
          infoY += 20;
        }
      }

      if (isDebug)
      {
        String str = String.format(frameTemplate, nowFrame - prevFrame, (System.currentTimeMillis() - lastBoardUpdate) / 1000, toggledebug ? "*" : "");
        int mx = 1 + (int)(new TextLayout(str, offScreenPainterGraphics.getFont(), offScreenPainterGraphics.getFontRenderContext())).getBounds().getWidth();
        if (mx > infoMaxX) infoMaxX = mx;
        offScreenPainterGraphics.drawString(str, offInfoX, infoY);
      }

      // flush the back screen to the front screen
      graphics.drawImage(offScreenPainter, 0, 0, null);


      prevFrame = nowFrame;
      nowFrame = System.currentTimeMillis();

      // delay the next frame, decrease the CPU usage
      try
      {
        Thread.sleep(sleep);
      }
      catch (InterruptedException ex)
      {
      }
    }
    catch (Exception ex)
    {
      LOGGER.log(Level.WARNING, ex.getMessage(), ex);
    }
    catch (Error ex)
    {
      LOGGER.log(Level.WARNING, ex.getMessage(), ex);
      throw ex;
    }
  }
  
  /**
   * Update the off screen image. Every time the minutes count change, repaint the board and cities to reflect the time change, 
   * unpaint the current plane, moves it and paint the plane at the new position
   */
  private void paintFrame()
  {
    // check if the static image should be updated
    currentMinutesCount = System.currentTimeMillis() / 60000;
    if (boardUpdated || currentMinutesCount > lastMinutesCount)
    {
      boardUpdated = false;
      lastMinutesCount = currentMinutesCount;
      
      renderOffscreenStatic(offScreenStaticGraphics);
      offScreenPainterGraphics.drawImage(offScreenStatic, 0, 0, null);
    }
    else
    {
      unrenderPlaneOffscreen(offScreenPainterGraphics);
    }

    move();
    renderPlaneOffscreen(offScreenPainterGraphics);
  }
  
  /**
   * updates the board applying 'special effects'
   */
  private void updateBoard()
  {
      board.updateTimeValues();
       
      switch (special)
      {
        case NOTHING:
          break;
        case XMAS:
          xmas();
          break;
        case STPADDY:
          stpaddy();
          break;
        case DEBUG:
          debug();
          break;
      }

      board.paintComponent(offScreenBoardGraphics);
  }
  
  /**
   * 'Xmas tree' effect
   */
  private void xmas()
  {
    int i = (int)(Math.random() * 14);
    switch (i)
    {
      case 0: // normal
        board.setSpecial(false);
        break;
      case 1:
        board.setSpecial(true);
        board.setSpecialColour(Color.BLACK);
        break;
      case 2:
        board.setSpecial(true);
        board.setSpecialColour(Color.BLUE);
        break;
      case 3:
        board.setSpecial(true);
        board.setSpecialColour(Color.CYAN);
        break;
      case 4:
        board.setSpecial(true);
        board.setSpecialColour(Color.DARK_GRAY);
        break;
      case 5:
        board.setSpecial(true);
        board.setSpecialColour(Color.GRAY);
        break;
      case 6:
        board.setSpecial(true);
        board.setSpecialColour(Color.GREEN);
        break;
      case 7:
        board.setSpecial(true);
        board.setSpecialColour(Color.LIGHT_GRAY);
        break;
      case 8:
        board.setSpecial(true);
        board.setSpecialColour(Color.MAGENTA);
        break;
      case 9:
        board.setSpecial(true);
        board.setSpecialColour(Color.ORANGE);
        break;
      case 10:
        board.setSpecial(true);
        board.setSpecialColour(Color.PINK);
        break;
      case 11:
        board.setSpecial(true);
        board.setSpecialColour(Color.RED);
        break;
      case 12:
        board.setSpecial(true);
        board.setSpecialColour(Color.WHITE);
        break;
      case 13:
        board.setSpecial(true);
        board.setSpecialColour(Color.YELLOW);
        break;
    }
     
  }

  // counter for the St Patrick's effect
  private static int nbstpaddychimes = 0;
  
  /**
   * St Patrick's effect (3 normal, 2 green, 1 orange)
   */
  private void stpaddy()
  {
    if (nbstpaddychimes == 5)
    {
        board.setSpecial(true);
        board.setSpecialColour(Color.ORANGE);
        nbstpaddychimes = 0;
    }
    else if (nbstpaddychimes > 2)
    {
        board.setSpecial(true);
        board.setSpecialColour(Color.GREEN);
    }
    else
    {
        board.setSpecial(false);
    }
    nbstpaddychimes++;
  }

  // toggle for the debug effect
  private static boolean toggledebug = false;
  
  /**
   * debug effect, used to check when the shadow is repainted
   */
  private void debug()
  {
    toggledebug = !toggledebug;
    if (toggledebug)
    {
        board.setSpecial(true);
        board.setSpecialColour(Color.GREEN);
    }
    else
    {
        board.setSpecial(false);
    }
  }
  
  /** 
   * Renders the static part of the image (board and cities)
   */
  void renderOffscreenStatic(Graphics g)
  {
    if (offScreenBoard != null)
    {
      g.drawImage(offScreenBoard, 0, 0, null);
    }
    cities.stream().forEach((city) ->
    {
      city.paint(g, width, height, isFullScreen);
    });
  }

  /**
   * Renders the current plane 
   * @param g Graphics onto which the plane is rendered, normally the offscreen graphics
   */
  void renderPlaneOffscreen(Graphics g)
  {
    if (avion != null)
    {
      avion.paint(g);
    }
  }

  /**
   * Unrender the current plane (ie copy the rectangle on which plane is from the static board onto the offscreen image)
   * @param g Graphics onto which the plane is unrendered, normally the offscreen graphics
   */
  void unrenderPlaneOffscreen(Graphics g)
  {
    if (avion != null)
    {
      Point p = avion.getPosition();
      int x = p.x;
      int y = p.y;
      int cx = avion.getWidth();
      int cy = avion.getHeight();

      // if the position is negative the corresponding part between the position and 0 is not shown, so there is no need to unpaint it
      if (x < 0)
      {
        x = 0;
        cx += x;
      }
      if (y < 0)
      {
        y = 0;
        cy += y;
      }
      Graphics g2 = g.create();
      g2.setClip(x, y, cx, cy);
      g2.drawImage(offScreenStatic, 0, 0, null);
      g2.dispose();
    }
  }

  /**
   * Destroy the screen saver (clean up resources) 
   */
  @Override
  protected void destroy()
  {
    // dispose graphics
    offScreenBoardGraphics.dispose();
    offScreenStaticGraphics.dispose();
    offScreenPainterGraphics.dispose();
    // dispose images
    offScreenBoard = null;
    offScreenStatic = null;
    offScreenPainter = null;
    // dispose lists
    avions = null;
    cities = null;
    
    super.destroy();
  }

  /**
   * Returns the next plane, calls the appropriate method depending on the random flag
   * @return the next plane or null
   */
  private Avion getNextPlane()
  {
    if (isRandom)
    {
      return randomPlane();
    }
    else
    {
      return sequencePlane();
    }
  }

  /**
   * Returns the next plane in sequence
   * @return the next plane or null if the array is empty
   */
  private Avion sequencePlane()
  {
    if (avions == null)
    {
      return null;
    }

    if (nextPlaneIndex < avions.length - 1)
    {
      nextPlaneIndex++;
    }
    else
    {
      nextPlaneIndex = 0;
    }
    return avions[nextPlaneIndex];
  }

  /**
   * Returns the next plane randomly
   * @return the next plane or null if the array is empty
   */
  private Avion randomPlane()
  {
    if (avions == null)
    {
      return null;
    }

    int i = (int)(Math.random() * avions.length);
    return avions[i];
  }

  /**
   * Moves the current plane, get the next plane once the current plane has moved off screen, loading the next one if needed
   */
  private void move()
  {
    Avion nextPlane = avion; // next plane to be shown, unless it need to be changed (first time or moved offscreen) it is set to the current plane
    if (nextPlane == null) // planes null, get a plane
    {
      nextPlane = getNextPlane();
    }
    else
    {
      if (!nextPlane.isShown(width, height)) // if the plane is no longer shown then get the next one
      {
        nextPlane = getNextPlane();
      }
    }

    // if the next plane is null, no point moving it
    if (nextPlane != null)
    {
      // check if the image of the next plane is loaded, 
      // if not checks if it can load it, 
      // if it can then load it in a separate thread to avoid (seriously) delaying the current frame display
      if (!nextPlane.isLoaded())
      {
        if (loadingPlane == null)
        {
          loadingPlane = nextPlane;
          new Thread(() ->
          {
            LOGGER.log(Level.INFO, "loading {0}", loadingPlane.getName());
            loadingPlane.loadImage(width, height);
            LOGGER.log(Level.INFO, "loaded {0}", loadingPlane.getName());
            loadingPlane = null;
            ++nbLoaded;
          }).start();
        }
      }

      // if no plane is loaded no need to move
      if (nbLoaded > 0)
      {
        // check if the next plane is loaded, if it isn't then tries to get a loaded one (checks for null as getNextPlane can return a null plane)
        while (nextPlane == null || !nextPlane.isLoaded())
        {
          nextPlane = getNextPlane();
          if (nextPlane != null)
          {
            if (nextPlane.isLoaded())
            {
              LOGGER.log(Level.INFO, "using avion = {0}", nextPlane.getName());
            }
            else
            {
              LOGGER.log(Level.INFO, "avion not loaded = {0}", nextPlane.getName());
            }
          }
        }

        // a plane is loaded, set it as the current plane
        avion = nextPlane;
        // if the current plane is not shown then initialise it position so that it is (shown)
        if (!avion.isShown(width, height))
        {
          LOGGER.log(Level.INFO, "init pos avion = {0}", avion.getName());
          avion.initPos(width, height);
        }
        // last but not least: moves the plane
        avion.move();
      } // EO nb planes > 0
    } // EO plane's null
  }

}
