#!/usr/bin/env python3
"""
Script unificado de desarrollo — Sistema de Acreditación UNdeC.
Detecta automáticamente el sistema operativo (Windows, macOS, Linux) y ejecuta:
  - Infraestructura (PostgreSQL + MinIO con Docker Compose)
  - Backend (Spring Boot 3.2.5 en puerto 8080)
  - Frontend (Angular 21 en puerto 4200)

Uso:
  python dev.py         -> Inicia todo el entorno
  python dev.py start   -> Inicia todo el entorno
  python dev.py stop    -> Detiene backend, frontend e infraestructura
  python dev.py status  -> Muestra el estado de los servicios
"""

import os
import platform
import shutil
import socket
import subprocess
import sys
import time
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parent
INFRA_DIR = ROOT_DIR / "infra"
BACKEND_DIR = ROOT_DIR / "backend"
FRONTEND_DIR = ROOT_DIR / "frontend"
OS_NAME = platform.system()  # 'Windows', 'Darwin' (macOS), 'Linux'

# Códigos ANSI para colores
CYAN = "\033[0;36m"
GREEN = "\033[0;32m"
YELLOW = "\033[1;33m"
WHITE = "\033[1;37m"
GRAY = "\033[0;90m"
RED = "\033[0;31m"
RESET = "\033[0m"


def print_colored(text: str, color: str = RESET) -> None:
    """Imprime texto con color ANSI."""
    print(f"{color}{text}{RESET}")


def ensure_env_file(directory: Path, filename: str = ".env") -> None:
    """Copia .env.example a .env si este último no existe."""
    target = directory / filename
    example = directory / f"{filename}.example"
    if not target.exists() and example.exists():
        shutil.copyfile(example, target)
        print_colored(f"  -> Creado {target.relative_to(ROOT_DIR)} desde {example.name}", GREEN)


def is_port_in_use(port: int, host: str = "127.0.0.1") -> bool:
    """Comprueba si un puerto TCP está abierto y escuchando."""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(0.5)
        return s.connect_ex((host, port)) == 0


def wait_for_port(port: int, host: str = "127.0.0.1", timeout_seconds: int = 15) -> bool:
    """Espera activamente a que un puerto esté abierto."""
    start = time.time()
    while time.time() - start < timeout_seconds:
        if is_port_in_use(port, host):
            return True
        time.sleep(0.5)
    return False


def start_infra() -> None:
    """Inicia Docker Compose (PostgreSQL + MinIO)."""
    print_colored("[1/4] Verificando configuración y levantando infraestructura...", YELLOW)
    ensure_env_file(INFRA_DIR)
    ensure_env_file(BACKEND_DIR)

    compose_file = str(INFRA_DIR / "docker-compose.yml")
    try:
        subprocess.run(
            ["docker", "compose", "-f", compose_file, "up", "-d"],
            check=True,
            cwd=str(ROOT_DIR),
        )
        print_colored("  -> Contenedores de infraestructura iniciados.", GREEN)
    except (subprocess.CalledProcessError, FileNotFoundError):
        print_colored("  [!] Error: No se pudo ejecutar Docker Compose. Verificá que Docker esté abierto.", RED)
        sys.exit(1)

    print_colored("  -> Esperando a que PostgreSQL responda en 127.0.0.1:5432...", GRAY)
    if wait_for_port(5432, timeout_seconds=15):
        print_colored("  -> PostgreSQL listo para recibir conexiones.", GREEN)
    else:
        print_colored("  [!] Advertencia: PostgreSQL tardó en responder. Continuando...", YELLOW)


def start_backend() -> None:
    """Inicia el Backend Spring Boot en una terminal dedicada según el SO."""
    print_colored("[2/4] Iniciando Backend (Spring Boot en puerto 8080)...", YELLOW)

    if OS_NAME == "Windows":
        cmd = (
            f"$Host.UI.RawUI.WindowTitle='Backend — UNdeC Acreditacion (8080)'; "
            f"Set-Location '{BACKEND_DIR}'; "
            f"Write-Host 'Iniciando Backend Spring Boot...' -ForegroundColor Cyan; "
            f"mvn spring-boot:run"
        )
        subprocess.Popen(["powershell", "-NoExit", "-Command", cmd])
    elif OS_NAME == "Darwin":  # macOS
        script = f'tell application "Terminal" to do script "cd \\"{BACKEND_DIR}\\" && echo \\"Iniciando Backend...\\" && mvn spring-boot:run"'
        subprocess.Popen(["osascript", "-e", script])
    else:  # Linux
        terminal = shutil.which("gnome-terminal") or shutil.which("x-terminal-emulator")
        if terminal:
            subprocess.Popen([terminal, "--", "bash", "-c", f"cd '{BACKEND_DIR}' && mvn spring-boot:run; exec bash"])
        else:
            log_file = open(ROOT_DIR / "backend.log", "a")
            subprocess.Popen(["mvn", "spring-boot:run"], cwd=str(BACKEND_DIR), stdout=log_file, stderr=log_file)

    print_colored("  -> Backend iniciado en ventana independiente.", GREEN)


