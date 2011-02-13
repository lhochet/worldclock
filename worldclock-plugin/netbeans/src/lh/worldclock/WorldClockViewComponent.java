package lh.worldclock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.awt.event.MouseEvent;
import lh.worldclock.core.WorldClockBoard;
import lh.worldclockmodule.WorldClockProperties;

/**
 * <p>Title: WorldClockViewComponent</p>
 *
 * <p>Description: The view component for Netbeans, draws and update the board and the cities loaded from the configuration file</p>
 *
 * <p>Copyright: Copyright (c) 2004-2006 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.2 $ $Date: 2006/05/08 22:02:41 $
 */
public class WorldClockViewComponent extends JPanel
{
  private WorldClockBoard board = new WorldClockBoard();
  private javax.swing.Timer timer;
  
  private java.util.List<City> cities = new java.util.ArrayList(0);


  public WorldClockViewComponent()
  {
    timer = new javax.swing.Timer(60000, new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        board.updateTimeValues();
        repaint();
      }
    });
    timer.start();
    
    
   loadConfig(WorldClockProperties.getInstance().getPath());
    for (City city: cities)
    {
      add(city);
      ToolTipManager.sharedInstance().registerComponent(city);
    }
    
  }
  
  public void paintComponent(Graphics g)
  {
    Dimension d = getSize();
    board.updateSizeValues(d.width, d.height);
    for (City city: cities)
    {
      city.setRefenciel(d.width, d.height);
    }

    board.updateTimeValues();
    
    board.paintComponent(g);

  }
  
  private void loadConfig(String path)
  {
    if (path == null) return;
    if (path.equals("")) return;
    File f = new File(path);
    if (!f.exists()) return;

    ConfigLoader cl = new ConfigLoader();
    cl.load(path);


    cities = cl.getCities();

  }


}
