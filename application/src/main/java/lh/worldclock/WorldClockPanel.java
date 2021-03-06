package lh.worldclock;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

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

  private java.util.List<City> cities = new java.util.ArrayList<>(0);

  public WorldClockPanel()
  {
    board = new WorldClockBoard();

    timer = new javax.swing.Timer(250, new ActionListener()
    {
//      private long last = 0;
      private final Clock minuteClock = Clock.tickMinutes(ZoneId.systemDefault());
      private Instant last = Instant.EPOCH;

      @Override
      public void actionPerformed(ActionEvent e)
      {
//        long now = System.currentTimeMillis() / 60000;
        Instant now = minuteClock.instant();
//        if (now > last)
        if (now.isAfter(last))
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
    if (path == null) {return;}
    if (path.equals("")) {return;}
    if (Files.notExists(Paths.get(path))) {return;}

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
    cities.stream().forEach((city) ->
    {
      city.paint(graphics, width, height, true);
    });
  }

  public void updateSize(int newWidth, int newHeight)
  {
    this.width = newWidth;
    this.height = newHeight;
    board.updateSizeValues(newWidth, newHeight);
  }

}
