@echo off
set OLD_DIR=%CD%
set PROJ4J_HOME=%~dp0..%
set JAVA_OPTS=-Xms256M -Xmx512M

set CLASSPATH=
set LIB=%PROJ4J_HOME%\lib

for %%i in ("%LIB%\*.jar") do call cpAppend %%i

java -cp "%CLASSPATH%" %JAVA_OPTS% org.osgeo.proj4j.CoordinateTransformationTest %*

