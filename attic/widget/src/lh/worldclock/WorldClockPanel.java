package lh.worldclock;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import java.util.Iterator;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

import javax.swing.ToolTipManager;
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
 * @version $Revision: 14 $ $Date: 2007-04-12 23:20:30 +0200 (Thu, 12 Apr 2007) $
 */
public class WorldClockPanel extends JPanel
{

  public static enum NextCityMethod
  {
    ITERATE,
    RANDOM
  }

  final WorldClockBoard board;
  private final Timer timer;
  private int width;
  private int height;
  private boolean isFullScreen = true;
  private java.util.List<City> cities = new java.util.ArrayList<City>(0);
  private Color textColour = Color.RED;

  private City currentCity = null;
  private NextCityMethod nextCityMethod = NextCityMethod.RANDOM;
  private Iterator<City> citiesIterator = null;
  private Random random = new Random();

  public WorldClockPanel()
  {
    board = new WorldClockBoard();

    timer = new javax.swing.Timer(250, new ActionListener()
    {

      private long last = 0;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        long now = System.currentTimeMillis() / 60000;
        if (now > last)
        {
          board.updateTimeValues();
          repaint();
          last = now;
          nextCity();
        }
      }
    });
    timer.start();

    width = getWidth();
    height = getHeight();
    board.updateSizeValues(width, height);

//    ToolTipManager.sharedInstance().registerComponent(this);
  }

  public void loadConfig(String path)
  {
    if (path == null)
    {
      return;
    }
    if (path.equals(""))
    {
      return;
    }
    File f = new File(path);
    if (!f.exists())
    {
      return;
    }

    ConfigLoader cl = new ConfigLoader();
    cl.load(path);

    cities = cl.getCities();
    citiesIterator = null;
    nextCity();
  }

  public void loadConfig(URL url)
  {
    if (url == null)
    {
      return;
    }

    ConfigLoader cl = new ConfigLoader();
    cl.load(url);

    cities = cl.getCities();
    citiesIterator = null;
    nextCity();
  }

  public void clearCities()
  {
    cities.clear();
    citiesIterator = null;
    currentCity = null;
  }

  public void setColour(Color textcolour)
  {
    this.textColour = textcolour;
  }

  public void setNextCityMethod(NextCityMethod nextCityMethod)
  {
    this.nextCityMethod = nextCityMethod;
  }

  @Override
  public void paintComponent(Graphics graphics)
  {
    board.paintComponent(graphics);

    Font f = graphics.getFont();
    graphics.setFont(f.deriveFont(10.0f));

    for (City city : cities)
    {
      city.paint(graphics, width, height, textColour, isFullScreen);
    }

    graphics.setFont(f);
  }

  public void updateSize(int newWidth, int newHeight)
  {
    this.width = newWidth;
    this.height = newHeight;
    board.updateSizeValues(newWidth, newHeight);
    isFullScreen = width > 320 && height > 200;
  }

//  @Override
//  public String getToolTipText(MouseEvent event)
//  {
//    if (!isFullScreen)
//    {
//      for (City city : cities)
//      {
//        if (city.contains(event.getPoint(), width, height))
//        {
//          return city.getTimeNameString();
//        }
//      }
//    }
//    return super.getToolTipText(event);
//  }

  public City getCity(int x, int y)
  {
      final Point p = new Point(x, y);
      for (City city : cities)
      {
        if (city.contains(p, width, height))
        {
          return city;
        }
      }
      return null;
  }

  public City getCurrentCity()
  {
    return currentCity;
  }

  private void nextCity()
  {
    if (nextCityMethod == NextCityMethod.RANDOM)
    {
      nextCityRandom();
    }
    else // Iterate
    {
      nextCityIterate();
    }
  }

  private void nextCityRandom()
  {
    if (cities.size() == 0) return;
    currentCity = cities.get(random.nextInt(cities.size()));
  }

  private void nextCityIterate()
  {
    if (cities.size() == 0) return;

    if (citiesIterator == null)
    {
      citiesIterator = cities.iterator();
    }
    if (citiesIterator.hasNext())
    {
      currentCity = citiesIterator.next();
    }
    else
    {
      citiesIterator = null;
      currentCity = null;
    }
  }
}
