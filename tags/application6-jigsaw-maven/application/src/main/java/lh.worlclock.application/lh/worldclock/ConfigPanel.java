package lh.worldclock;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <p>
 * Title: ConfigPanel
 * </p>
 * 
 * <p>
 * Description: UI to select the path to the configuration file
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005-2006 Ludovic HOCHET
 * </p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision: 8 $ $Date: 2007-04-12 22:36:00 +0200 (Thu, 12 Apr 2007) $
 */
public class ConfigPanel extends JPanel
{
	private ResourceBundle res = ResourceBundle
			.getBundle("lh/worldclock/worldclock");

	public ConfigPanel()
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

	private void jbInit() throws Exception
	{
		jLabel1.setText(res.getString("CONFIG_FILE_LBL"));
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
		if (jfc.showDialog(this, res.getString("CONFIG_SELECT_LBL")) == JFileChooser.APPROVE_OPTION)
		{
			File f = jfc.getSelectedFile();
			if (f != null)
			{
				tfPath.setText(f.getAbsolutePath());
			}
		}
	}

	public void setConfigPath(String path)
	{
		tfPath.setText(path);
	}

	public String getConfigPath()
	{
		return tfPath.getText();
	}
}
