rmdir /S /Q mods
mkdir mods
copy %USERPROFILE%\.m2\repository\org\jdesktop\appframework\1.0.3\appframework-1.0.3.jar mods
copy %USERPROFILE%\.m2\repository\org\jdesktop\swing-worker\1.1\swing-worker-1.1.jar mods
copy application\target\application-0.8-SNAPSHOT.jar mods
copy config\target\config-1.1-SNAPSHOT.jar mods
copy editor\target\editor-1.1-SNAPSHOT.jar mods
copy geonames4lhwc\target\geonames4lhwc-1.1-SNAPSHOT.jar mods
copy panel\target\panel-0.8-SNAPSHOT.jar mods
copy schema\target\schema-1.1-SNAPSHOT.jar mods