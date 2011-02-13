/*
 * WorldClockProperties.java
 *
 * Created on 25 February 2006, 15:15
 */

package lh.worldclockmodule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 * Properties manager, inpired from the gmail Netbeans Module tutorial
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.3 $ $Date: 2006/05/08 22:02:48 $
 */
public class WorldClockProperties
{
    private static WorldClockProperties instance = null;
    public static WorldClockProperties getInstance()
    {
        if (instance == null)
        {
            instance = new WorldClockProperties();
        }
        return instance;
    }
    
    private FileObject folderObject = null;
    private FileObject settingFile = null;
    private Properties properties = null;
    
    private WorldClockProperties()
    {
        folderObject= Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Settings");
        if (folderObject==null)
        {
            try
            {
                folderObject=Repository.getDefault().getDefaultFileSystem().getRoot().createFolder("Settings");
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                // TODO file can not be created , do something about it
            }
        }
        try
        {
            settingFile= folderObject.getFileObject("worldclock","properties");
            if (settingFile == null)
            {
                settingFile= folderObject.createData("worldclock","properties");
            }
            
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            
        }
    }
    
    public String getPath()
    {
        if (properties == null)
        {
            properties = new Properties();
            try
            {
                properties.load(settingFile.getInputStream());
            }
            catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return properties.getProperty("path", "");
    }
    
    public void setPath(String path)
    {
        if (properties == null)
        {
            properties = new Properties();
        }
        properties.setProperty("path", path);
        FileLock lock = null;
        try
        {
            lock = settingFile.lock();
            properties.store(settingFile.getOutputStream(lock), "LH World Clock Properties");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (lock != null) lock.releaseLock();
        }
    }
    
}
