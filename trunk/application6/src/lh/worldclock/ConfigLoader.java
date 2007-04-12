package lh.worldclock;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * Title: ConfigLoader
 * </p>
 * 
 * <p>
 * Description: Load a world clock configuration file, use the same format as
 * world clock saver, hence the planes ;)
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005 Ludovic HOCHET
 * </p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class ConfigLoader
{
	// private static final String ROOT = "config";
	private static final String PLANES = "planes";

	private static final String PLANE = "plane";

	private static final String NAME = "name";

	// private static final String HORIZONTAL = "horizontal";
	// private static final String VERTICAL = "vertical";
	// private static final String IMAGE = "image";
	private static final String CITIES = "cities";

	private static final String CITY = "city";

	private static final String LATITUDE = "lat";

	private static final String LONGITUDE = "long";

	private static final String TIMEZONE = "timezone";

	// private static final String NEUTRAL = "neutre";
	// private static final String TOP = "haut";
	// private static final String BOTTOM = "bas";
	// private static final String LEFT = "gauche";
	// private static final String RIGHT = "droite";

	private static final int C_DOC = 0;

	private static final int C_PLANES = 1;

	private static final int C_PLANE = 2;

	private static final int C_CITIES = 3;

	private static final int C_CITY = 4;

	private class ConfigHandler extends DefaultHandler
	{
		private int cur = C_DOC;

		private City city;

		private String cityName;

		private double latitude;

		private double longitude;

		private String timezone;

		private double getDoubleFromString(String str)
		{
			try
			{
				return Double.parseDouble(str);
			}
			catch (NumberFormatException ex)
			{
				return 0.0;
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) 
		{
			switch (cur)
			{
			case C_DOC:
				{
					if (localName.equals(PLANES))
					{
						cur = C_PLANES;
					}
					else if (localName.equals(CITIES))
					{
						cur = C_CITIES;
					}
					break;
				}
			case C_PLANES:
				{
					if (localName.equals(PLANE))
					{
						cur = C_PLANE;
					}
					break;
				}
			case C_CITIES:
				{
					if (localName.equals(CITY))
					{
						cur = C_CITY;
						cityName = attributes.getValue(NAME);
						latitude = getDoubleFromString(attributes.getValue(LATITUDE));
						longitude = getDoubleFromString(attributes.getValue(LONGITUDE));
						timezone = attributes.getValue(TIMEZONE);
						if (cityName != null && !cityName.equals(""))
						{
							city = new City(cityName, latitude, longitude, timezone);
							cities.add(city);
						}
					}
					break;
				}
			default:
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			switch (cur)
			{
				case C_PLANE:
					cur = C_PLANES;
					break;
					
				case C_CITY:
					cur = C_CITIES;
					break;
					
				default:
					cur = C_DOC;
			}
		}

		@Override
		public void endDocument()
		{
		}

	}

	private List<City> cities = new ArrayList<City>();

	public void load(String filename)
	{
		try
		{
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(false);
			parserFactory.setNamespaceAware(true);
			parserFactory.setFeature(
					"http://xml.org/sax/features/namespace-prefixes", true);
			parserFactory.setFeature(
					"http://apache.org/xml/features/continue-after-fatal-error", true);
			SAXParser parser = parserFactory.newSAXParser();
			parser.parse(new InputSource(new FileReader(new File(filename))),
					new ConfigHandler());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void load(URL url)
	{
		try
		{
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(false);
			parserFactory.setNamespaceAware(true);
			parserFactory.setFeature(
					"http://xml.org/sax/features/namespace-prefixes", true);
			parserFactory.setFeature(
					"http://apache.org/xml/features/continue-after-fatal-error", true);
			SAXParser parser = parserFactory.newSAXParser();
			URLConnection conn = url.openConnection();
			parser.parse(new InputSource(new BufferedInputStream(conn
					.getInputStream())), new ConfigHandler());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public List<City> getCities()
	{
		return cities;
	}
}
