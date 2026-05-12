@echo off
chcp 65001 >nul
cd /d "%~dp0"

set "FORK=%~dp0target\prd-ui-fork-3.9.1-fork-1.jar"
set "FLAT=%~dp0target\patch-lib\flatlaf.jar"
set "LIB=%~dp0runtime\prd-ce-3.9.1-GA\lib"

if not exist "%FORK%" (
  echo Нет файла сборки. Откройте PowerShell в этой папке и выполните: mvn package
  pause
  exit /b 1
)
if not exist "%FLAT%" (
  echo Нет FlatLaf. Выполните: mvn package
  pause
  exit /b 1
)
if not exist "%LIB%" (
  echo Нет библиотек PRD. Один раз выполните в PowerShell: tools\setup-prd-runtime.ps1
  pause
  exit /b 1
)

set "JAVA_EXE=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if /i "%JAVA_EXE%"=="java" (
  where java >nul 2>&1
  if errorlevel 1 goto :badjava
) else (
  if not exist "%JAVA_EXE%" goto :badjava
)

set "CP=%FORK%;%FLAT%;%LIB%\*"
start "" "%JAVA_EXE%" --add-opens java.desktop/javax.swing.plaf.basic=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED -cp "%CP%" org.pentaho.reporting.designer.core.ReportDesigner %*
goto :eof

:badjava
echo Не найден Java. Установите JDK и добавьте java в PATH или задайте JAVA_HOME.
pause
exit /b 1
