package lh.worldclock.ot;

import com.borland.primetime.ide.view.ViewType;

/**
 * <p>Title: WorldClockViewType</p>
 *
 * <p>Description: View type for the world clock view</p>
 *
 * <p>Copyright: Copyright (c) 2004 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/02/15 21:45:34 $
 */
public class WorldClockViewType extends ViewType
{
  public static final String ID = "worldclock-view";
  private static final String NAME = "World Clock";

  public WorldClockViewType()
  {
    super(null, ID, NAME, null, false);
  }

  public WorldClockViewType(ViewType parent)
  {
    super(parent, ID, NAME, null, false);
  }
}
