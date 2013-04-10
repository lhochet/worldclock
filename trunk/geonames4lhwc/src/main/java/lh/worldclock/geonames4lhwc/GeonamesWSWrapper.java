package lh.worldclock.geonames4lhwc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import lh.worldclock.geonames.schema.Geonames;
import lh.worldclock.geonames.schema.Geonames.Geoname;
import lh.worldclock.geonames.schema.ObjectFactory;

public class GeonamesWSWrapper
{

  public static void main(String[] args)
  {
    try
    {
//      for (Geoname g : getGeonamesFromFile(new File("geonames.xml")))
      for (Geoname g : getGeonames("Paris", 20))
      {
        System.out.println("name: " + g.getName());
        System.out.println("lat: " + g.getLat());
        System.out.println("lng: " + g.getLng());
        System.out.println("country: " + g.getCountryName());
        System.out.println("timezone: " + g.getTimezone());
      }
    }
    catch (Exception ex)
    {
      Logger.getLogger(GeonamesWSWrapper.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  
  private static final String JAXB_PACKAGE = ObjectFactory.class.getPackage().getName();
  private static final String URL_BASE = "http://ws.geonames.org/search?q=";
  private static final String URL_STYLE = "&style=full";
  private static final String URL_MAX_ROWS = "&maxRows=";
  
  private static final GeonamesWSWrapper instance = new GeonamesWSWrapper();

  private final ObjectFactory factory = new ObjectFactory();
  private Geonames geonames = null;

  private GeonamesWSWrapper()
  {
  }

  public static List<Geoname> getGeonames(String location, int max)
  {
    return instance.doGetGeonames(location, max);
  }

  public static List<Geoname> getGeonamesFromFile(File file)
  {
    return instance.doGetGeonamesFromFile(file);
  }


  private List<Geoname> doGetGeonames(String location, int max)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(URL_BASE);
    try
    {
      sb.append(URLEncoder.encode(location, "UTF8"));
    }
    catch (UnsupportedEncodingException ex)
    {
      Logger.getLogger(GeonamesWSWrapper.class.getName()).log(Level.FINE, null, ex);
      // try without encoding
      sb.append(location);
    }
    sb.append(URL_STYLE);
    sb.append(URL_MAX_ROWS);
    sb.append(Integer.toString(max));
    try
    {
      load(new URL(sb.toString()).openStream());
    }
    catch (JAXBException | IOException ex)
    {
      Logger.getLogger(GeonamesWSWrapper.class.getName()).log(Level.FINE, null, ex);
    }
    return getGeonames();
  }

  public List<Geoname> doGetGeonamesFromFile(File file)
  {
    try
    {
      load(file);
    }
    catch (JAXBException ex)
    {
      Logger.getLogger(GeonamesWSWrapper.class.getName()).log(Level.FINE, null, ex);
    }
    return getGeonames();
  }

  private List<Geoname> getGeonames()
  {
    if (geonames == null)
    {
      geonames = factory.createGeonames();
    }
    List<Geoname> ret = geonames.getGeoname();
    geonames = null;
    return ret;
  }

  private void load(File file) throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE);
    Unmarshaller u = jc.createUnmarshaller();
    geonames = (Geonames) u.unmarshal(file);
  }

  private void load(InputStream is) throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE);
    Unmarshaller u = jc.createUnmarshaller();
    geonames = (Geonames) u.unmarshal(is);
  }
}
