package lh.worldclock.ot;

import java.text.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: City</p>
 *
 * <p>Description: Represents a city for the UI</p>
 *
 * <p>Copyright: Copyright (c) 2005 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/02/15 21:45:30 $
 */
public class City extends JComponent
{
  private static final int SZ = 5;
  
  private String name;
  private String tzName;
  private double latitude;
  private double longitude;
  

  
  public City(String name, double latitude, double longitude, String tzName)
  {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.tzName = tzName;
  }
  
  public String getName()
  {
    return name;
  }
  
  public double getLatitude()
  {
    return latitude;
  }
  
  public double getLongitude()
  {
    return longitude;
  }
  
  
  public void setRefenciel(int cx, int cy)
  {
    double cx2 = cx / 2;
    double cy2 = cy / 2;
    double lt = latitude * -1;
    int x = (int)(cx2 * longitude / 180 + cx2);
    int y = (int)(cy2 * lt / 90 + cy2);
    
    setBounds(x, y, SZ, SZ);
  }

  public void paintComponent(Graphics g)
  {
    g.setColor(Color.RED);
    g.fillOval(0, 0, SZ, SZ);
  }

  public String getToolTipText()
  {
    TimeZone tz = TimeZone.getTimeZone(tzName);
    Calendar cal = Calendar.getInstance(tz);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    sdf.setTimeZone(tz);
    return name + " " + sdf.format(cal.getTime());
  }
}
