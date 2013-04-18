package lh.worldclock.core;

import java.util.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * <p>Title: WorldClockBoard</p>
 *
 * <p>Description: Draws the Earth day and night</p>
 * This part is directly based on Jürgen Giesen's <a href="http://www.geoastro.de/TNApplet/DN/index.html">Day & Night Applet</a><br/>
 * Images are from <a href="http://visibleearth.nasa.gov/">NASA's Visible Earth</a> website !
 *
 * <p>Copyright: Copyright (c) 2004-2006 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.2 $ $Date: 2006/05/08 22:02:06 $
 */
public class WorldClockBoard extends JComponent
{
  // earth
  ImageIcon earthImage;
  ImageIcon scaledEarthImage;
  ImageIcon earthNightImage;
  ImageIcon scaledEarthNightImage;
  Image clippedImage;

  int cx = 0;
  int cy = 0;

  // drawing
  int offx = 0; // offset
  int offy = 0;
  int meridien0 = 0;
  int equator = 90 + offx;
  int Radius = 6;
  int xMinEquator = offx;
  int xMaxEquator = cx;
  int xMeridien = offx + meridien0;
  int yMinMeridien = offy;
  int yMaxMeridien = offy + cy;
  int yArticPolar = (int)Math.round(equator - 66.5 * ((double)equator / 90));
  int yCancer = (int)Math.round(equator - 23.5 * ((double)equator / 90));
  int yCapricorn = (int)Math.round(equator + 23.5 * ((double)equator / 90));
  int yAnarticPolar = (int)Math.round(equator + 66.5  * ((double)equator / 90));
  double xStep = (double)cx / 360;
  double yStep = (double)equator / 90;

  // click pos
  int xMouse = -1, yMouse = -1;

  // time
  public int locOffset = 0;
  int year, month, day, hours, minutes, seconds;
  double STD;

  // astro maths
  double latitude, longitude, declination, GHA, sunHight = 0.0;
  double K = Math.PI / 180.0;
  double GHA12, equation, diff, azimuth;
  int min;
  int xSun360;

