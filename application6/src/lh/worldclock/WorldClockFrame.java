package lh.worldclock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * <p>
 * Title: WorldClockFrame
 * </p>
 * 
 * <p>
 * Description: Shows the world clock board, hides the window when 'closed'
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005 Ludovic HOCHET
 * </p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class WorldClockFrame extends JFrame
{
  private WorldClockPanel pane = new WorldClockPanel();

  public WorldClockFrame(ImageIcon icon)
  {
    setTitle(WorldClock.APP_NAME);
    setIconImage(icon.getImage());
    setLayout(new BorderLayout());
    add(pane, BorderLayout.CENTER);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds(0, 0, screenSize.width, screenSize.height);
    pane.updateSize(screenSize.width, screenSize.height);

    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    addComponentListener(new java.awt.event.ComponentAdapter()
    {
      @Override
	public void componentResized(ComponentEvent e)
      {
        pane.updateSize(getWidth(), getHeight());
      }
    });

  }

  public void loadConfig(String path)
  {
    pane.loadConfig(path);
  }

  public void loadConfig(URL url)
  {
    pane.loadConfig(url);
  }
}
