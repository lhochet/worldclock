package lh.worldclock;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * <p>Title: WorldClockPlugin</p>
 * 
 * <p>Description: The 'plugin' class</p>
 * 
 * <p>Copyright: Copyright (c) 2006 Ludovic HOCHET</p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/04/13 20:50:56 $
 */
public class WorldClockPlugin extends AbstractUIPlugin
{

  // The shared instance.
  private static WorldClockPlugin plugin;

  /**
   * The constructor.
   */
  public WorldClockPlugin()
  {
    plugin = this;
  }

  /**
   * This method is called upon plug-in activation
   */
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
  }

  /**
   * This method is called when the plug-in is stopped
   */
  public void stop(BundleContext context) throws Exception
  {
    super.stop(context);
    plugin = null;
  }

  /**
   * Returns the shared instance.
   * 
   * @return the shared instance.
   */
  public static WorldClockPlugin getDefault()
  {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in
   * relative path.
   * 
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path)
  {
    return AbstractUIPlugin.imageDescriptorFromPlugin("worldclock", path);
  }
}
