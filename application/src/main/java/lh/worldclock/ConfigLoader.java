package lh.worldclock;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
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
  
  private enum CurrentElement
  {
    DOCUMENT,
    PLANES,
    PLANE,
    CITIES,
    CITY
  }


	class ConfigHandler extends DefaultHandler
	{
		private CurrentElement current = CurrentElement.DOCUMENT;

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
			switch (current)
			{
        case DOCUMENT:
          {
            switch (localName)
            {
              case PLANES:
                current = CurrentElement.PLANES;
                break;
              case CITIES:
                current = CurrentElement.CITIES;
                break;
            }
            break;
          }
        case PLANES:
          {
            if (localName.equals(PLANE))
            {
              current = CurrentElement.PLANE;
            }
            break;
          }
        case CITIES:
          {
            if (localName.equals(CITY))
            {
              current = CurrentElement.CITY;
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
			switch (current)
			{
				case PLANE:
					current = CurrentElement.PLANES;
					break;
					
				case CITY:
					current = CurrentElement.CITIES;
					break;
					
				default:
					current = CurrentElement.DOCUMENT;
			}
		}

		@Override
		public void endDocument()
		{
			// not used.
		}

	}

	List<City> cities = new ArrayList<>();

	public void load(String filename)
	{
		try
		{
			doLoad(new InputSource(Files.newBufferedReader(Paths.get(filename), Charset.forName("UTF-8"))));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void load(URL url)
	{
		try
		{
      URLConnection conn = url.openConnection();
      try (InputStream is =conn.getInputStream())
      {
        doLoad(new InputSource(new BufferedInputStream(is)));
      }
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
  
  private void doLoad(InputSource source)
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
			parser.parse(source, new ConfigHandler());
		}
		catch (IOException | ParserConfigurationException | SAXException ex)
		{
			ex.printStackTrace();
		}
  }

	public List<City> getCities()
	{
		return cities;
	}
}