  public WorldClockBoard()
  {
    earthImage = new ImageIcon(WorldClockBoard.class.getResource("world.jpg"));
    scaledEarthImage = earthImage;
    earthNightImage = new ImageIcon(WorldClockBoard.class.getResource("world_night.jpg"));
    scaledEarthNightImage = earthNightImage;
    cx = earthImage.getIconWidth();
    cy = earthImage.getIconHeight();
    setSize(cx, cy);

    updateSizeValues();

    final Component c = this;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public double getSunHeight()
  {
    return sunHight;
  }

  public void updateSizeValues()
  {
    Dimension dim = getSize();
    updateSizeValues(dim.width, dim.height);
  }
  public void updateSizeValues(int _cx, int _cy)
  {
    cx = _cx;
    if (cx < 1) return;
    cy = _cy;
    if (cy < 1) return;
    if ((cx != earthImage.getIconWidth()) || (cy != earthImage.getIconHeight()))
    {
      AffineTransform tx = new AffineTransform();
      tx.scale((double)cx / earthImage.getIconWidth(), (double)cy / earthImage.getIconHeight());
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

      BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration().createCompatibleImage(earthImage.getIconWidth(),earthImage.getIconHeight(), Transparency.BITMASK);
      Graphics g = img.getGraphics();
      g.drawImage(earthImage.getImage(), 0, 0, null);
      g.dispose();
      scaledEarthImage.setImage(op.filter(img, null));

      g = img.getGraphics();
      g.drawImage(earthNightImage.getImage(), 0, 0, null);
      g.dispose();

      scaledEarthNightImage.setImage(op.filter(img, null));


    }
   clippedImage =  GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration().createCompatibleImage(cx,cy, Transparency.BITMASK);

    meridien0 = cx / 2;
    equator = cy / 2;

    //  update related
    xMinEquator = offx;
    xMaxEquator = cx;
    xMeridien = offx + meridien0;
    yMinMeridien = offy;
    yMaxMeridien = offy + cy;

    yArticPolar = (int)Math.round(equator - 66.5 * ((double)equator / 90));
    yCancer = (int)Math.round(equator - 23.5 * ((double)equator / 90));
    yCapricorn = (int)Math.round(equator + 23.5 * ((double)equator / 90));
    yAnarticPolar = (int)Math.round(equator + 66.5  * ((double)equator / 90));

    xStep = (double)cx / 360;
    yStep = (double)equator / 90;
    
    if (cx < 400 || cy < 400)
    {
      Radius = 2;
    }
    else
    {
      Radius = 5;
    }
  }

  public void updateTimeValues()
  {
    // time
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    day = cal.get(Calendar.DAY_OF_MONTH);
    month = cal.get(Calendar.MONTH);
    year = cal.get(Calendar.YEAR);
    hours = cal.get(Calendar.HOUR_OF_DAY);
    minutes = cal.get(Calendar.MINUTE);
    seconds = cal.get(Calendar.SECOND);

    STD = 1.0 * (hours - locOffset) + minutes / 60.0 + seconds / 3600.0;


    // compute Declination
    declination = computeDeclination(day, month+1, year/*+1900*/, STD);

    // compute Greenwich Hour Angle
    GHA = computeGHA(day, month + 1, year /*+1900*/, STD);

    // compute equation of time
    GHA12 = computeGHA(day, month + 1, year /*+1900*/, 12.0);
    if (GHA12 > 5.0)
    {
      GHA12 = GHA12 - 360.0;
    }
    equation = GHA12 * 4.0; // Minutes

    diff = Math.abs(equation - (int)equation);
    min = (int)Math.round(diff*60.0);

    // sun location
    xSun360 = 180 - (int)Math.round(GHA);
    if (xSun360 < 0)
    {
      xSun360 += 360;
    }
    if (xSun360 > 360)
    {
      xSun360 -= 360;
    }
  }

  public void paintComponent(Graphics g)
  {
    int xx;

    Graphics g2 = clippedImage.getGraphics();

    // light
    scaledEarthImage.paintIcon(this, g2, offy, offx);

    // shadow
    int yMinShadow = 0;
    if (declination > 0)
    {
      yMinShadow = cy;
    }
    else
    {
      yMinShadow = 0;
    }

    int yy = 0, yy1 = 0;
    Polygon polyline = new Polygon();
    polyline.addPoint(0, yMinShadow);

    for (int i = -xSun360; xSun360 + i < 360; i++)
    {
      xx = offx + (xSun360 + i);
      xx = (int)((double)xx * xStep);
      yy = (int)((double)computeLat(i, declination) * yStep);
      yy1 = (int)((double)computeLat(i + 1, declination) * yStep);

      polyline.addPoint(xx, equator - yy);
    }
    polyline.addPoint(cx, equator - yy);
    polyline.addPoint(cx, equator - yy1);
    polyline.addPoint(cx, yMinShadow);

    g2.setClip(polyline);
    g2.drawImage(scaledEarthNightImage.getImage(), 0, 0, this);
    g2.dispose();
    g.drawImage(clippedImage, 0, 0, this);


    // equator lines and meridien
    g.setColor(Color.RED);
    // equator
    g.drawLine(xMinEquator, equator, xMaxEquator, equator);
    // meridien
    g.drawLine(xMeridien, yMinMeridien, xMeridien, yMaxMeridien);

    // parallels
    xx = xMinEquator;
    while (xx < cx)
    {
      g.setColor(Color.PINK);
      g.drawLine(xx, yArticPolar, xx + 2, yArticPolar);
      g.setColor(Color.RED);
      g.drawLine(xx, yCancer, xx + 2, yCancer);
      g.drawLine(xx, yCapricorn, xx + 2, yCapricorn);
      g.setColor(Color.PINK);
      g.drawLine(xx, yAnarticPolar, xx + 2, yAnarticPolar);
      xx = xx + 6;
    }

    // sun location
    int xSun = xSun360 * cx / 360;
    int ySun = equator - (int)Math.round(declination);

    g.setColor(Color.YELLOW);
    g.fillOval(xSun - Radius, ySun - Radius, 2 * Radius, 2 * Radius);

    g.setColor(Color.RED);
    g.drawOval(offy + xSun - Radius - 1, ySun - Radius - 1, 2 * Radius + 2, 2 * Radius + 2);

  }

  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////

  private double computeDeclination(int T, int M, int J, double STD)
  {

    long N;
    double X;
    double Ekliptik, J2000;

    N = 365 * J + T + 31 * M - 46;
    if (M < 3)
    {
      N = N + (int)((J - 1) / 4);
    }
    else
    {
      N = N - (int)(0.4 * M + 2.3) + (int)(J / 4.0);
    }

    X = (N - 693960) / 1461.0;
    X = (X - (int)X) * 1440.02509 + (int)X * 0.0307572;
    X = X + STD / 24.0 * 0.9856645 + 356.6498973;
    X = X + 1.91233 * Math.sin(0.9999825 * X * K);
    X = (X + Math.sin(1.999965 * X * K) / 50.0 + 282.55462) / 360.0;
    X = (X - (int)X) * 360.0;

    J2000 = (J - 2000) / 100.0;
    Ekliptik = 23.43929111 - (46.8150 + (0.00059 - 0.001813 * J2000) * J2000) * J2000 / 3600.0;

    X = Math.sin(X * K) * Math.sin(K * Ekliptik);

    return Math.atan(X / Math.sqrt(1.0 - X * X)) / K + 0.00075;
  }

  private double computeGHA(int T, int M, int J, double STD)
  {

    long N;
    double X, XX, P, NN;

    N = 365 * J + T + 31 * M - 46;
    if (M < 3)
    {
      N = N + (int)((J - 1) / 4);
    }
    else
    {
      N = N - (int)(0.4 * M + 2.3) + (int)(J / 4.0);
    }

    P = STD / 24.0;
    X = (P + N - 7.22449E5) * 0.98564734 + 279.306;
    X = X * K;
    XX = -104.55 * Math.sin(X) - 429.266 * Math.cos(X) + 595.63 * Math.sin(2.0 * X) - 2.283 * Math.cos(2.0 * X);
    XX = XX + 4.6 * Math.sin(3.0 * X) + 18.7333 * Math.cos(3.0 * X);
    XX = XX - 13.2 * Math.sin(4.0 * X) - Math.cos(5.0 * X) - Math.sin(5.0 * X) / 3.0 + 0.5 * Math.sin(6.0 * X) + 0.231;
    XX = XX / 240.0 + 360.0 * (P + 0.5);
    if (XX > 360)
    {
      XX = XX - 360.0;
    }
    return XX;
  }

  private int computeLat(int longitude, double dec)
  {

    double tan, itan;

    tan = -Math.cos(longitude * K) / Math.tan(dec * K);
    itan = Math.atan(tan);
    itan = itan / K;

    return (int)Math.round(itan);
  }

}
