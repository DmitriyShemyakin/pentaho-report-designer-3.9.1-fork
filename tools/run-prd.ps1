# Runs Report Designer with fork classes + FlatLaf first on the classpath (after mvn package).
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$forkJar = Join-Path $root "target\prd-ui-fork-3.9.1-fork-1.jar"
$flatJar = Join-Path $root "target\patch-lib\flatlaf.jar"
$lib = Join-Path $root "runtime\prd-ce-3.9.1-GA\lib"

if (-not (Test-Path $forkJar)) {
  throw "Missing $forkJar - run: mvn package"
}
if (-not (Test-Path $flatJar)) {
  throw "Missing $flatJar - run: mvn package"
}
if (-not (Test-Path $lib)) {
  throw "Missing $lib - run: tools\setup-prd-runtime.ps1"
}

$libs = @( $forkJar, $flatJar ) + (Get-ChildItem -Path $lib -Filter "*.jar" | ForEach-Object { $_.FullName })
$cp = ($libs -join ";")

$java = "java"
if ($null -ne $env:JAVA_HOME) {
  $java = Join-Path $env:JAVA_HOME "bin\java.exe"
}

& $java --add-opens java.desktop/javax.swing.plaf.basic=ALL-UNNAMED `
  --add-opens java.desktop/sun.awt=ALL-UNNAMED `
  -cp $cp org.pentaho.reporting.designer.core.ReportDesigner @args
