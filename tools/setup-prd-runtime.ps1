# Downloads official PRD 3.9.1 GA and unpacks it under runtime/prd-ce-3.9.1-GA (needed for mvn package).
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$runtime = Join-Path $root "runtime"
$zip = Join-Path $runtime "prd-ce-3.9.1-GA.zip"
$dest = Join-Path $runtime "prd-ce-3.9.1-GA"
$lib = Join-Path $dest "lib"

New-Item -ItemType Directory -Force -Path $runtime | Out-Null

if (-not (Test-Path $zip) -or (Get-Item $zip).Length -lt 160MB) {
  Write-Host "Downloading prd-ce-3.9.1-GA.zip (~170 MB)..."
  $zipTmp = $zip + ".download"
  if (Test-Path $zipTmp) { Remove-Item -LiteralPath $zipTmp -Force -ErrorAction SilentlyContinue }
  curl.exe -L --retry 5 --retry-delay 2 -o $zipTmp "https://downloads.sourceforge.net/project/jfreereport/04.%20Report%20Designer/3.9.1-stable/prd-ce-3.9.1-GA.zip"
  if ($LASTEXITCODE -ne 0) { throw "curl failed with exit $LASTEXITCODE" }
  if (-not (Test-Path $zipTmp) -or (Get-Item $zipTmp).Length -lt 160MB) {
    throw "Download looks incomplete. Check network and delete $zipTmp then retry."
  }
  Move-Item -LiteralPath $zipTmp -Destination $zip -Force
}

if (-not (Test-Path $lib)) {
  Write-Host "Extracting zip..."
  Expand-Archive -LiteralPath $zip -DestinationPath $runtime -Force
  # Some archives nest one extra folder; normalize so .../prd-ce-3.9.1-GA/lib exists
  if (-not (Test-Path $lib)) {
    $nested = Get-ChildItem -Path $runtime -Directory | Where-Object { Test-Path (Join-Path $_.FullName "lib") } | Select-Object -First 1
    if ($null -ne $nested -and $nested.FullName -ne $dest) {
      if (Test-Path $dest) { Remove-Item -LiteralPath $dest -Recurse -Force }
      Rename-Item -LiteralPath $nested.FullName -NewName "prd-ce-3.9.1-GA"
    }
  }
}

if (-not (Test-Path $lib)) {
  throw "Unpack failed: lib folder not found at $lib"
}

$jarCount = (Get-ChildItem -Path $lib -Filter "*.jar").Count
Write-Host "OK: $jarCount jars under $lib"
Write-Host "Next: mvn -f `"$(Join-Path $root 'pom.xml')`" package"
