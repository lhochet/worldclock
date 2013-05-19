package lh.worldclock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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
		Path dir = Paths.get(homedir, dirname);
		Path propfile = dir.resolve(filename);
		Properties props = new Properties();
		try (InputStream is = Files.newInputStream(propfile))
		{
			props.load(is);
		}
		catch (FileNotFoundException ex)
		{
			// No reason to abort program loading for this.
			System.out.println("Property file was not found: " + ex.getMessage());
		}
		configpath = props.getProperty("configpath", null);
	}

	public void save()
	{
    try
    {
      String homedir = System.getProperty("user.home");
      Path dir = Paths.get(homedir, dirname);
      if (Files.notExists(dir))
      {
        Files.createDirectories(dir);
      }
      Path propfile = dir.resolve(filename);
      Properties props = new Properties();
      if (configpath != null && !configpath.equals(""))
      {
        props.setProperty("configpath", configpath);
      }

      try (OutputStream os = Files.newOutputStream(propfile))
      {
        props.store(os, "World Clock Properties");
      }
    }
    catch(IOException ex)
    {
      ex.printStackTrace();
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
