package lh.worldclock.core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * <p>
 * Title: WorldClockBoard
 * </p>
 * 
 * <p>
 * Description: Draws the Earth day and night
 * </p>
 * This part is directly based on J�rgen Giesen's <a
 * href="http://www.geoastro.de/TNApplet/DN/index.html">Day & Night Applet</a><br/>
 * Images are from <a href="http://visibleearth.nasa.gov/">NASA's Visible Earth</a>
 * website !
 * 
 * <p>
 * Copyright: Copyright (c) 2004-2007 The WorldClock Application Team
 * </p>
 * 
 * @author Ludovic HOCHET
 * @author Guus der Kinderen
 * @version $Revision$ $Date$
 */
public class WorldClockBoard extends JComponent
{
  private static final long serialVersionUID = 2622878225689691584L;

  private static final double PI_DIVIDED_BY_180 = Math.PI / 180.0;

  private final BufferedImage originalEarthImageDay, originalEarthImageNight;

  private ImageIcon scaledEarthImageDay, scaledEarthImageNight;

  // There's no point in persistent storing this. Its content should change with
  // every tick of the clock anyway.
  private transient Image clippedImage;
  private int width = 0;
  private int height = 0;

  // drawing
  private int offx = 0; // offset
  private int offy = 0;
  private int equator = 0;
  private int sunRadius = 6;
  private double xStep = 0.0;
  private double yStep = 0.0;
  private int xMinEquator = 0;
  private int xMaxEquator = 0;
  private int xMeridien = 0;
  private int yMinMeridien = 0;
  private int yMaxMeridien = 0;
  private int yArticPolar = 0;
  private int yCancer = 0;
  private int yCapricorn = 0;
  private int yAntarticPolar = 0;
  private int yOneDegree = 0;

  // full night polygon
  Polygon fullNightPolygon = null;

  // twilight
  private final int division = 10;
  private final int nbTwilightZone = 18 * division; // 18: number of degrees covered by the twilight zone
  Polygon[] twilightPolygons = null;


  // astro maths
  private double declination, GHA;
  private int xSun360;

  // customisation
  private boolean showLines = true;
  private boolean special = false;
  private Color specialColour = Color.GREEN;

  /**
   * Constructs a new object that can draw the current globe.
   */
  public WorldClockBoard()
  {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    URL imageDayURL = WorldClockBoard.class.getResource("world.jpg");
    URL imageNightURL = WorldClockBoard.class.getResource("world_night.jpg");

    originalEarthImageDay = toBufferedImage(toolkit.getImage(imageDayURL));
    originalEarthImageNight = toBufferedImage(toolkit.getImage(imageNightURL));

    scaledEarthImageDay = new ImageIcon(originalEarthImageDay);
    scaledEarthImageNight = new ImageIcon(originalEarthImageNight);

    width = originalEarthImageDay.getWidth();
    height = originalEarthImageDay.getHeight();
    setSize(width, height);

    updateSizeValues();
  }

  /**
   * Determine if the polar, tropical, equator and Greenwhich meridian lines are shown
   * @param showLines Set to true to show lines
   */
  public void setShowLines(boolean showLines)
  {
    this.showLines = showLines;
  }

  /**
   * Return wether the polar, tropical, equator and Greenwhich meridian lines are shown or not
   * @return true if the lines are shown
   */
  public boolean isShowLines()
  {
    return showLines;
  }

  /**
   * Updates the content of this component to the size of this component.
   */
  public void updateSizeValues()
  {
    Dimension dim = getSize();
    updateSizeValues(dim.width, dim.height);
  }

