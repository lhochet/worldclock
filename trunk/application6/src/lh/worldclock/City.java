package lh.worldclock;

import java.awt.Color;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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
 * Copyright: Copyright (c) 2005-2006 Ludovic HOCHET
 * </p>
 * 
 * @author Ludovic HOCHET
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
