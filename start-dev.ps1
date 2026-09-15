# ==============================================================================
# Script de inicio del entorno de desarrollo completo — UNdeC Acreditación
# Levanta: Infraestructura (Docker) + Backend (Spring Boot) + Frontend (Angular)
# ==============================================================================

$ErrorActionPreference = "Stop"
$rootDir = $PSScriptRoot

Write-Host ""
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host "  Iniciando Entorno de Desarrollo — Sistema de Acreditación UNdeC      " -ForegroundColor Cyan
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar archivos .env
Write-Host "[1/4] Verificando archivos de configuración..." -ForegroundColor Yellow

if (-not (Test-Path "$rootDir\infra\.env")) {
    if (Test-Path "$rootDir\infra\.env.example") {
        Copy-Item "$rootDir\infra\.env.example" "$rootDir\infra\.env"
        Write-Host "  -> Creado infra/.env desde infra/.env.example" -ForegroundColor Green
    }
}

if (-not (Test-Path "$rootDir\backend\.env")) {
    if (Test-Path "$rootDir\backend\.env.example") {
        Copy-Item "$rootDir\backend\.env.example" "$rootDir\backend\.env"
        Write-Host "  -> Creado backend/.env desde backend/.env.example" -ForegroundColor Green
    }
}

# 2. Levantar Infraestructura con Docker Compose
Write-Host "[2/4] Levantando infraestructura (PostgreSQL + MinIO)..." -ForegroundColor Yellow
try {
    docker compose -f "$rootDir\infra\docker-compose.yml" up -d
    Write-Host "  -> Contenedores de infraestructura iniciados." -ForegroundColor Green
} catch {
    Write-Host "  [!] Error al iniciar Docker. Asegurate de que Docker Desktop este abierto y con espacio en disco." -ForegroundColor Red
    exit 1
}

# Esperar unos segundos a que PostgreSQL abra el socket
Write-Host "  -> Esperando a que PostgreSQL este listo en el puerto 5432..." -ForegroundColor Gray
$retries = 15
$connected = $false
while ($retries -gt 0) {
    $test = Test-NetConnection -ComputerName 127.0.0.1 -Port 5432 -WarningAction SilentlyContinue
    if ($test.TcpTestSucceeded) {
        $connected = $true
        break
    }
    Start-Sleep -Seconds 1
    $retries--
}

if (-not $connected) {
    Write-Host "  [!] Advertencia: PostgreSQL no respondio a tiempo en 127.0.0.1:5432." -ForegroundColor Yellow
} else {
    Write-Host "  -> PostgreSQL listo para conexiones." -ForegroundColor Green
}

# 3. Iniciar Backend en una ventana dedicada
Write-Host "[3/4] Iniciando Backend (Spring Boot 3.2.5 en puerto 8080)..." -ForegroundColor Yellow
$backendCmd = "`$Host.UI.RawUI.WindowTitle='Backend — UNdeC Acreditacion (Puerto 8080)'; Set-Location '$rootDir\backend'; Write-Host 'Levantando Spring Boot...' -ForegroundColor Cyan; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $backendCmd
Write-Host "  -> Ventana de Backend abierta." -ForegroundColor Green

# 4. Iniciar Frontend en una ventana dedicada
Write-Host "[4/4] Iniciando Frontend (Angular 21 en puerto 4200)..." -ForegroundColor Yellow
$frontendCmd = "`$Host.UI.RawUI.WindowTitle='Frontend — UNdeC Acreditacion (Puerto 4200)'; Set-Location '$rootDir\frontend'; Write-Host 'Levantando Angular...' -ForegroundColor Cyan; pnpm start"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $frontendCmd
Write-Host "  -> Ventana de Frontend abierta." -ForegroundColor Green

Write-Host ""
Write-Host "========================================================================" -ForegroundColor Green
Write-Host "  ¡Entorno de desarrollo iniciado con exito!                            " -ForegroundColor Green
Write-Host "========================================================================" -ForegroundColor Green
Write-Host "  * Frontend:       http://localhost:4200" -ForegroundColor White
Write-Host "  * Backend API:    http://localhost:8080" -ForegroundColor White
Write-Host "  * PostgreSQL:     127.0.0.1:5432 (acreditacion / acreditacion_user)" -ForegroundColor White
Write-Host "  * MinIO Storage:  http://localhost:9001 (minioadmin / minioadmin123)" -ForegroundColor White
Write-Host ""
Write-Host "  Credenciales iniciales:" -ForegroundColor Yellow
Write-Host "  * Usuario:  admin@undec.edu.ar" -ForegroundColor White
Write-Host "  * Password: admin123" -ForegroundColor White
Write-Host "========================================================================" -ForegroundColor Green
Write-Host "  Para detener todos los servicios, ejecuta: .\stop-dev.ps1" -ForegroundColor Gray
Write-Host ""
