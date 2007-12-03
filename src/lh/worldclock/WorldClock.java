package lh.worldclock;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
 * Copyright: Copyright (c) 2005-2006 Ludovic HOCHET
 * </p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class WorldClock
{
  public static final String APP_NAME = "World Clock";

  public static final String APP_VERSION = "0.6";

  private static ImageIcon icon = null;

  static WorldClockFrame frame = null;

  private static URL url = null;

  static ResourceBundle res = ResourceBundle.getBundle("lh/worldclock/worldclock");

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
    PopupMenu popup = createPopup();
    TrayIcon trayIcon = new TrayIcon(icon.getImage(), APP_NAME, popup);
    trayIcon.setImageAutoSize(true);
//    trayIcon.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        if ("PressAction".equals(e.getActionCommand()))
//        {
//          showWindow();
//        }
//      }
//    });

    trayIcon.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e)
      {
        if (SwingUtilities.isLeftMouseButton(e))
        {
          showWindow();
        }
      }
    });

    if (SystemTray.isSupported())
    {
      SystemTray systemTray = SystemTray.getSystemTray();
      try
      {
        systemTray.add(trayIcon);
      }
      catch (AWTException ex)
      {
        ex.printStackTrace();
      }
    }

    if (showWindowOnStart)
    {
      showWindow();
    }
  }

  synchronized static void showWindow()
  {
    if (frame == null)
    {
      frame = new WorldClockFrame(icon);
      
      // if there is no system tray, then closig the window should also exit the application
      // otherwise keeps the default (hide on close)
      if (SystemTray.isSupported())
      {
        frame.setExitOnClose(true);
      }
      
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

  private static PopupMenu createPopup()
  {
    PopupMenu popup = new PopupMenu();

    MenuItem mnuiShow = new MenuItem(res.getString("SHOW_LBL"));
    mnuiShow.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        showWindow();
      }
    });
    popup.add(mnuiShow);

    MenuItem mnuiShowOptions = new MenuItem(res.getString("OPTIONS_LBL"));
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

    MenuItem mnuiAbout = new MenuItem(res.getString("ABOUT_LBL"));
    mnuiAbout.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        JOptionPane.showMessageDialog(null, APP_NAME + " " + APP_VERSION, res.getString("ABOUT_LBL"), JOptionPane.INFORMATION_MESSAGE);
      }
    });
    popup.add(mnuiAbout);

    popup.addSeparator();

    MenuItem mnuiExit = new MenuItem(res.getString("EXIT_LBL"));
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
