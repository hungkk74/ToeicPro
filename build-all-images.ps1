# =============================================================================
# Build All Docker Images - TOEIC Pro Platform (Windows PowerShell)
# =============================================================================
# Cách sử dụng:
#   .\build-all-images.ps1
#
# Script này tự động tìm JDK 21 (Temurin) và đóng gói 7 Docker Images
# cho toàn bộ Microservices bằng Jib Maven Plugin.
# =============================================================================

$ErrorActionPreference = "Stop"

# --- Tự động tìm JDK 21 ---
$jdk21Path = $null

# Kiểm tra thư mục .jdks của IntelliJ IDEA
$jdksDir = "$env:USERPROFILE\.jdks"
if (Test-Path $jdksDir) {
    $jdk21Dir = Get-ChildItem $jdksDir -Directory | Where-Object { $_.Name -match "21" } | Select-Object -First 1
    if ($jdk21Dir) {
        $jdk21Path = $jdk21Dir.FullName
    }
}

# Kiểm tra JAVA_HOME nếu chưa tìm thấy
if (-not $jdk21Path -and $env:JAVA_HOME) {
    $javaVersion = & "$env:JAVA_HOME\bin\java" -version 2>&1 | Select-String "21\."
    if ($javaVersion) {
        $jdk21Path = $env:JAVA_HOME
    }
}

if (-not $jdk21Path) {
    Write-Host "ERROR: Khong tim thay JDK 21. Vui long cai dat Temurin JDK 21." -ForegroundColor Red
    Write-Host "Download tai: https://adoptium.net/temurin/releases/?version=21" -ForegroundColor Yellow
    exit 1
}

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  TOEIC Pro - Build All Docker Images" -ForegroundColor Cyan
Write-Host "  JDK 21: $jdk21Path" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

$env:JAVA_HOME = $jdk21Path

# --- Danh sách 7 services ---
$services = @(
    "gateway",
    "userservice",
    "subscriptionservice",
    "paymentservice",
    "examservice",
    "courseservice",
    "notificationservice"
)

$rootDir = $PSScriptRoot
$failedServices = @()

foreach ($service in $services) {
    $servicePath = Join-Path $rootDir $service
    if (-not (Test-Path (Join-Path $servicePath "pom.xml"))) {
        Write-Host "SKIP: $service (khong tim thay pom.xml)" -ForegroundColor Yellow
        continue
    }

    Write-Host ""
    Write-Host ">>> Building Docker image: $service ..." -ForegroundColor Green

    Push-Location $servicePath
    try {
        & .\mvnw.cmd verify -DskipTests -Pprod jib:dockerBuild -B
        if ($LASTEXITCODE -ne 0) {
            throw "Build failed for $service"
        }
        Write-Host "<<< $service - BUILD SUCCESS" -ForegroundColor Green
    }
    catch {
        Write-Host "<<< $service - BUILD FAILED" -ForegroundColor Red
        $failedServices += $service
    }
    finally {
        Pop-Location
    }
}

# --- Kết quả ---
Write-Host ""
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  KET QUA BUILD" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

if ($failedServices.Count -eq 0) {
    Write-Host "  Tat ca $($services.Count) services THANH CONG!" -ForegroundColor Green
    Write-Host ""
    Write-Host "  Buoc tiep theo:" -ForegroundColor Yellow
    Write-Host "    docker compose -f docker-compose-all.yml up -d" -ForegroundColor White
}
else {
    Write-Host "  Thanh cong: $($services.Count - $failedServices.Count)/$($services.Count)" -ForegroundColor Yellow
    Write-Host "  That bai:   $($failedServices -join ', ')" -ForegroundColor Red
}

Write-Host ""
