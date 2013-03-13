package lh.worldclock;

import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.event.*;
import javax.swing.*;

import org.jdesktop.jdic.tray.*;

/**
 * <p>
 * Title: WorldClock
 * </p>
 * 
 * <p>
 * Description: The main class, creates the tray icon and shows the world clock
 * window
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
public class WorldClock
{
  public static final String APP_NAME = "World Clock";

  public static final String APP_VERSION = "0.6.1";

  private static ImageIcon icon = null;

  private static WorldClockFrame frame = null;

  private static URL url = null;

  private static ResourceBundle res = ResourceBundle.getBundle("lh/worldclock/worldclock");

  public static void main(String[] args)
  {
    // Try to set the theme of the application to the theme used by the
    // operating system.
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
    // ignore any problems. The program will run fine, even
    // if the system look and feel can't be used.
    }

    boolean showWindowOnStart = false;

    // handle parameters
    for (String arg : args)
    {
      if (arg.toUpperCase().startsWith("-URL="))
      {
        String surl = arg.substring(5);
        try
        {
          url = new URL(surl);
        }
        catch (MalformedURLException ex)
        {
          ex.printStackTrace();
        // keeps the url null
        }
      }
      else if (arg.equalsIgnoreCase("-show"))
      {
        showWindowOnStart = true;
      }
    }

    icon = new ImageIcon(WorldClock.class.getResource("icon.png"));

    try
    {
      PropsManager.getInstance().load();
    }
    catch (IOException ex)
    {
      // ignore these exceptions, as the application will run anyway. 
      // Users will miss any user specific settings, that's all.
      System.err.println("Failed to load user specific settings.");
      ex.printStackTrace();
    }
    JPopupMenu popup = createPopup();
    TrayIcon trayIcon = new TrayIcon(icon, APP_NAME, popup);
    trayIcon.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if ("PressAction".equals(e.getActionCommand()))
        {
          showWindow();
        }
      }
    });

    SystemTray systemTray = SystemTray.getDefaultSystemTray();
    systemTray.addTrayIcon(trayIcon);

    if (showWindowOnStart)
    {
      showWindow();
    }
  }

  private synchronized static void showWindow()
  {
    if (frame == null)
    {
      frame = new WorldClockFrame(icon);
      if (url == null)
      {
        frame.loadConfig(PropsManager.getInstance().getConfigPath());
      }
      else
      {
        frame.loadConfig(url);
      }
    }
    frame.setVisible(true);
  }

  private static JPopupMenu createPopup()
  {
    JPopupMenu popup = new JPopupMenu();

    JMenuItem mnuiShow = new JMenuItem(res.getString("SHOW_LBL"));
    mnuiShow.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        showWindow();
      }
    });
    popup.add(mnuiShow);

    JMenuItem mnuiShowOptions = new JMenuItem(res.getString("OPTIONS_LBL"));
    mnuiShowOptions.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        PropsManager props = PropsManager.getInstance();
        ConfigPanel panel = new ConfigPanel();
        panel.setConfigPath(props.getConfigPath());
        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, panel,
          res.getString("OPTIONS_LBL"), JOptionPane.OK_CANCEL_OPTION))
        {
          props.setConfigPath(panel.getConfigPath());
          props.save();
          frame = null;
        }
      }
    });
    popup.add(mnuiShowOptions);

    JMenuItem mnuiAbout = new JMenuItem(res.getString("ABOUT_LBL"));
    mnuiAbout.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        JOptionPane.showMessageDialog(null, APP_NAME + " " + APP_VERSION, res.getString("ABOUT_LBL"), JOptionPane.INFORMATION_MESSAGE);
      }
    });
    popup.add(mnuiAbout);

    popup.addSeparator();

    JMenuItem mnuiExit = new JMenuItem(res.getString("EXIT_LBL"));
    mnuiExit.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.exit(0);
      }
    });
    popup.add(mnuiExit);

    return popup;
  }
}
