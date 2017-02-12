module lh.worldclock.geonames4lhwc 
{
  exports lh.worldclock.geonames4lhwc;
  exports lh.worldclock.geonames.schema;
  opens lh.worldclock.geonames.schema;
  requires java.xml.bind;
  requires java.logging;
}
