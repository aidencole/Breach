# Breach — one-shot dev environment setup for Windows (v2)
# Run in PowerShell:
#   irm https://raw.githubusercontent.com/aidencole/Breach/main/scripts/setup-dev.ps1 | iex
# Or from a cloned repo:
#   .\scripts\setup-dev.ps1

# Native commands like `java -version` write to stderr; don't treat that as fatal.
$ErrorActionPreference = "Continue"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$RepoUrl   = "https://github.com/aidencole/Breach.git"
$ClonePath = Join-Path $env:USERPROFILE "Projects\Breach"
$GitName   = "aidencole"
$GitEmail  = "aidencole@users.noreply.github.com"

function Refresh-Path {
    $env:Path = @(
        "C:\Program Files\Git\bin",
        "C:\Program Files\GitHub CLI",
        $env:Path
    ) -join ";"
}

function Ensure-Command($name, $wingetId) {
    Refresh-Path
    if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
        Write-Host "Installing $name..." -ForegroundColor Yellow
        winget install --id $wingetId -e --accept-source-agreements --accept-package-agreements
        Refresh-Path
    }
}

function Get-Jdk25Path {
    @(
        "C:\Program Files\Microsoft\jdk-25*",
        "C:\Program Files\Eclipse Adoptium\jdk-25*"
    ) | ForEach-Object {
        Get-ChildItem $_ -ErrorAction SilentlyContinue
    } | Sort-Object Name -Descending | Select-Object -First 1
}

function Test-GitHubLogin {
    Refresh-Path
    if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
        Write-Host "GitHub CLI (gh) not installed. Cloning still works; install gh to push/pull easily." -ForegroundColor Yellow
        Write-Host "  winget install GitHub.cli" -ForegroundColor DarkGray
        return
    }

    $status = gh auth status 2>&1 | Out-String
    if ($LASTEXITCODE -eq 0) {
        Write-Host "GitHub login: OK" -ForegroundColor Green
        ($status -split "`n" | Select-Object -First 2) | ForEach-Object { Write-Host "  $_" -ForegroundColor DarkGray }
    } else {
        Write-Host "GitHub login: NOT logged in on this PC" -ForegroundColor Yellow
        Write-Host "  Run this, then re-run setup if needed:" -ForegroundColor Yellow
        Write-Host "  gh auth login" -ForegroundColor Cyan
        Write-Host "  (Pick GitHub.com -> HTTPS -> Login with browser)" -ForegroundColor DarkGray
    }
}

Write-Host "`n=== Breach dev setup (v2) ===" -ForegroundColor Cyan

Ensure-Command git "Git.Git"

$jdk = Get-Jdk25Path
if (-not $jdk) {
    Write-Host "Installing JDK 25..." -ForegroundColor Yellow
    winget install --id Microsoft.OpenJDK.25 -e --accept-source-agreements --accept-package-agreements
    $jdk = Get-Jdk25Path
}
if (-not $jdk) {
    Write-Host "ERROR: JDK 25 not found. Restart PowerShell and run this script again." -ForegroundColor Red
    exit 1
}

$env:JAVA_HOME = $jdk.FullName
$env:Path = "$($jdk.FullName)\bin;" + $env:Path
Write-Host "Using JAVA_HOME=$($jdk.FullName)" -ForegroundColor DarkGray

if (-not (git config --global user.name 2>$null)) {
    git config --global user.name $GitName
    Write-Host "Set git user.name to $GitName" -ForegroundColor DarkGray
}
if (-not (git config --global user.email 2>$null)) {
    git config --global user.email $GitEmail
    Write-Host "Set git user.email to $GitEmail" -ForegroundColor DarkGray
}

Test-GitHubLogin

if (Test-Path $ClonePath) {
    Write-Host "`nUpdating existing repo at $ClonePath" -ForegroundColor Green
    Set-Location $ClonePath
    git pull origin main
    if ($LASTEXITCODE -ne 0) {
        Write-Host "git pull failed — if this is a auth issue, run: gh auth login" -ForegroundColor Yellow
    }
} else {
    Write-Host "`nCloning Breach to $ClonePath" -ForegroundColor Green
    New-Item -ItemType Directory -Force -Path (Split-Path $ClonePath) | Out-Null
    git clone $RepoUrl $ClonePath
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: git clone failed." -ForegroundColor Red
        exit 1
    }
    Set-Location $ClonePath
}

Write-Host "`nDownloading Minecraft + Fabric (first run takes several minutes)..." -ForegroundColor Yellow
& .\gradlew.bat genSources --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Gradle failed. Check JAVA_HOME and internet connection." -ForegroundColor Red
    exit 1
}

$ideaInstalled = @(
    (Get-ChildItem "$env:ProgramFiles\JetBrains\IntelliJ IDEA*" -ErrorAction SilentlyContinue),
    (Get-ChildItem "$env:LOCALAPPDATA\Programs\IntelliJ IDEA*" -ErrorAction SilentlyContinue)
) | Where-Object { $_ } | Select-Object -First 1

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
  .\gradlew.bat runClient2
  .\gradlew.bat runServer

GitHub push/pull on this PC:
  gh auth login

"@
