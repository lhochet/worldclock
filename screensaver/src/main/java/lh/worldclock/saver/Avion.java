package lh.worldclock.saver;

import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.imageio.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * <p>Title: Avion</p>
 *
 * <p>Description: Represents a plane (avion in French) for the UI</p>
 *
 * <p>Copyright: Copyright (c) 2004-2006 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class Avion
{
  public static final int HAUT = -1; // top
  public static final int GAUCHE = -1; // left
  public static final int NEUTRE = 0; // neutral
  public static final int BAS = 1; // bottom
  public static final int DROITE = 1; // right

  public static final int MAX_SPEED = 10;


  private String name;
  private Image image = null;
  private int icx = 0;
  private int icy = 0;
  private Point baseDirection;
  private URL imageURL;
  private Point pos = new Point();
  private Point dir = new Point();


  public Avion()
  {
  }
  
  public Avion(String name, URL imageURL, Point baseDirection)
  {
    this.name = name;
    this.imageURL = imageURL;
    this.baseDirection = baseDirection;
  }
  
  protected void setName(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return name;
  }

  protected void setImageURL(URL imageURL)
  {
    this.imageURL = imageURL;
  }
  
  public URL getImageURL()
  {
    return imageURL;
  }
  

  public void loadImage(int cx, int cy)
  {
    Logger logger = WorldClockSaver.getLogger();
    try
   {
     if (cx < 1) return;
     if (cy < 1) return;
     
     long start = System.currentTimeMillis();
     BufferedImage img = ImageIO.read(imageURL);
     logger.info("loaded " + name + " = " + (System.currentTimeMillis() - start));
     
     icx = img.getWidth();
     icy = img.getHeight();
     
     start = System.currentTimeMillis();
     double fx = ((double)cx) / (icx * 3);
     
     AffineTransform tx = AffineTransform.getScaleInstance(fx, fx);
     AffineTransformOp op = new AffineTransformOp(tx,
         AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
     img = op.filter(img, null);

     icx = (int)(icx * fx);
     icy = (int)(icy * fx);
     
     GraphicsConfiguration gc = GraphicsEnvironment.
       getLocalGraphicsEnvironment().
       getDefaultScreenDevice().getDefaultConfiguration();
     image = gc.createCompatibleImage(icx, icy, Transparency.TRANSLUCENT);
     Graphics2D gImg = (Graphics2D)image.getGraphics();
     gImg.drawImage(img, 0, 0, icx, icy, null);
     gImg.dispose();

     logger.info("scaled " + name + " = " + (System.currentTimeMillis() - start));
     
     initPos(cx, cy);
   }
   catch (IOException ex)
   {
     logger.log(Level.WARNING, ex.getMessage(), ex);
   }
 }

  protected void setBaseDirection(Point baseDirection)
  {
    this.baseDirection = baseDirection;
  }

  public Point getBaseDirection()
  {
    return baseDirection;
  }
  
  public void paint(Graphics g)
  {
    if (image != null)
    {
      g.drawImage(image, pos.x, pos.y, null);
    }
  }
  
  public boolean isShown(int width, int height)
  {
    if (image == null) return false;
    
    boolean ret = true;
    if (dir.x > 0) // towards the right
    {
      if (pos.x > width) // the left of the plane is beyond the screen left border
      {
        ret = false;
      }
    }
    else // towards the left
    {
      if (pos.x + icx < 0) // the right of the plane is beyond the screen right border
      {
        ret = false;
      }
    }
    if (dir.y > 0) //  towards the bottom
    {
      if (pos.y  > height) // the top of the plane is beyond the screen bottom border
      {
        ret = false;
      }
    }
    else // towards the top
    {
      if (pos.y + icy < 1) // the bottom of the plane is beyond the screen top border
      {
        ret = false;
      }
    }
    return ret;
  }

  public void initPos(int width, int height)
  {
    // start point
    Point baseDir = getBaseDirection();
    switch(baseDir.x)
    {
      case Avion.GAUCHE:
        pos.x = width;
        break;
      case Avion.NEUTRE:
        pos.x = randomBool() ? 0 - icx : width;
        break;
      case Avion.DROITE:
        pos.x = 0 - icx;
        break;
    }
    pos.y = randomStart(height);

    // direction
    // x
    int vx = Math.abs(randomVector());
    switch(baseDir.x)
    {
      case Avion.GAUCHE:
        vx = -vx;
        break;
      case Avion.NEUTRE:
        vx = randomBool() ? -vx : vx;
        break;
      case Avion.DROITE:
        // vx = vx;
        break;
    }
    // y
    int vy = Math.abs(randomVectorY());
    switch(baseDir.y)
    {
      case Avion.HAUT:
        vy = -vy;
        break;
      case Avion.NEUTRE:
        vy = randomBool() ? -vy : vy;
        break;
      case Avion.BAS:
        // vy = vy;
        break;
    }

      dir = new Point(vx, vy);
  }
  
  public void move()
  {
    if (image == null) return;
    pos.x += dir.x;
    pos.y += dir.y;
  }

  /**
   * Returns a random number between -MAX_SPEED and MAX_SPEED, inclusive,
   * but excluding 0.
   */
  private int randomVector()
  {
    int result = (int)(Math.random() * MAX_SPEED) + 1;
    if (Math.random() > 0.5)
    {
      result = -result;
    }
    return result;
  }

  /**
   * Returns a random number between -MAX_SPEED/2 and MAX_SPEED/2, inclusive,
   * but excluding 0.
   */
  private int randomVectorY()
  {
    int result = (int)(Math.random() * (MAX_SPEED * 0.5) ) + 1;
    if (Math.random() > 0.5)
    {
      result = -result;
    }
    return result;
  }

  private int randomStart(int cy)
  {
    return (int)(Math.random() * cy);
  }

  private boolean randomBool()
  {
    long i = Math.round(Math.random());
    return i == 0 ? false : true;
  }
  
  public boolean isLoaded()
  {
    return image != null;
  }
  
  public Point getPosition()
  {
    return pos;
  }
  
  public int getWidth()
  {
    return icx;
  }
  
  public int getHeight()
  {
    return icy;
  }

}
