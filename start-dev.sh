#!/usr/bin/env bash
# ==============================================================================
# Script de inicio del entorno de desarrollo para macOS y Linux — UNdeC
# Levanta: Infraestructura (Docker) + Backend (Spring Boot) + Frontend (Angular)
# ==============================================================================

set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Colores de consola
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
WHITE='\033[1;37m'
GRAY='\033[0;90m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo ""
echo -e "${CYAN}========================================================================${NC}"
echo -e "${CYAN}  Iniciando Entorno de Desarrollo — Sistema de Acreditación UNdeC      ${NC}"
echo -e "${CYAN}========================================================================${NC}"
echo ""

# 1. Verificar archivos .env
echo -e "${YELLOW}[1/4] Verificando archivos de configuración...${NC}"

if [ ! -f "$ROOT_DIR/infra/.env" ] && [ -f "$ROOT_DIR/infra/.env.example" ]; then
    cp "$ROOT_DIR/infra/.env.example" "$ROOT_DIR/infra/.env"
    echo -e "${GREEN}  -> Creado infra/.env desde infra/.env.example${NC}"
fi

if [ ! -f "$ROOT_DIR/backend/.env" ] && [ -f "$ROOT_DIR/backend/.env.example" ]; then
    cp "$ROOT_DIR/backend/.env.example" "$ROOT_DIR/backend/.env"
    echo -e "${GREEN}  -> Creado backend/.env desde backend/.env.example${NC}"
fi

# 2. Levantar Infraestructura con Docker Compose
echo -e "${YELLOW}[2/4] Levantando infraestructura (PostgreSQL + MinIO)...${NC}"
if ! docker compose -f "$ROOT_DIR/infra/docker-compose.yml" up -d; then
    echo -e "${RED}  [!] Error al iniciar Docker. Asegurate de que Docker esté abierto y funcionando.${NC}"
    exit 1
fi
echo -e "${GREEN}  -> Contenedores de infraestructura iniciados.${NC}"

# Esperar a que PostgreSQL esté listo
echo -e "${GRAY}  -> Esperando a que PostgreSQL responda en el puerto 5432...${NC}"
RETRIES=15
READY=0
while [ $RETRIES -gt 0 ]; do
    if nc -z 127.0.0.1 5432 2>/dev/null || (echo > /dev/tcp/127.0.0.1/5432) 2>/dev/null; then
        READY=1
        break
    fi
    sleep 1
    RETRIES=$((RETRIES - 1))
done

if [ $READY -eq 1 ]; then
    echo -e "${GREEN}  -> PostgreSQL listo para conexiones.${NC}"
else
    echo -e "${YELLOW}  [!] Advertencia: PostgreSQL tardó en responder. Continuando...${NC}"
fi

# 3 y 4. Iniciar Backend y Frontend
# Si estamos en macOS con interfaz gráfica, abrimos terminales dedicadas con osascript
if [[ "$OSTYPE" == "darwin"* ]] && command -v osascript >/dev/null 2>&1; then
    echo -e "${YELLOW}[3/4] Iniciando Backend (Spring Boot 3.2.5 en puerto 8080 en nueva terminal)...${NC}"
    osascript -e "tell application \"Terminal\" to do script \"cd '$ROOT_DIR/backend' && echo 'Iniciando Backend Spring Boot...' && mvn spring-boot:run\""
    echo -e "${GREEN}  -> Terminal de Backend abierta.${NC}"

    echo -e "${YELLOW}[4/4] Iniciando Frontend (Angular 21 en puerto 4200 en nueva terminal)...${NC}"
    osascript -e "tell application \"Terminal\" to do script \"cd '$ROOT_DIR/frontend' && echo 'Iniciando Frontend Angular...' && pnpm start\""
    echo -e "${GREEN}  -> Terminal de Frontend abierta.${NC}"
else
    # En Linux o entornos headless, levantamos en background
    echo -e "${YELLOW}[3/4] Iniciando Backend (Spring Boot en background)...${NC}"
    (cd "$ROOT_DIR/backend" && mvn spring-boot:run) > /dev/null 2>&1 &
    echo -e "${GREEN}  -> Backend ejecutándose en segundo plano.${NC}"

    echo -e "${YELLOW}[4/4] Iniciando Frontend (Angular en background)...${NC}"
    (cd "$ROOT_DIR/frontend" && pnpm start) > /dev/null 2>&1 &
    echo -e "${GREEN}  -> Frontend ejecutándose en segundo plano.${NC}"
fi

echo ""
echo -e "${GREEN}========================================================================${NC}"
echo -e "${GREEN}  ¡Entorno de desarrollo iniciado con éxito!                            ${NC}"
echo -e "${GREEN}========================================================================${NC}"
echo -e "${WHITE}  * Frontend:       http://localhost:4200${NC}"
echo -e "${WHITE}  * Backend API:    http://localhost:8080${NC}"
echo -e "${WHITE}  * PostgreSQL:     127.0.0.1:5432 (acreditacion / acreditacion_user)${NC}"
echo -e "${WHITE}  * MinIO Storage:  http://localhost:9001 (minioadmin / minioadmin123)${NC}"
echo ""
echo -e "${YELLOW}  Credenciales iniciales:${NC}"
echo -e "${WHITE}  * Usuario:  admin@undec.edu.ar${NC}"
echo -e "${WHITE}  * Password: admin123${NC}"
echo -e "${GREEN}========================================================================${NC}"
echo -e "${GRAY}  Para detener todos los servicios, ejecutá: ./stop-dev.sh${NC}"
echo ""
