package lh.worldclock.views;

import java.text.*;
import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;


/**
 * <p>Title: City</p>
 *
 * <p>Description: Represents a city for the UI</p>
 *
 * <p>Copyright: Copyright (c) 2005-2006 Ludovic HOCHET</p>
 *
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/04/13 20:50:56 $
 */
public class City
{
  public static final int DIAMETRE = 5;
  
  private String name;
  private String tzName;
  private double latitude;
  private double longitude;
  
  private int x;
  private int y;
  

  
  public City(String name, double latitude, double longitude, String tzName)
  {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.tzName = tzName;
  }
  
  public String getName()
  {
    return name;
  }
  
  public double getLatitude()
  {
    return latitude;
  }
  
  public double getLongitude()
  {
    return longitude;
  }
  
  

  public String getToolTipText()
  {
    TimeZone tz = TimeZone.getTimeZone(tzName);
    Calendar cal = Calendar.getInstance(tz);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    sdf.setTimeZone(tz);
    return name + " " + sdf.format(cal.getTime());
  }
  
  public void setReferenciel(int cx, int cy)
  {
    double cx2 = cx / 2;
    double cy2 = cy / 2;
    
    double lt = getLatitude() * -1;
    x = (int)(cx2 * getLongitude() / 180 + cx2);
    y = (int)(cy2 * lt / 90 + cy2);
  }
  
  public int getX()
  {
    return x;
  }
  
  public int getY()
  {
    return y;
  }
  
  public boolean contains(int mx, int my)
  {
    if (x <= mx && mx <= x + DIAMETRE)
    {
      if (y <= my && my <= y + DIAMETRE)
      {
        return true;
      }
    }
    return false;
  }
}
