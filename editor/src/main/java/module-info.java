module lh.worldclock.editor 
{
  exports lh.worldclock.editor to appframework;
  opens lh.worldclock.editor.resources;
  opens lh.worldclock.editor.resources.busyicons;
  requires lh.worldclock.config;
  requires lh.worldclock.geonames4lhwc;
  requires appframework;
  requires swing.worker;
  requires java.desktop;
  requires java.logging;
  requires java.xml.bind;
}
