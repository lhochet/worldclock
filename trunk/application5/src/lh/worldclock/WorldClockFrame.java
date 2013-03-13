package lh.worldclock;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;

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
 * Copyright: Copyright (c) 2005-2007 The WorldClock Application Team
 * </p>
 * 
 * @author Ludovic HOCHET
 * @author Guus der Kinderen
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

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    addComponentListener(new java.awt.event.ComponentAdapter()
    {
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
