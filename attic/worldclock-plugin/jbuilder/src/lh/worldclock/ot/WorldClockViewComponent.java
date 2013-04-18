package lh.worldclock.ot;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import lh.worldclock.core.*;

/**
 * <p>Title: WorldClockViewComponent</p>
 *
 * <p>Description: The view component for JB, draws and update the board and the cities loaded from the configuration file</p>
 *
 * <p>Copyright: Copyright (c) 2004 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.2 $ $Date: 2006/04/13 20:50:57 $
 */
public class WorldClockViewComponent extends JPanel
{
  private WorldClockBoard board = new WorldClockBoard();
  private javax.swing.Timer timer;
  
  private java.util.List<City> cities = new java.util.ArrayList<City>(0);


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
    
    
    loadConfig(WorldClockPropertyGroup.configPath.getValue());
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