  /**
   * Updates the content of this component to a specific size.
   * 
   * @param newWidth
   *          New width value (in pixels) of the component.
   * @param newHeight
   *          New height value (in pixels) of the component.
   */
  public void updateSizeValues(int newWidth, int newHeight)
  {
    width = newWidth;
    height = newHeight;
    if (width < 1 || height < 1)
      return;

    // Does the new window size correspond with the original image size?
    final int imageWidth = originalEarthImageDay.getWidth();
    final int imageHeight = originalEarthImageDay.getHeight();

    if (width != imageWidth || height != imageHeight)
    {
      // if not, recreate the scaled images from the original ones.
      AffineTransform tx = new AffineTransform();
      tx.scale((double) width / imageWidth, (double) height / imageHeight);
      AffineTransformOp op = new AffineTransformOp(tx,
          AffineTransformOp.TYPE_BILINEAR);

      BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment()
          .getDefaultScreenDevice().getDefaultConfiguration()
          .createCompatibleImage(originalEarthImageDay.getWidth(),
              originalEarthImageDay.getHeight(), Transparency.BITMASK);

      Graphics g = img.getGraphics();
      g.drawImage(originalEarthImageDay, 0, 0, null);
      g.dispose();
      scaledEarthImageDay.setImage(op.filter(img, null));

      g = img.getGraphics();
      g.drawImage(originalEarthImageNight, 0, 0, null);
      g.dispose();
      scaledEarthImageNight.setImage(op.filter(img, null));
    }

    clippedImage = GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice().getDefaultConfiguration()
        .createCompatibleImage(width, height, Transparency.BITMASK);

    // update 'lines' positions
    equator = (int)Math.round(height / 2.0);
    xMinEquator = 0;
    xMaxEquator = width;
    xMeridien = (int)Math.round(width / 2.0);
    yMinMeridien = 0;
    yMaxMeridien = height;

    xStep = width / 360.0;
    yStep = equator / 90.0;

    yArticPolar = (int)Math.round(equator - 66.5 * yStep);
    yCancer = (int)Math.round(equator - 23.5 * yStep);
    yCapricorn = (int)Math.round(equator + 23.5 * yStep);
    yAntarticPolar = (int)Math.round(equator + 66.5 * yStep);

    yOneDegree = (int)Math.round(yStep);

    if (width < 400 || height < 400)
    {
      sunRadius = 2;
    }
    else
    {
      sunRadius = 5;
    }

    updateTimeValues();
  }

  /**
   * Computes the position of elements on the globe based on the current system
   * time.
   */
  public void updateTimeValues()
  {
    // time
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int month = cal.get(Calendar.MONTH) + 1;
    int year = cal.get(Calendar.YEAR);
    int hours = cal.get(Calendar.HOUR_OF_DAY);
    int minutes = cal.get(Calendar.MINUTE);
    int seconds = cal.get(Calendar.SECOND);

    double decimalHours = 1.0 * hours + minutes / 60.0 + seconds / 3600.0;

    // compute declination of the sun
    declination = computeDeclination(day, month, year, decimalHours);

    // compute Greenwich Hour Angle of the sun
    GHA = computeGHA(day, month, year, decimalHours);

    // sun location
    xSun360 = 180 - (int) Math.round(GHA);
    if (xSun360 < 0)
    {
      xSun360 += 360;
    }
    if (xSun360 > 360)
    {
      xSun360 -= 360;
    }

    // update twilitght/night polygons
    computePolygons();

    special = !special;

    // fun
    if (day == 17 && month == Calendar.MARCH)
    {
      special = true;
      specialColour = Color.GREEN;
    }
    else if (day == 14 && month == Calendar.FEBRUARY)
    {
//      special = true;
      specialColour = Color.RED;
    }
    
//    special = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  @Override
public void paintComponent(Graphics g)
  {
    if (width == 0) return;

    Graphics2D g2 = (Graphics2D) clippedImage.getGraphics();

    // day
    g2.drawImage(scaledEarthImageDay.getImage(), 0, 0, this);

    // night
    g2.setClip(fullNightPolygon);
    g2.drawImage(scaledEarthImageNight.getImage(), 0, 0, this);      

    // twilight
    float ratio = 1.0f / ((float)nbTwilightZone / 1.5f);
    for (int j = 0; j < nbTwilightZone; j++)
    {
      float r = j * ratio + 0.2f; // 0.2f: arbitrary minimum of opacity
      if (r > 0.99f) r = 0.99f; // make sure this doesn't go to 1.0, which is for full night only
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, r));
      g2.setClip(twilightPolygons[j]);
      if (special)
      {
        g2.setColor(specialColour);
        g2.fillRect(0, 0, scaledEarthImageNight.getIconWidth(), scaledEarthImageNight.getIconHeight());
      }
      else
      {
        g2.drawImage(scaledEarthImageNight.getImage(), 0, 0, this);
      }
    }


