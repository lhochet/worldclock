cd ..
rmdir /S /Q target\mods
mkdir target\mods
copy %USERPROFILE%\.m2\repository\org\jdesktop\appframework\1.0.3\appframework-1.0.3.jar target\mods
copy %USERPROFILE%\.m2\repository\org\jdesktop\swing-worker\1.1\swing-worker-1.1.jar target\mods
copy application\target\application-0.10-SNAPSHOT.jar target\mods
copy config\target\config-1.3-SNAPSHOT.jar target\mods
copy editor\target\editor-1.3-SNAPSHOT.jar target\mods
copy geonames4lhwc\target\geonames4lhwc-1.3-SNAPSHOT.jar target\mods
copy panel\target\panel-0.10-SNAPSHOT.jar target\mods
copy schema\target\schema-1.3-SNAPSHOT.jar target\mods