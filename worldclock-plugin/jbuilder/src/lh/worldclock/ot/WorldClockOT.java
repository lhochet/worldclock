package lh.worldclock.ot;

import com.borland.jbuilder.*;
import com.borland.primetime.*;
import com.borland.primetime.ide.*;
import com.borland.primetime.ide.view.*;
import com.borland.primetime.ide.workspace.*;
import com.borland.primetime.properties.*;
import net.java.dev.jbuilder.opentools.community.*;

/**
 * <p>Title: WorldClock OpenTool</p>
 *
 * <p>Description: Core JB class, registers everything</p>
 *
 * <p>Copyright: Copyright (c) 2004 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/02/15 21:45:31 $
 */
public class WorldClockOT
{
  private static class WorldClockViewStateAction extends BrowserStateAction
  {
    View view = null;

    public WorldClockViewStateAction()
    {
      super("World Clock");
    }
    public void setState(Browser browser, boolean state)
    {
      Workspace workspace = WorkspaceManager.getWorkspace(browser);
      if (view == null)
      {
        View[] views = workspace.getViews(WorldClockViewType.ID);
        if (views.length == 1)
        {
          view = views[0];
        }
        else
        {
          ViewType viewtype = new WorldClockViewType(browser.getProjectView().selectProjectTreeViewPage().getViewType());
          ViewManager.registerViewFactory(new WorldClockViewFactory(viewtype));
          view = ViewManager.createView(browser, WorldClockViewType.ID);
        }
      }

      if (state)
      {
        workspace.openView(view);
        workspace.setViewActive(view);
      }
      else
      {
        workspace.removeView(view);
        view = null;
      }
    }

    public boolean getState(Browser browser)
    {
      Workspace workspace = WorkspaceManager.getWorkspace(browser);
      if (view == null)
      {
        View[] views = workspace.getViews(WorldClockViewType.ID);
        if (views.length == 1)
        {
          view = views[0];
        }
      }

      if (view != null)
      {
        return workspace.isViewVisible(view);
      }
      return false;
    }
  }

  private static BrowserStateAction STATE_WorldClockView = new WorldClockViewStateAction();

  public static void initOpenTool(byte majorVersion, byte minorVersion)
  {
    if (PrimeTime.isVerbose())
    {
      System.out.println("LH WorldClockOT vrs " + VersionUtil.getVersionString(WorldClockOT.class));
      System.out.println("  Compiled with JB OTAPI " + PrimeTime.CURRENT_MAJOR_VERSION + "." + PrimeTime.CURRENT_MINOR_VERSION);
      System.out.println("  Excuted with JB OTAPI " + majorVersion + "." + minorVersion);
    }

    // setup action
    JBuilderMenu.GROUP_ViewIdePanes.add(STATE_WorldClockView);

    // setup view factory that will create views at the default location, same place as message view
    ViewType viewtype = new WorldClockViewType();
    ViewManager.registerViewFactory(new WorldClockViewFactory(viewtype));
    
    PropertyManager.registerPropertyGroup(new WorldClockPropertyGroup());
  }

}
