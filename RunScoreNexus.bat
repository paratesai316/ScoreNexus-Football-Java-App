@echo off
rem Compiling Java source files
javac -d . -cp ".;lib/sqlite-jdbc-3.43.0.0.jar" src/com/scorenexus/main/ScoreNexusApp.java src/com/scorenexus/model/*.java src/com/scorenexus/ui/*.java src/com/scorenexus/db/*.java src/com/scorenexus/util/*.java

rem Running the Java application
java -cp ".;lib/sqlite-jdbc-3.43.0.0.jar" com.scorenexus.main.ScoreNexusApp

pause
