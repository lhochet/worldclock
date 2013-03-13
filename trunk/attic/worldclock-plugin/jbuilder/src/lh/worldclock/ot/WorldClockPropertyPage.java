package lh.worldclock.ot;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.borland.primetime.help.*;
import com.borland.primetime.properties.*;

/**
 * <p>Title: WorldClockPropertyPage</p>
 *
 * <p>Description: The property page for this OT, allows to select the configuration file location</p>
 *
 * <p>Copyright: Copyright (c) 2004 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/02/15 21:45:32 $
 */
public class WorldClockPropertyPage extends PropertyPage
{
  public WorldClockPropertyPage()
  {
    try
    {
      jbInit();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * getHelpTopic
   *
   * @return HelpTopic
   */
  public HelpTopic getHelpTopic()
  {
    return null;
  }

  /**
   * readProperties
   *
   */
  public void readProperties()
  {
    tfPath.setText(WorldClockPropertyGroup.configPath.getValue());
  }

  /**
   * writeProperties
   *
   */
  public void writeProperties()
  {
    WorldClockPropertyGroup.configPath.setValue(tfPath.getText());
  }

  private void jbInit() throws Exception
  {
    jLabel1.setText("Configuration file:");
    this.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    tfPath.setColumns(30);
    btnBrowse.setText("...");
    btnBrowse.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        btnBrowse_actionPerformed(e);
      }
    });
    this.add(jLabel1);
    this.add(tfPath);
    this.add(btnBrowse);
  }

  private JLabel jLabel1 = new JLabel();
  private FlowLayout flowLayout1 = new FlowLayout();
  private JTextField tfPath = new JTextField();
  private JButton btnBrowse = new JButton();
  public void btnBrowse_actionPerformed(ActionEvent e)
  {
    String p = tfPath.getText();
    File dir = new File(p).getParentFile();
    JFileChooser jfc = new JFileChooser(dir);
    if (jfc.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION)
    {
      File f = jfc.getSelectedFile();
      if (f != null)
      {
        tfPath.setText(f.getAbsolutePath());
      }
    }
  }
}
