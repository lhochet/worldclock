module lh.worldclock.schema 
{
  exports lh.worldclock.config.schema;
  opens lh.worldclock.config.schema to java.xml.bind;
  requires java.xml.ws.annotation;
  requires java.xml.bind;
}