    g2.dispose();

    g.drawImage(clippedImage, 0, 0, this);

    if (showLines)
    {
      // equator lines and meridien
      g.setColor(Color.RED);
      // equator
      g.drawLine(xMinEquator, equator, xMaxEquator, equator);
      // meridien
      g.drawLine(xMeridien, yMinMeridien, xMeridien, yMaxMeridien);

      // parallels
      int xx = xMinEquator;
      while (xx < xMaxEquator)
      {
        g.setColor(Color.PINK);
        g.drawLine(xx, yArticPolar, xx + 2, yArticPolar);
        g.setColor(Color.RED);
        g.drawLine(xx, yCancer, xx + 2, yCancer);
        g.drawLine(xx, yCapricorn, xx + 2, yCapricorn);
        g.setColor(Color.PINK);
        g.drawLine(xx, yAntarticPolar, xx + 2, yAntarticPolar);
        xx = xx + 6;
      }
    }

    // sun location
    int xSun = (int) Math.round(xSun360 * xStep);
    int ySun = equator - (int) Math.round(declination);

    g.setColor(Color.YELLOW);
    g.fillOval(xSun - sunRadius, ySun - sunRadius, 2 * sunRadius,
        2 * sunRadius);

    g.setColor(Color.RED);
    g.drawOval(offy + xSun - sunRadius - 1, ySun - sunRadius - 1,
        2 * sunRadius + 2, 2 * sunRadius + 2);
  }

  /**
   * Compute the polygons over the globe where the sunbeam hits the ground at a
   * specific angle.
   * 
   * If the offset is 0, the line represents places where the suns position is
   * exactly at the horizon. An offset of 18 indicates full night.
   */
  private void computePolygons()
  {
    if (width == 0) return;

    int x = 0;
    int y = 0;

    int yMinShadow = 0; // top/bottom border of the screen that is in the night
    if (declination > 0)
    {
      yMinShadow = height;
    }
    else
    {
      yMinShadow = 0;
    }


    final int nbPoints = 360 + 1; // + 1 to duplicate the first point at the other side of the screen
    final int fullNightIndex = nbTwilightZone;
    final int nbOffsets = fullNightIndex + 1;  // +1 to include the full night in the index
    int[][] xs = new int[nbOffsets][nbPoints];
    int[][] ys = new int[nbOffsets][nbPoints];
    int[] offsets = new int[nbOffsets];

    // initialise offsets
    final float yOneDegreeDivision = (float)yOneDegree / (float)division;
    for (int j = 0; j < nbOffsets; j++)
    {
      offsets[j] =  (int)(j * yOneDegreeDivision);
    }

    final boolean isDeclinationNegative = declination < 0;
    
    // compute the polygon point locations
    for (int i = -xSun360, k = 0; k < nbPoints; i++, k++)
    {
      x = offx + (int)(k * xStep);
      y = (int) Math.round(computeLat(i, declination) * yStep);

      if (isDeclinationNegative)
      {
        for (int j = 0; j < nbOffsets; j++)
        {
          xs[j][k] = x;
          ys[j][k] = y + offsets[j];
        }
      }
      else
      {
        for (int j = 0; j < nbOffsets; j++)
        {
          xs[j][k] = x;
          ys[j][k] = y - offsets[j];
        }
      }

    }

    // creates full night polygon
    fullNightPolygon = new Polygon();
    fullNightPolygon.addPoint(offx, yMinShadow);
    for (int i = 0; i < nbPoints; i++)
    {
      fullNightPolygon.addPoint(xs[fullNightIndex][i], equator - ys[fullNightIndex][i]);
    }
    fullNightPolygon.addPoint(width, equator - ys[fullNightIndex][nbPoints-1]);
    fullNightPolygon.addPoint(width, yMinShadow);
    fullNightPolygon.addPoint(offx, yMinShadow);

    // creates twilight polygons
    twilightPolygons = new Polygon[fullNightIndex];
    for (int j = 0; j < fullNightIndex; j++)
    {
      twilightPolygons[j] = new Polygon();
      // one line with this twilight limit
      twilightPolygons[j].addPoint(offx, equator - ys[j][0]);
      for (int i = 0; i < nbPoints; i++)
      {
        twilightPolygons[j].addPoint(xs[j][i], equator - ys[j][i]);
      }
      twilightPolygons[j].addPoint(width, equator - ys[j][nbPoints-1]);
      // the 'return' line with the darker twilight limit
      final int jp1 = j + 1;
      twilightPolygons[j].addPoint(width, equator - ys[jp1][nbPoints-1]);
      for (int i = nbPoints - 1; i > -1; i--)
      {
        twilightPolygons[j].addPoint(xs[jp1][i], equator - ys[jp1][i]);
      }
      twilightPolygons[j].addPoint(offx, equator - ys[jp1][0]);
    }

  }

  /**
   * Calculates the declination of the sun on a specific date and time.
   * 
   * In astronomy, declination (abbrev. dec or δ) is one of the two coordinates
   * of the equatorial coordinate system, the other being either right ascension
   * or hour angle. Dec is comparable to latitude, projected onto the celestial
   * sphere, and is measured in degrees north and south of the celestial
   * equator. Therefore, points north of the celestial equator have positive
   * declination, while those to the south have negative declination.
   * 
   * An object on the celestial equator has a dec of 0°. An object above the
   * north pole has a dec of +90°. An object above the south pole has a dec of
   * −90°.
   * 
   * The declination of the Sun (δ) is the angle between the rays of the sun and
   * the plane of the earth equator. Since the angle between the earth axis and
   * the plane of the earth orbit is nearly constant, δ varies with the seasons
   * and its period is one year, that is the time needed by the earth to
   * complete its revolution around the sun.
   * 
   * @param day
   *          Day of the month (1-31).
   * @param month
   *          Month of year (1-12, where January is 1 ).
   * @param year
   *          four digit year.
   * @param decimalHours
   *          The hour, minute and seconds, in a decimal notation ( 10:30:00 ==
   *          10.5 ).
   * @return Declination of the sun on a specific date and time.
   */
  private static double computeDeclination(int day, int month, int year,
      double decimalHours)
  {
    long n;
    double x;
    double ekliptik, j2000;

    n = 365 * year + day + 31 * month - 46;
    if (month < 3)
    {
      n = n + (int) ((year - 1) / 4);
    }
    else
    {
      n = n - (int) (0.4 * month + 2.3) + (int) (year / 4.0);
    }

    x = (n - 693960) / 1461.0;
    x = (x - (int) x) * 1440.02509 + (int) x * 0.0307572;
    x = x + decimalHours / 24.0 * 0.9856645 + 356.6498973;
    x = x + 1.91233 * Math.sin(0.9999825 * x * PI_DIVIDED_BY_180);
    x = (x + Math.sin(1.999965 * x * PI_DIVIDED_BY_180) / 50.0 + 282.55462) / 360.0;
    x = (x - (int) x) * 360.0;

    j2000 = (year - 2000) / 100.0;
    ekliptik = 23.43929111 - (46.8150 + (0.00059 - 0.001813 * j2000) * j2000)
        * j2000 / 3600.0;

    x = Math.sin(x * PI_DIVIDED_BY_180)
        * Math.sin(PI_DIVIDED_BY_180 * ekliptik);

    return Math.atan(x / Math.sqrt(1.0 - x * x)) / PI_DIVIDED_BY_180 + 0.00075;
  }

  /**
   * Returns the Greenwich Hour Angle of the sun on a specific date and time.
   * 
   * If we consider as great circles of reference the celestial equator and the
   * hour circle (meridian) through Greenwich , the coordinates of the star are
   * its declination and Greenwich Hour Angle (G.H.A.)
   * 
   * The G.H.A. of a star is the angle between the meridian of Greenwich and the
   * meridian of the star measured westward from from the Greenwich meridian 0
   * to 360 degrees.
   * 
   * @param day
   *          Day of the month (1-31).
   * @param month
   *          Month of year (1-12, where January is 1 ).
   * @param year
   *          four digit year.
   * @param decimalHours
   *          The hour, minute and seconds, in a decimal notation ( 10:30:00 ==
   *          10.5 ).
   * @return Greenwich Hour Angle of the sun on a specific date and time.
   */
  private double computeGHA(int day, int month, int year, double decimalHours)
  {
    long n;
    double x, xx, p;

    n = 365 * year + day + 31 * month - 46;
    if (month < 3)
    {
      n = n + (int) ((year - 1) / 4);
    }
    else
    {
      n = n - (int) (0.4 * month + 2.3) + (int) (year / 4.0);
    }

    p = decimalHours / 24.0;
    x = (p + n - 7.22449E5) * 0.98564734 + 279.306;
    x = x * PI_DIVIDED_BY_180;
    xx = -104.55 * Math.sin(x) - 429.266 * Math.cos(x) + 595.63
        * Math.sin(2.0 * x) - 2.283 * Math.cos(2.0 * x);
    xx = xx + 4.6 * Math.sin(3.0 * x) + 18.7333 * Math.cos(3.0 * x);
    xx = xx - 13.2 * Math.sin(4.0 * x) - Math.cos(5.0 * x) - Math.sin(5.0 * x)
        / 3.0 + 0.5 * Math.sin(6.0 * x) + 0.231;
    xx = xx / 240.0 + 360.0 * (p + 0.5);
    if (xx > 360)
    {
      xx = xx - 360.0;
    }
    return xx;
  }

  /**
   * Calculates the lattitude of a location based on its longitude and a degree
   * offset (?)
   * 
   * @param longitude
   * @param dec
   * @return
   */
  private double computeLat(int longitude, double dec)
  {
    double tan, itan;

    tan = -Math.cos(longitude * PI_DIVIDED_BY_180)
        / Math.tan(dec * PI_DIVIDED_BY_180);
    itan = Math.atan(tan);
    itan = itan / PI_DIVIDED_BY_180;

    return itan;
  }

  /**
   * This method returns a buffered image with the contents of an image.
   * 
   * Example taken from the Java Developers Almanac 1.4.
   */
  private static BufferedImage toBufferedImage(Image image)
  {
    if (image instanceof BufferedImage)
    {
      return (BufferedImage) image;
    }

    // This code ensures that all the pixels in the image are loaded
    image = new ImageIcon(image).getImage();

    // Determine if the image has transparent pixels; for this method's
    // implementation, see e661 Determining If an Image Has Transparent Pixels
    boolean hasAlpha = hasAlpha(image);

    // Create a buffered image with a format that's compatible with the screen
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try
    {
      // Determine the type of transparency of the new buffered image
      int transparency = Transparency.OPAQUE;
      if (hasAlpha)
      {
        transparency = Transparency.BITMASK;
      }

      // Create the buffered image
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      bimage = gc.createCompatibleImage(image.getWidth(null), image
          .getHeight(null), transparency);
    }
    catch (HeadlessException e)
    {
      // The system does not have a screen
    }

    if (bimage == null)
    {
      // Create a buffered image using the default color model
      int type = BufferedImage.TYPE_INT_RGB;
      if (hasAlpha)
      {
        type = BufferedImage.TYPE_INT_ARGB;
      }
      bimage = new BufferedImage(image.getWidth(null), image.getHeight(null),
          type);
    }

    // Copy image to buffered image
    Graphics g = bimage.createGraphics();

    // Paint the image onto the buffered image
    g.drawImage(image, 0, 0, null);
    g.dispose();

    return bimage;
  }

  /**
   * This method returns true if the specified image has transparent pixels.
   * 
   * Example taken from the Java Developers Almanac 1.4.
   */
  private static boolean hasAlpha(Image image)
  {
    // If buffered image, the color model is readily available
    if (image instanceof BufferedImage)
    {
      BufferedImage bimage = (BufferedImage) image;
      return bimage.getColorModel().hasAlpha();
    }

    // Use a pixel grabber to retrieve the image's color model;
    // grabbing a single pixel is usually sufficient
    PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
    try
    {
      pg.grabPixels();
    }
    catch (InterruptedException e)
    {
    }

    // Get the image's color model
    ColorModel cm = pg.getColorModel();
    return cm.hasAlpha();
  }
}
