package lh.worldclockmodule;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows WorldClock component.
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.3 $ $Date: 2006/05/08 22:02:44 $
 */
public class WorldClockAction extends AbstractAction
{
    
    public WorldClockAction()
    {
        super(NbBundle.getMessage(WorldClockAction.class, "CTL_WorldClockAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(WorldClockTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt)
    {
        TopComponent win = WorldClockTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
