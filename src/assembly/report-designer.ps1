# Portable launcher (same layout as ZIP from mvn package). Fork JAR name is resolved by wildcard.
$ErrorActionPreference = "Stop"
$base = $PSScriptRoot
$patch = Join-Path $base "patch"
$lib = Join-Path $base "lib"
$fork = Get-ChildItem -LiteralPath $patch -Filter "prd-ui-fork-*.jar" -ErrorAction SilentlyContinue | Select-Object -First 1
$flat = Join-Path $patch "flatlaf.jar"
if (-not $fork) { throw "Missing prd-ui-fork-*.jar in patch folder" }
if (-not (Test-Path -LiteralPath $flat)) { throw "Missing flatlaf.jar in patch folder" }
$libs = @( $fork.FullName, $flat ) + (Get-ChildItem -Path $lib -Filter "*.jar" | ForEach-Object { $_.FullName })
$cp = ($libs -join ";")
$java = "java"
if ($null -ne $env:JAVA_HOME) {
  $jh = Join-Path $env:JAVA_HOME "bin\java.exe"
  if (Test-Path -LiteralPath $jh) { $java = $jh }
}
& $java --add-opens java.desktop/javax.swing.plaf.basic=ALL-UNNAMED `
  --add-opens java.desktop/sun.awt=ALL-UNNAMED `
  -cp $cp org.pentaho.reporting.designer.core.ReportDesigner @args
