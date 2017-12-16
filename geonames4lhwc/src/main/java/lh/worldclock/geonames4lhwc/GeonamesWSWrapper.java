package lh.worldclock.geonames4lhwc;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import lh.worldclock.geonames.schema.Geonames;
import lh.worldclock.geonames.schema.Geonames.Geoname;
import lh.worldclock.geonames.schema.Geonames.Status;
import lh.worldclock.geonames.schema.ObjectFactory;

public class GeonamesWSWrapper
{

  public static void main(String[] args)
  {
    try
    {
      getGeonames("Paris", 20).stream().forEach((g) ->
      {
        System.out.println("name: " + g.getName());
        System.out.println("lat: " + g.getLat());
        System.out.println("lng: " + g.getLng());
        System.out.println("country: " + g.getCountryName());
        System.out.println("timezone: " + g.getTimezone());
     });
    }
    catch (Exception ex)
    {
      Logger.getLogger(GeonamesWSWrapper.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  
  private static final String JAXB_PACKAGE = ObjectFactory.class.getPackage().getName();
  private static final String URL_BASE = "http://api.geonames.org/search?q=";
  private static final String URL_STYLE = "&style=full";
  private static final String URL_MAX_ROWS = "&maxRows=";
   private static final String URL_USER = "&username=worldclockeditor";
 
  private static final GeonamesWSWrapper INSTANCE = new GeonamesWSWrapper();

  private final ObjectFactory factory = new ObjectFactory();
  private Geonames geonames = null;

  private GeonamesWSWrapper()
  {
  }

  public static List<Geoname> getGeonames(String location, int max)
  {
    return INSTANCE.doGetGeonames(location, max);
  }

  public static List<Geoname> getGeonamesFromPath(Path path)
  {
    return INSTANCE.doGetGeonamesFromPath(path);
  }


  private List<Geoname> doGetGeonames(String location, int max)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(URL_BASE);
    try
    {
      sb.append(URLEncoder.encode(location, "UTF-8"));
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
    sb.append(URL_USER);
    try(InputStream is = new URL(sb.toString()).openStream())
    {
      load(is);
    }
    catch (JAXBException | IOException ex)
    {
      Logger.getLogger(GeonamesWSWrapper.class.getName()).log(Level.FINE, null, ex);
      Geoname g = new Geoname();
      g.setName(ex.getMessage());
      return List.of(g);
    }
    return getGeonames();
  }

  public List<Geoname> doGetGeonamesFromPath(Path path)
  {
    try
    {
      load(path);
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
    if (ret.isEmpty())
    {
      Status status = geonames.getStatus();
      if (status != null)
      {
        Geoname g = new Geoname();
        g.setName(status.getMessage());
        ret = List.of(g);
      }
    }
    geonames = null;
    return ret;
  }

  private void load(Path path) throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE);
    Unmarshaller u = jc.createUnmarshaller();
    geonames = (Geonames) u.unmarshal(path.toFile());
  }

  private void load(InputStream is) throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE);
    Unmarshaller u = jc.createUnmarshaller();
    geonames = (Geonames) u.unmarshal(is);
    System.out.println("geonames="+geonames.getTotalResultsCount());
  }
}
