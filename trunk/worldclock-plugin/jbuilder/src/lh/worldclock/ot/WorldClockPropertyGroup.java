package lh.worldclock.ot;

import com.borland.primetime.properties.*;

/**
 * <p>Title: WorldClockPropertyGroup</p>
 *
 * <p>Description: 'Group' the property for this OT, provides the property page factory<br/>
 * the config path is the path to the XML containing the cities description</p>
 *
 * <p>Copyright: Copyright (c) 2004 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/02/15 21:45:32 $
 */
public class WorldClockPropertyGroup implements PropertyGroup
{
  private static final String WORLD_CLOCK_CAT = "lh.worldclock";
  
  public static GlobalProperty configPath = new GlobalProperty(WORLD_CLOCK_CAT, "configpath");
  
  public WorldClockPropertyGroup()
  {
  }

  /**
   * getPageFactory
   *
   * @param topic Object
   * @return PropertyPageFactory
   */
  public PropertyPageFactory getPageFactory(Object topic)
  {
    // null: shows in the jbuilder preferences page
    if (topic == null)
    {
      return new PropertyPageFactory("World Clock")
      {
        public PropertyPage createPropertyPage()
        {
          return new WorldClockPropertyPage();
        }
      };
    }
    return null;
  }

  /**
   * initializeProperties
   */
  public void initializeProperties()
  {
    // does nothing here
  }
}
