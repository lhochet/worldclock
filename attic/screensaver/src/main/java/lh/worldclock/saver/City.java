package lh.worldclock.saver;


import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

//  private final TimeZone tz;
//  private Calendar cal;
//  private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
  private final DateTimeFormatter formatter;
  
  public City(String name, double latitude, double longitude, String tzName)
  {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
//    tz = TimeZone.getTimeZone(tzName);
//    sdf.setTimeZone(tz);
//    cal = Calendar.getInstance(tz);
    formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of(tzName));
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
  
  public void paint(Graphics g, int width, int height, boolean isFullScreen)
  {
    double cx2 = width / 2;
    double cy2 = height / 2;
    double lt = latitude * -1;
    int x = (int)(cx2 * longitude / 180 + cx2);
    int y = (int)(cy2 * lt / 90 + cy2);
    
    g.setColor(Color.RED);
    g.fillOval(x - 1, y - 1, 3, 3);    
    if (isFullScreen)
    {
//      cal = Calendar.getInstance(tz);
//      final String time = sdf.format(cal.getTime());
      final String time = formatter.format(Instant.now());
      final StringBuilder sb = new StringBuilder(name.length() + 1 + time.length());
      final String stringToDraw = sb.append(name).append(' ').append(time).toString();
      
      // basic attempt at repositioning the city string if it goes outside of the window
      final FontMetrics fm = g.getFontMetrics();
      final int stringWidth = fm.stringWidth(stringToDraw);     
      // + 10 to avoid the screen/window edge
      if (x + 3 + stringWidth + 10> width)
      {
        x = x - (x + 25 + stringWidth - width);
        y = y - 5;
      }      
      
			g.drawString(stringToDraw, x + 3, y + 1);
    }
  }

}
