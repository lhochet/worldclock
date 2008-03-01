package lh.worldclock.desklet;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import lh.worldclock.City;
import lh.worldclock.ConfigPanel;
import lh.worldclock.WorldClockPanel;
import lh.worldclock.core.*;
import org.glossitope.desklet.DeskletContainer;
import org.glossitope.desklet.test.DeskletTester;

/**
 *
 * @author Ludovic
 * @version $Revision$ $Date$
 */
public class WorldclockDesklet extends org.glossitope.desklet.Desklet
{

  public static final String APP_NAME = "World Clock Desklet";
  public static final String APP_VERSION = "0.3";
  private static final String FILE_PATH = "FILE_PATH";
  private ResourceBundle res = ResourceBundle.getBundle("lh/worldclock/worldclock");
  private WorldClockPanel pane = new WorldClockPanel();
  private JLabel sidePane = new JLabel();
  private ConfigPanel configPane = new ConfigPanel();


  /** Creates a new instance of WorldclockDesklet */
  public WorldclockDesklet()
  {
  }

  public void destroy() throws Exception
  {
    if (configPane != null)
    {
      getContext().setPreference(FILE_PATH, configPane.getConfigPath());
    }
  }

  public void stop() throws Exception
  {
    if (configPane != null)
    {
      getContext().setPreference(FILE_PATH, configPane.getConfigPath());
    }
    this.getContext().notifyStopped();
  }

  public void start() throws Exception
  {
    configPane.setConfigPath(getContext().getPreference(FILE_PATH, ""));
    loadConfig();
  }
  
  private void loadConfig()
  {
    pane.loadConfig(configPane.getConfigPath());
    pane.repaint();
    
    StringBuilder sb = new StringBuilder();
    sb.append("<html><body>");
    for (City city: pane.getCities())
    {
      sb.append(city.getName()).append(": ").append(city.getCurrentFormattedTime()).append("<br>");
    }
    sb.append("</body></html>");
    sidePane.setText(sb.toString());
  }

  private JPopupMenu createMenu()
  {
    final JPopupMenu mnu = new JPopupMenu();
    JMenuItem configItem = new JMenuItem(res.getString("OPTIONS_LBL"));
    configItem.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnu.setVisible(false);
        configPane.setConfigPath(getContext().getPreference(FILE_PATH, ""));
        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(pane, configPane,
          res.getString("OPTIONS_LBL"), JOptionPane.OK_CANCEL_OPTION))
        {
          getContext().setPreference(FILE_PATH, configPane.getConfigPath());
          loadConfig();
        }
      }
    });
    mnu.add(configItem);

    JMenuItem aboutItem = new JMenuItem(res.getString("ABOUT_LBL"));
    aboutItem.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnu.setVisible(false);
        JOptionPane.showMessageDialog(pane, APP_NAME + " " + APP_VERSION, res.getString("ABOUT_LBL"), JOptionPane.INFORMATION_MESSAGE);
      }
    });
    mnu.add(aboutItem);

    return mnu;
  }

  public void init() throws Exception
  {
    // This is the main ui for your desklet.
    DeskletContainer main = getContext().getContainer();
    main.setContent(pane);
    Dimension d = new Dimension(600, 400);
    pane.setSize(d);

    pane.addMouseListener(new MouseAdapter()
    {

      private JPopupMenu menu = createMenu();

      @Override
      public void mouseClicked(MouseEvent e)
      {
        if (SwingUtilities.isRightMouseButton(e))
        {
//          menu.setLocation(e.getLocationOnScreen()); // this is Java 6 or above, desklet are supposed to be for Java 5+
          Point p = e.getPoint();
          SwingUtilities.convertPointToScreen(p, pane);
          menu.setLocation(p);
          menu.setVisible(true);
        }
      }
    });

    main.setSize(d);

    // This is the configuration ui for setting preferences, etc.
//    DeskletContainer config = getContext().getConfigurationContainer();

    // This is the ui for your desklet on the dock bar.
    DeskletContainer dock = getContext().getDockingContainer();
    dock.setContent(sidePane);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
    DeskletTester.start(WorldclockDesklet.class);
  }
}
