package lh.worldclock.ot;

import javax.swing.*;

import com.borland.primetime.ide.*;
import com.borland.primetime.ide.view.*;

/**
 * <p>Title: WorldClockViewFactory</p>
 *
 * <p>Description: Factory for the world clock view</p>
 *
 * <p>Copyright: Copyright (c) 2004 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/02/15 21:45:33 $
 */
public class WorldClockViewFactory extends DefaultViewFactory
{
  public WorldClockViewFactory(ViewType type)
  {
    super(type);
  }

  protected JComponent createComponent(Browser browser)
  {
    return new WorldClockViewComponent();
  }
}
