package lh.worldclock.views;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import lh.worldclock.WorldClockPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.sun.corba.se.spi.activation.Activator;

/**
 * <p>Title: WorldClockView</p>
 * 
 * <p>Description: The view component for Eclipse, draws and update the board and
 * the cities loaded from the configuration file</p>
 * 
 * <p>Copyright: Copyright (c) 2006 Ludovic HOCHET</p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/04/13 20:50:56 $
 */
public class WoldClockView extends ViewPart
{
  private class BoardUpdateTask extends TimerTask
  {
    public void run()
    {
      board.getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          board.updateTimeValues();
          board.redraw();
        }
      });
    }

  }

  private WorlClockBoard board;

  private java.util.List<City> cities = new java.util.ArrayList<City>(0);

  private Timer timer;

  public WoldClockView()
  {
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize
   * it.
   */
  public void createPartControl(Composite parent)
  {
    String path = WorldClockPlugin.getDefault().getPreferenceStore().getString("worldclockFilename");
    loadConfig(path);

    board = new WorlClockBoard(parent);
    board.addPaintListener(new PaintListener()
    {
      public void paintControl(PaintEvent e)
      {
        Display display = board.getDisplay();
        Rectangle rect = board.getBounds();

        GC gc = e.gc;
        gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
        for (City city : cities)
        {
          city.setReferenciel(rect.width, rect.height);
          gc.fillOval(city.getX(), city.getY(), City.DIAMETRE, City.DIAMETRE);
        }
      }
    });

    board.addMouseMoveListener(new MouseMoveListener()
    {

      public void mouseMove(MouseEvent e)
      {
        setTitleToolTip(null);
        for (City city : cities)
        {
          if (city.contains(e.x, e.y))
          {
            board.setToolTipText(city.getToolTipText());
          }
        }
      }
    });

    timer = new Timer();
    timer.scheduleAtFixedRate(new BoardUpdateTask(), 0, 60000);
  }

  /**
   * Loads the configuration file
   * 
   * @param path
   *          Path to the configuration file
   */
  private void loadConfig(String path)
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

  /**
   * Passing the focus request to the viewer's control.
   */
  public void setFocus()
  {
    board.setFocus();
  }
}