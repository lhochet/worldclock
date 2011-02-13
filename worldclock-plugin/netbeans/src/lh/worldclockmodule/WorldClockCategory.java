/*
 * WorldClockCategory.java
 *
 * Created on 25 February 2006, 00:54
 */

package lh.worldclockmodule;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

/**
 * Options category, displays in the Miscellaneous section
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.3 $ $Date: 2006/05/08 22:02:45 $
 */
public class WorldClockCategory extends AdvancedOption
{
    public WorldClockCategory()
    {
    }

    public String getDisplayName()
    {
        return NbBundle.getMessage(WorldClockCategory.class, "CTL_WorldClock");
    }

    public String getTooltip()
    {
        return NbBundle.getMessage(WorldClockCategory.class, "CTL_WorldClock");
    }

    public OptionsPanelController create()
    {
        return new WorldClockPanelController();
    }
}
