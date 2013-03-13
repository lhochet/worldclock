/*
 * WorldClockPanelController.java
 *
 * Created on 25 February 2006, 01:00
 */

package lh.worldclockmodule;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;


/**
 * Options controler, loads and save the path into/from the option panel
 * @author Ludovic HOCHET
 * @version $Revision: 1.3 $ $Date: 2006/05/08 22:02:46 $
 */
public class WorldClockPanelController extends OptionsPanelController
{
    private WorldClockOptionsPanel panel = new WorldClockOptionsPanel();
    private String path = "";
    
    /** Creates a new instance of WorldClockPanelController */
    public WorldClockPanelController()
    {
    }

    public void update()
    {
        path = WorldClockProperties.getInstance().getPath();
        panel.setPath(path);
    }

    public void applyChanges()
    {
        WorldClockProperties.getInstance().setPath(panel.getPath());
    }

    public void cancel()
    {
    }

    public boolean isValid()
    {
       return true; // always true -> an empty path is ok
    }

    public boolean isChanged()
    {
        return path.equals(panel.getPath());
    }

    public JComponent getComponent(Lookup masterLookup)
    {
        return panel;
    }

    public HelpCtx getHelpCtx()
    {
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
    }
    
}
