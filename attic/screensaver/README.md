# Worldclock

## Screensaver

A World clock screen saver based on the Java SaverBean Screensaver 
SDK. In addition to the clocks, the screen saver also allows for UFOs.

### Downloads

* for "unix" plateforms:

    *   [worldclocksaver-0.8-unix.tar.gz](https://dl.bintray.com/lhochet/Worldclock/worldclocksaver-0.8-unix.tar.gz)
*   for "windows" plateforms:

    *   [worldclocksaver-0.8-win32.zip](https://dl.bintray.com/lhochet/Worldclock/worldclocksaver-0.8-win32.zip)
    *   [worldclocksaver-0.8-setup.exe](https://dl.bintray.com/lhochet/Worldclock/worldclocksaver-0.8-setup.exe) (installer)

### Troubleshooting

On Windows, if the screen saver complain of a missing msvcr100.dll, 
then copy the one that's in C:\Programs Files\Java\jre7\bin to 
C:\windows\system32

## Configuration

Both the application and the screensaver require a configuration file.
That file is a simple XML file that can be easily edited via a text editor or via the [editor](#editor) application.
Basic structure of the file:

```xml
 <config>
   <planes>
     <plane name="ufo" horizontal="gauche" vertical="haut" image="pathto/ufo.png"/>
   </planes>
   <cities>
     <city name="Tours" lat="47.27" long="0.43" timezone="Europe/Paris"/>
   </cities>
 </config>

```

Planes:

*   name: name of the plane

*   horizontal: direction of the horizontal movement of the plane, gauche: towards the left, droite: towards the right

*   vertical: direction of the vertical movement of the plane, haut: towards the top, bas: towards the bottom

*   image: plane's image path relative to the configuration file

Cities:

*   lat: latitude, positive values: north, negative values: south

*   long: longitude, positive values: east, negative values: west

*   timezone: time zone in which the city is located (list)

See the [config.xml](config.xml) for some samples.


## Team

*   [Ludovic Hochet](https://github.com/lhochet)
*   [Guus der Kinderen](https://github.com/guusdk)

## Credits

*   Jürgen Giesen, for his [Day & Night Applet](http://www.geoastro.de/TNApplet/DN/index.html) that serves as the base for the world clock board

*   NASA, for the Earth pictures (more at [NASA's Visible Earth](http://visibleearth.nasa.gov/) website)
