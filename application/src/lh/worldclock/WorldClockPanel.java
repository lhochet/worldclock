package lh.worldclock;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import lh.worldclock.core.*;
import java.net.*;

/**
 * <p>
 * Title: WorldClockPanel
 * </p>
 * 
 * <p>
 * Description: Paint and refresh the board and cities
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005-2007 The WorldClock Application Team
 * </p>
 * 
 * @author Ludovic HOCHET
 * @author Guus der Kinderen
 * @version $Revision$ $Date$
 */
public class WorldClockPanel extends JPanel
{
  private WorldClockBoard board = null;

  private int width;

  private int height;

  private java.util.List<City> cities = new java.util.ArrayList<City>(0);

  private Timer timer;

  public WorldClockPanel()
  {
    board = new WorldClockBoard();

    timer = new javax.swing.Timer(250, new ActionListener()
    {
      private long last = 0;

      public void actionPerformed(ActionEvent e)
      {
        long now = System.currentTimeMillis() / 60000;
        if (now > last)
        {
          board.updateTimeValues();
          repaint();
          last = now;
        }
      }
    });
    timer.start();

    width = getWidth();
    height = getHeight();
    board.updateSizeValues(width, height);
    board.updateTimeValues();

  }

  public void loadConfig(String path)
  {
    if (path == null)
      return;
    if (path.equals(""))
      return;
    File f = new File(path);
    if (!f.exists())
      return;

    ConfigLoader cl = new ConfigLoader();
    cl.load(path);

    cities = cl.getCities();
  }

  public void loadConfig(URL url)
  {
    if (url == null)
      return;

    ConfigLoader cl = new ConfigLoader();
    cl.load(url);

    cities = cl.getCities();
  }

  public void paintComponent(Graphics graphics)
  {
    board.paintComponent(graphics);

    for (City city : cities)
    {
      city.paint(graphics, width, height, true);
    }
  }

  public void updateSize(int width, int height)
  {
    this.width = width;
    this.height = height;
    board.updateSizeValues(width, height);
  }

}
