# Breach — one-shot dev environment setup for Windows
# Run in PowerShell:  irm https://raw.githubusercontent.com/aidencole/Breach/main/scripts/setup-dev.ps1 | iex
# Or from a cloned repo:  .\scripts\setup-dev.ps1

$ErrorActionPreference = "Stop"

$RepoUrl   = "https://github.com/aidencole/Breach.git"
$ClonePath = Join-Path $env:USERPROFILE "Projects\Breach"
$GitName   = "aidencole"
$GitEmail  = "aidencole@users.noreply.github.com"

function Refresh-Path {
    $env:Path = "C:\Program Files\Git\bin;C:\Program Files\GitHub CLI;" + $env:Path
}

function Ensure-Command($name, $wingetId) {
    Refresh-Path
    if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
        Write-Host "Installing $name..." -ForegroundColor Yellow
        winget install --id $wingetId -e --accept-source-agreements --accept-package-agreements
        Refresh-Path
    }
}

Write-Host "`n=== Breach dev setup ===" -ForegroundColor Cyan

Ensure-Command git "Git.Git"

if (-not (Get-Command java -ErrorAction SilentlyContinue) -or (java -version 2>&1 | Select-String "25") -eq $null) {
    Write-Host "Installing JDK 25..." -ForegroundColor Yellow
    winget install --id Microsoft.OpenJDK.25 -e --accept-source-agreements --accept-package-agreements
}

$jdk = Get-ChildItem "C:\Program Files\Microsoft\jdk-25*" -ErrorAction SilentlyContinue |
    Sort-Object Name -Descending | Select-Object -First 1
if (-not $jdk) {
    throw "JDK 25 not found after install. Restart PowerShell and run this script again."
}
$env:JAVA_HOME = $jdk.FullName
$env:Path = "$($jdk.FullName)\bin;" + $env:Path
Write-Host "Using JAVA_HOME=$($jdk.FullName)" -ForegroundColor DarkGray

if (-not (git config --global user.name 2>$null)) {
    git config --global user.name $GitName
}
if (-not (git config --global user.email 2>$null)) {
    git config --global user.email $GitEmail
}

if (Test-Path $ClonePath) {
    Write-Host "Updating existing repo at $ClonePath" -ForegroundColor Green
    Set-Location $ClonePath
    git pull
} else {
    Write-Host "Cloning Breach to $ClonePath" -ForegroundColor Green
    New-Item -ItemType Directory -Force -Path (Split-Path $ClonePath) | Out-Null
    git clone $RepoUrl $ClonePath
    Set-Location $ClonePath
}

Write-Host "`nDownloading Minecraft + Fabric (first run takes several minutes)..." -ForegroundColor Yellow
.\gradlew.bat genSources --no-daemon

$ideaInstalled = Get-ChildItem "$env:ProgramFiles\JetBrains\IntelliJ IDEA*" -ErrorAction SilentlyContinue
if (-not $ideaInstalled) {
    $ideaInstalled = Get-ChildItem "$env:LOCALAPPDATA\Programs\IntelliJ IDEA*" -ErrorAction SilentlyContinue
}
if (-not $ideaInstalled) {
    Write-Host "`nInstalling IntelliJ IDEA Community..." -ForegroundColor Yellow
    winget install --id JetBrains.IntelliJIDEA.Community -e --accept-source-agreements --accept-package-agreements
}

Write-Host "`n=== Setup complete ===" -ForegroundColor Green
Write-Host @"

Next steps:
  1. Open IntelliJ IDEA
  2. File -> Open -> $ClonePath
  3. Trust project, wait for Gradle sync
  4. File -> Project Structure -> Project SDK -> JDK 25
  5. Run 'Minecraft Client' from the top-right dropdown

Or without IntelliJ:
  cd $ClonePath
  .\gradlew.bat runClient
  .\gradlew.bat runClient2    # second test client
  .\gradlew.bat runServer     # local multiplayer server

"@
