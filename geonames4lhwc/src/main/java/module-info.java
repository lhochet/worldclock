module lh.worldclock.geonames4lhwc 
{
  exports lh.worldclock.geonames4lhwc;
  exports lh.worldclock.geonames.schema;
  opens lh.worldclock.geonames.schema to java.xml.bind;
  requires java.logging;
  requires java.xml.bind;
}
