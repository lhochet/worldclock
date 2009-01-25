package lh.worldclock;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
 * @version $Revision: 20 $ $Date: 2007-11-28 23:45:43 +0100 (Wed, 28 Nov 2007) $
 */
public class City
{
	private final TimeZone tz;

	private final Calendar cal;

	private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

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

		tz = TimeZone.getTimeZone(tzName);
		cal = Calendar.getInstance(tz);
		sdf.setTimeZone(tz);
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
	public void paint(Graphics g, int width, int height, Color colour, boolean isFullScreen)
	{
		final double cx2 = width / 2.0;
		final double cy2 = height / 2.0;
		final double lt = latitude * -1;
		final int x = (int) (cx2 * longitude / 180 + cx2);
		final int y = (int) (cy2 * lt / 90 + cy2);

		g.setColor(colour);
		g.fillOval(x - 1, y - 1, 3, 3);
		if (isFullScreen)
		{
      cal.setTime(new Date());
			g.drawString(sdf.format(cal.getTime()) + " " + name, x + 3, y + 1);
		}
	}

  public boolean contains(Point p, int width, int height)
  {
		final double cx2 = width / 2.0;
		final double cy2 = height / 2.0;
		final double lt = latitude * -1;
		final int x = (int) (cx2 * longitude / 180 + cx2) - 2;
		final int y = (int) (cy2 * lt / 90 + cy2) - 2;

    return p.x >= x && p.x <= x + 4 && p.y >= y && p.y <= y + 4;
  }

  public String getNameTimeString()
  {
    cal.setTime(new Date());
		return sdf.format(cal.getTime()) + " " + name;

  }

}
