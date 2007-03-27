package lh.worldclock;

import java.io.*;
import java.util.*;

/**
 * <p>
 * Title: PropsManager
 * </p>
 * 
 * <p>
 * Description: Load and save the property file containing the path to the
 * configuration file ;0
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005 Ludovic HOCHET
 * </p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision$ $Date$
 */
public class PropsManager
{
	private static final PropsManager instance = new PropsManager();

	public static PropsManager getInstance()
	{
		return instance;
	}

	private static final String dirname = ".worldclock";

	private static final String filename = "worldclock.properties";

	private String configpath;

	public void load() throws IOException
	{
		String homedir = System.getProperty("user.home");
		File dir = new File(homedir, dirname);
		File propfile = new File(dir, filename);
		Properties props = new Properties();
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(propfile);
			props.load(fis);
		}
		catch (FileNotFoundException ex)
		{
			// No reason to abort program loading for this.
			System.out.println("Property file was not found: " + ex.getMessage());
		}
		finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		configpath = props.getProperty("configpath", null);
	}

	public void save()
	{
		String homedir = System.getProperty("user.home");
		File dir = new File(homedir, dirname);
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		File propfile = new File(dir, filename);
		Properties props = new Properties();
		if (configpath != null && !configpath.equals(""))
		{
			props.setProperty("configpath", configpath);
		}

		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(propfile);
			props.store(fos, "World Clock Properties");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void setConfigPath(String configpath)
	{
		this.configpath = configpath;
	}

	public String getConfigPath()
	{
		return configpath;
	}

}
