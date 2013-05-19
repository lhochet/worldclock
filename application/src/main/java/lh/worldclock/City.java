package lh.worldclock;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
//	private final TimeZone tz;

//	private final Calendar cal;

//	private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
  private final DateTimeFormatter formatter;

	private final String name;

	private final double latitude;

	private final double longitude;

	/**
	 * Creates a new city object.
	 * 
	 * @param name
	 *          Name of the city.
	 * @param latitude
	 *          The distance north or south of the equator.
	 * @param longitude
	 *          The distance east or west of the prime meridian.
	 * @param tzName
	 *          Name of the timezone of this city.
	 */
	public City(String name, double latitude, double longitude, String tzName)
	{
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;

//		tz = TimeZone.getTimeZone(tzName);
//		cal = Calendar.getInstance(tz);
//		sdf.setTimeZone(tz);
    formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of(tzName));
	}

	/**
	 * Returns the name of this city.
	 * 
	 * @return City name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the latitude of this city. The latitude is the distance of this
	 * city north or south of the equator.
	 * 
	 * @return latitude of this city.
	 */
	public double getLatitude()
	{
		return latitude;
	}

	/**
	 * Returns the longitude of this city. The longitude is the distance east or
	 * west of the prime meridian.
	 * 
	 * @return longitude of this city
	 */
	public double getLongitude()
	{
		return longitude;
	}

	/**
	 * Draws this city on the graphics object specified.
	 * 
	 * @param g
	 *          Graphics object on which this city instance is being drawed.
	 * @param width
	 *          Pixel width of the Graphics object.
	 * @param height
	 *          Pixel height of the Graphics object.
	 * @param isFullScreen
	 *          ''true'' if the Graphics object fills the entire screen.
	 */
	public void paint(Graphics g, int width, int height, boolean isFullScreen)
	{
		final double cx2 = width / 2.0;
		final double cy2 = height / 2.0;
		final double lt = latitude * -1;
		int x = (int) (cx2 * longitude / 180 + cx2);
		int y = (int) (cy2 * lt / 90 + cy2);

		g.setColor(Color.RED);
		g.fillOval(x - 1, y - 1, 3, 3);
		if (isFullScreen)
		{
//      cal.setTime(new Date());
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
