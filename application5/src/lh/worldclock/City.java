package lh.worldclock;

import java.text.*;
import java.util.*;

import java.awt.*;

/**
 * <p>
 * Title: City
 * </p>
 * 
 * <p>
 * Description: Represents a city for the UI
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005-2007 The WorldClock Application Team
 * </p>
 * 
 * @author Ludovic HOCHET
 * @author Guus der Kinderen
 * @version $Revision$ $Date$
 */
public class City
{
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

  public void paint(Graphics g, int cx, int cy, boolean isFullScreen)
  {
    double cx2 = cx / 2.0;
    double cy2 = cy / 2.0;
    double lt = latitude * -1;
    int x = (int) (cx2 * longitude / 180 + cx2);
    int y = (int) (cy2 * lt / 90 + cy2);

    g.setColor(Color.RED);
    g.fillOval(x - 1, y - 1, 3, 3);
    if (isFullScreen)
    {
      TimeZone tz = TimeZone.getTimeZone(tzName);
      Calendar cal = Calendar.getInstance(tz);
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
      sdf.setTimeZone(tz);
      g.drawString(name + " " + sdf.format(cal.getTime()), x + 3, y + 1);
    }
  }

}
