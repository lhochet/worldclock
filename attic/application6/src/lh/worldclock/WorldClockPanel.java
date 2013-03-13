package lh.worldclock;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.Timer;

import lh.worldclock.core.WorldClockBoard;

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
 * Copyright: Copyright (c) 2004-2006 Ludovic HOCHET
 * </p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class WorldClockPanel extends JPanel
{
  final WorldClockBoard board;

  private final Timer timer;

  private int width;

  private int height;

  private java.util.List<City> cities = new java.util.ArrayList<City>(0);

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

  @Override
public void paintComponent(Graphics graphics)
  {
    board.paintComponent(graphics);

    for (City city : cities)
    {
      city.paint(graphics, width, height, true);
    }
  }

  public void updateSize(int newWidth, int newHeight)
  {
    this.width = newWidth;
    this.height = newHeight;
    board.updateSizeValues(newWidth, newHeight);
  }

}
