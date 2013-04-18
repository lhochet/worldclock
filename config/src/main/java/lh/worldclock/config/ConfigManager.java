package lh.worldclock.config;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import lh.worldclock.config.schema.City;
import lh.worldclock.config.schema.Config;
import lh.worldclock.config.schema.Config.Cities;
import lh.worldclock.config.schema.Config.Planes;
import lh.worldclock.config.schema.ObjectFactory;
import lh.worldclock.config.schema.Plane;

/**
 *
 * @author Ludovic
 */
public class ConfigManager
{
  private static final String JAXB_PACKAGE = Config.class.getPackage().getName();

  private final ObjectFactory factory;
  private Config config;

  public ConfigManager()
  {
    factory = new ObjectFactory();
    config = factory.createConfig();
  }

  public void load(Path path) throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE);
    Unmarshaller u = jc.createUnmarshaller();
    config = (Config)u.unmarshal(path.toFile());
  }

  public void load(URL url) throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE);
    Unmarshaller u = jc.createUnmarshaller();
    config = (Config)u.unmarshal(url);
  }

  public List<City> getCities()
  {
    Cities cities = config.getCities();
    if (cities == null)
    {
      cities = factory.createConfigCities();
      config.setCities(cities);
    }
    return cities.getCity();
  }

  public List<Plane> getPlanes()
  {
    Planes planes = config.getPlanes();
    if (planes == null)
    {
      planes = factory.createConfigPlanes();
      config.setPlanes(planes);
    }
    return planes.getPlane();
  }

  public City createCity()
  {
    return factory.createCity();
  }

  public Plane createPlane()
  {
    return factory.createPlane();
  }

  public void save(Path path) throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE);
    Marshaller m = jc.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    m.marshal(config, path.toFile());
  }

}