def start_frontend() -> None:
    """Inicia el Frontend Angular en una terminal dedicada según el SO."""
    print_colored("[3/4] Iniciando Frontend (Angular en puerto 4200)...", YELLOW)

    if OS_NAME == "Windows":
        cmd = (
            f"$Host.UI.RawUI.WindowTitle='Frontend — UNdeC Acreditacion (4200)'; "
            f"Set-Location '{FRONTEND_DIR}'; "
            f"Write-Host 'Iniciando Frontend Angular...' -ForegroundColor Cyan; "
            f"pnpm start"
        )
        subprocess.Popen(["powershell", "-NoExit", "-Command", cmd])
    elif OS_NAME == "Darwin":  # macOS
        script = f'tell application "Terminal" to do script "cd \\"{FRONTEND_DIR}\\" && echo \\"Iniciando Frontend...\\" && pnpm start"'
        subprocess.Popen(["osascript", "-e", script])
    else:  # Linux
        terminal = shutil.which("gnome-terminal") or shutil.which("x-terminal-emulator")
        if terminal:
            subprocess.Popen([terminal, "--", "bash", "-c", f"cd '{FRONTEND_DIR}' && pnpm start; exec bash"])
        else:
            log_file = open(ROOT_DIR / "frontend.log", "a")
            subprocess.Popen(["pnpm", "start"], cwd=str(FRONTEND_DIR), stdout=log_file, stderr=log_file)

    print_colored("  -> Frontend iniciado en ventana independiente.", GREEN)


def print_banner() -> None:
    """Muestra el resumen final de URLs y accesos."""
    print_colored("\n========================================================================", GREEN)
    print_colored("  ¡Entorno de desarrollo iniciado con éxito!                            ", GREEN)
    print_colored("========================================================================", GREEN)
    print_colored(f"  * Sistema operativo detectado: {OS_NAME}", GRAY)
    print_colored("  * Frontend:       http://localhost:4200", WHITE)
    print_colored("  * Backend API:    http://localhost:8080", WHITE)
    print_colored("  * PostgreSQL:     127.0.0.1:5432 (acreditacion / acreditacion_user)", WHITE)
    print_colored("  * MinIO Storage:  http://localhost:9001 (minioadmin / minioadmin123)", WHITE)
    print_colored("\n  Credenciales de acceso inicial:", YELLOW)
    print_colored("  * Usuario:  admin@undec.edu.ar", WHITE)
    print_colored("  * Password: admin123", WHITE)
    print_colored("========================================================================", GREEN)
    print_colored("  Para detener todos los servicios, ejecutá: python dev.py stop\n", GRAY)


def kill_process_on_port(port: int) -> None:
    """Detiene cualquier proceso que esté escuchando en un puerto específico."""
    if not is_port_in_use(port):
        return

    try:
        if OS_NAME == "Windows":
            ps_cmd = (
                f"Get-NetTCPConnection -LocalPort {port} -ErrorAction SilentlyContinue | "
                f"ForEach-Object {{ Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }}"
            )
            subprocess.run(["powershell", "-Command", ps_cmd], check=False)
        else:  # macOS / Linux
            subprocess.run(
                f"lsof -ti:{port} | xargs kill -9 2>/dev/null || true",
                shell=True,
                check=False,
            )
        print_colored(f"  -> Proceso en puerto {port} detenido.", GREEN)
    except Exception as e:
        print_colored(f"  [!] No se pudo detener el proceso en el puerto {port}: {e}", YELLOW)


def stop_all() -> None:
    """Detiene backend, frontend y la infraestructura."""
    print_colored("\n========================================================================", YELLOW)
    print_colored("  Deteniendo Entorno de Desarrollo — Sistema de Acreditación UNdeC      ", YELLOW)
    print_colored("========================================================================", YELLOW)

    print_colored("[1/3] Deteniendo Frontend (puerto 4200)...", YELLOW)
    kill_process_on_port(4200)

    print_colored("[2/3] Deteniendo Backend (puerto 8080)...", YELLOW)
    kill_process_on_port(8080)

    print_colored("[3/3] Deteniendo contenedores Docker...", YELLOW)
    compose_file = str(INFRA_DIR / "docker-compose.yml")
    try:
        subprocess.run(["docker", "compose", "-f", compose_file, "stop"], check=False)
        print_colored("  -> Contenedores detenidos con éxito.", GREEN)
    except Exception as e:
        print_colored(f"  [!] Error al detener Docker: {e}", YELLOW)

    print_colored("\n========================================================================", GREEN)
    print_colored("  ¡Entorno de desarrollo detenido correctamente!                        ", GREEN)
    print_colored("========================================================================\n", GREEN)


def show_status() -> None:
    """Muestra el estado de cada servicio."""
    print_colored("\n=== Estado de los Servicios ===", CYAN)
    print(f"  * PostgreSQL (5432): {'[ACTIVO]' if is_port_in_use(5432) else '[INACTIVO]'}")
    print(f"  * Backend (8080):    {'[ACTIVO]' if is_port_in_use(8080) else '[INACTIVO]'}")
    print(f"  * Frontend (4200):   {'[ACTIVO]' if is_port_in_use(4200) else '[INACTIVO]'}")
    print(f"  * MinIO (9001):      {'[ACTIVO]' if is_port_in_use(9001) else '[INACTIVO]'}")
    print("")


def main() -> None:
    action = sys.argv[1].lower() if len(sys.argv) > 1 else "start"

    if action in ("start", "up", "run"):
        print_colored("\nIniciando entorno en " + OS_NAME + "...", CYAN)
        start_infra()
        start_backend()
        start_frontend()
        print_banner()
    elif action in ("stop", "down"):
        stop_all()
    elif action in ("status", "ps"):
        show_status()
    else:
        print_colored(f"Comando desconocido: '{action}'. Usá: start, stop o status.", RED)
        sys.exit(1)


if __name__ == "__main__":
    main()
