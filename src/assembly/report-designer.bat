@echo off
setlocal
set "BASE=%~dp0"
set "CP=%BASE%patch\${project.build.finalName}.jar;%BASE%patch\flatlaf.jar;%BASE%lib\*"
set "JAVA_EXE=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
"%JAVA_EXE%" --add-opens java.desktop/javax.swing.plaf.basic=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED -cp "%CP%" org.pentaho.reporting.designer.core.ReportDesigner %*
