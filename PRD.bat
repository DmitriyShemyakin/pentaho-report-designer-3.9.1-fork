@echo off
setlocal
cd /d "%~dp0"

set "FORK=%~dp0target\prd-ui-fork-3.9.1-fork-1.jar"
set "FLAT=%~dp0target\patch-lib\flatlaf.jar"
set "LIB=%~dp0runtime\prd-ce-3.9.1-GA\lib"

if not exist "%FORK%" goto err_fork
if not exist "%FLAT%" goto err_flat
if not exist "%LIB%" goto err_lib

set "JAVA_EXE=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if /i "%JAVA_EXE%"=="java" (
  where java >nul 2>&1
  if errorlevel 1 goto err_java
) else (
  if not exist "%JAVA_EXE%" goto err_java
)

set "CP=%FORK%;%FLAT%;%LIB%\*"
rem Non-empty title avoids START parsing quirks with quoted java path
start "Pentaho Report Designer" "%JAVA_EXE%" --add-opens java.desktop/javax.swing.plaf.basic=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED -cp "%CP%" org.pentaho.reporting.designer.core.ReportDesigner %*
goto eof

:err_fork
echo [PRD.bat] Missing: %FORK%
echo Run in this folder: mvn package
goto pause_exit

:err_flat
echo [PRD.bat] Missing: %FLAT%
echo Run in this folder: mvn package
goto pause_exit

:err_lib
echo [PRD.bat] Missing folder: %LIB%
echo Run once in PowerShell: tools\setup-prd-runtime.ps1
goto pause_exit

:err_java
echo [PRD.bat] Java not found. Install JDK and set PATH or JAVA_HOME.
goto pause_exit

:pause_exit
pause
exit /b 1

:eof
endlocal
exit /b 0
