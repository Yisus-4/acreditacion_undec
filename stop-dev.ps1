# ==============================================================================
# Script de detención del entorno de desarrollo — UNdeC Acreditación
# Detiene: Frontend (puerto 4200) + Backend (puerto 8080) + Docker Compose
# ==============================================================================

$rootDir = $PSScriptRoot

Write-Host ""
Write-Host "========================================================================" -ForegroundColor Yellow
Write-Host "  Deteniendo Entorno de Desarrollo — Sistema de Acreditación UNdeC      " -ForegroundColor Yellow
Write-Host "========================================================================" -ForegroundColor Yellow
Write-Host ""

# 1. Detener Backend (Puerto 8080)
Write-Host "[1/3] Deteniendo proceso Backend (puerto 8080)..." -ForegroundColor Yellow
$backendConns = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($backendConns) {
    foreach ($conn in $backendConns) {
        try {
            Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
            Write-Host "  -> Proceso PID $($conn.OwningProcess) detenido." -ForegroundColor Green
        } catch {}
    }
} else {
    Write-Host "  -> No habia procesos escuchando en el puerto 8080." -ForegroundColor Gray
}

# 2. Detener Frontend (Puerto 4200)
Write-Host "[2/3] Deteniendo proceso Frontend (puerto 4200)..." -ForegroundColor Yellow
$frontendConns = Get-NetTCPConnection -LocalPort 4200 -ErrorAction SilentlyContinue
if ($frontendConns) {
    foreach ($conn in $frontendConns) {
        try {
            Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
            Write-Host "  -> Proceso PID $($conn.OwningProcess) detenido." -ForegroundColor Green
        } catch {}
    }
} else {
    Write-Host "  -> No habia procesos escuchando en el puerto 4200." -ForegroundColor Gray
}

# 3. Detener Contenedores Docker
Write-Host "[3/3] Deteniendo contenedores Docker..." -ForegroundColor Yellow
try {
    docker compose -f "$rootDir\infra\docker-compose.yml" stop
    Write-Host "  -> Contenedores detenidos con exito." -ForegroundColor Green
} catch {
    Write-Host "  [!] No se pudo comunicar con Docker (verificar si Docker Desktop esta abierto)." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================================================" -ForegroundColor Green
Write-Host "  ¡Entorno de desarrollo detenido correctamente!                        " -ForegroundColor Green
Write-Host "========================================================================" -ForegroundColor Green
Write-Host ""
