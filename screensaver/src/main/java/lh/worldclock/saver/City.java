package lh.worldclock.saver;

import java.text.*;
import java.util.*;

import java.awt.*;

/**
 * <p>Title: City</p>
 *
 * <p>Description: Represents a city for the UI</p>
 *
 * <p>Copyright: Copyright (c) 2005-2006 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class City
{
  private final String name;
  private final double latitude;
  private final double longitude;

  private final TimeZone tz;
  private Calendar cal;
  private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
  
  public City(String name, double latitude, double longitude, String tzName)
  {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    tz = TimeZone.getTimeZone(tzName);
    sdf.setTimeZone(tz);
    cal = Calendar.getInstance(tz);
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
  
  public void paint(Graphics g, int cx, int cy, boolean isFullScreen)
  {
    double cx2 = cx / 2;
    double cy2 = cy / 2;
    double lt = latitude * -1;
    int x = (int)(cx2 * longitude / 180 + cx2);
    int y = (int)(cy2 * lt / 90 + cy2);
    
    g.setColor(Color.RED);
    g.fillOval(x - 1, y - 1, 3, 3);    
    if (isFullScreen)
    {
      cal = Calendar.getInstance(tz);
      g.drawString(name + " " + sdf.format(cal.getTime()), x + 3, y + 1);
    }
  }

}
