set OPENJDK=C:\softs\openjdk-image

cd target

rmdir /S /Q swing-worker-1.1
mkdir swing-worker-1.1
cd swing-worker-1.1
"%OPENJDK%\bin\jar" --extract --file ..\..\target\mods\swing-worker-1.1.jar
cd ..

"%OPENJDK%\bin\javac" -d swing-worker-1.1 ..\modextra\swingworker\module-info.java 
"%OPENJDK%\bin\jar" --update --file mods\swing-worker-1.1.jar -C swing-worker-1.1 module-info.class 

rmdir /S /Q appframework-1.0.3
mkdir appframework-1.0.3
cd appframework-1.0.3
"%OPENJDK%\bin\jar" --extract --file ..\..\target\mods\appframework-1.0.3.jar
cd ..

"%OPENJDK%\bin\javac" -p mods -d appframework-1.0.3 ..\modextra\appframework\module-info.java 
"%OPENJDK%\bin\jar" --update --file mods\appframework-1.0.3.jar -C appframework-1.0.3 module-info.class 

rmdir /S /Q app
"%OPENJDK%\bin\jlink" --output app --module-path mods;"%OPENJDK%\jmods" --add-modules lh.worldclock.application --launcher worldclock=lh.worldclock.application/lh.worldclock.WorldClock 

rmdir /S /Q editor
"%OPENJDK%\bin\jlink" --output editor --module-path mods;"%OPENJDK%\jmods" --add-modules lh.worldclock.editor --launcher editor=lh.worldclock.editor/lh.worldclock.editor.EditorApp 

cd ..