package lh.worldclock.saver;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.xml.parsers.*;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.xml.sax.*;
import org.xml.sax.helpers.*;


/**
 * <p>Title: ConfigLoader</p>
 *
 * <p>Description: Load a world clock configuration file, use the same format as world clock saver, hence the planes ;)</p>
 *
 * <p>Copyright: Copyright (c) 2005 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class ConfigLoader
{
  public ConfigLoader()
  {
  }


//  private static final String ROOT = "config";
  private static final String PLANES = "planes";
  private static final String PLANE = "plane";
  private static final String NAME = "name";
  private static final String HORIZONTAL = "horizontal";
  private static final String VERTICAL = "vertical";
  private static final String IMAGE = "image";
  private static final String CITIES = "cities";
  private static final String CITY = "city";
  private static final String LATITUDE = "lat";
  private static final String LONGITUDE = "long";
  private static final String TIMEZONE = "timezone";
  
//  private static final String NEUTRAL = "neutre";
  private static final String TOP = "haut";
  private static final String BOTTOM = "bas";
  private static final String LEFT = "gauche";
  private static final String RIGHT = "droite";

  private enum CurrentElement
  {
    DOCUMENT,
    PLANES,
    PLANE,
    CITIES,
    CITY
  }

  private class ConfigHandler extends DefaultHandler
  {
		private CurrentElement current = CurrentElement.DOCUMENT;
    private String name;
    private int horizontal;
    private int vertical;
    private String image;
    private URL imageURL = null;
    private Avion plane;
    private City city;
    private String cityName;
    private double latitude;
    private double longitude;
    private String timezone;
    
    private int getCodeFromString(String string)
    {
      int ret = Avion.NEUTRE;
      switch(string)
      {
        case TOP:
          ret = Avion.HAUT;
          break;
        case BOTTOM:
          ret = Avion.BAS;
          break;
        case LEFT:
          ret = Avion.GAUCHE;
          break;
        case RIGHT:
          ret = Avion.DROITE;
          break;
      }
      return ret;
    }
    
    private URL getImageURL(String imgpath)
    {
      try
      {
        Path configFile = Paths.get(filename);
        Path imageFile = configFile.resolveSibling(imgpath);
        return imageFile.toUri().toURL();
      }
      catch (MalformedURLException ex)
      {
        return null;
      }
    }
    
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
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
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
            name = attributes.getValue(NAME);
            horizontal = getCodeFromString(attributes.getValue(HORIZONTAL));
            vertical = getCodeFromString(attributes.getValue(VERTICAL));
            image = attributes.getValue(IMAGE);
            imageURL = getImageURL(image);
            if (imageURL != null)
            {
              plane = new Avion(name, imageURL, new Point(horizontal, vertical));
              planes.add(plane);
            }
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
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
      switch (current)
      {
        case PLANE:
          current = CurrentElement.PLANES;
          break;
        case PLANES:
          current = CurrentElement.DOCUMENT;
          break;
        case CITY:
          current = CurrentElement.CITIES;
          break;
        case CITIES:
          current = CurrentElement.DOCUMENT;
          break;
        default:
          current = CurrentElement.DOCUMENT;
      }
    }

    @Override
    public void endDocument() throws SAXException
    {
    }

  }

  private final List<Avion> planes = new ArrayList<>();
  private final List<City> cities = new ArrayList<>();
  private String filename;


  public void load(String filename)
  {
    try
    {
      this.filename = filename;
      SAXParserFactory parserFactory = SAXParserFactory.newInstance();
      parserFactory.setValidating(false);
      parserFactory.setNamespaceAware(true);
      parserFactory.setFeature("http://xml.org/sax/features/namespace-prefixes",true);
      parserFactory.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
      SAXParser parser = parserFactory.newSAXParser();
      parser.parse(new InputSource(Files.newBufferedReader(Paths.get(filename), Charset.forName("UTF-8"))), new ConfigHandler());
    }
    catch (IOException | ParserConfigurationException | SAXException ex)
    {
      ex.printStackTrace();
    }
  }

  public List<Avion> getPlanes()
  {
    return planes;
  }

  public List<City> getCities()
  {
    return cities;
  }
}
