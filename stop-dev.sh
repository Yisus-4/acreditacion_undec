#!/usr/bin/env bash
# ==============================================================================
# Script de detención del entorno de desarrollo para macOS y Linux — UNdeC
# ==============================================================================

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Colores de consola
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
GRAY='\033[0;90m'
NC='\033[0m'

echo ""
echo -e "${YELLOW}========================================================================${NC}"
echo -e "${YELLOW}  Deteniendo Entorno de Desarrollo — Sistema de Acreditación UNdeC      ${NC}"
echo -e "${YELLOW}========================================================================${NC}"
echo ""

# 1. Detener Backend (Puerto 8080)
echo -e "${YELLOW}[1/3] Deteniendo proceso Backend (puerto 8080)...${NC}"
PID_8080=$(lsof -ti:8080 2>/dev/null || true)
if [ -n "$PID_8080" ]; then
    kill -9 $PID_8080 2>/dev/null || true
    echo -e "${GREEN}  -> Proceso en puerto 8080 detenido.${NC}"
else
    echo -e "${GRAY}  -> No había procesos escuchando en el puerto 8080.${NC}"
fi

# 2. Detener Frontend (Puerto 4200)
echo -e "${YELLOW}[2/3] Deteniendo proceso Frontend (puerto 4200)...${NC}"
PID_4200=$(lsof -ti:4200 2>/dev/null || true)
if [ -n "$PID_4200" ]; then
    kill -9 $PID_4200 2>/dev/null || true
    echo -e "${GREEN}  -> Proceso en puerto 4200 detenido.${NC}"
else
    echo -e "${GRAY}  -> No había procesos escuchando en el puerto 4200.${NC}"
fi

# 3. Detener Contenedores Docker
echo -e "${YELLOW}[3/3] Deteniendo contenedores Docker...${NC}"
if docker compose -f "$ROOT_DIR/infra/docker-compose.yml" stop 2>/dev/null; then
    echo -e "${GREEN}  -> Contenedores detenidos con éxito.${NC}"
else
    echo -e "${GRAY}  -> No se pudo comunicar con Docker (o los contenedores ya estaban detenidos).${NC}"
fi

echo ""
echo -e "${GREEN}========================================================================${NC}"
echo -e "${GREEN}  ¡Entorno de desarrollo detenido correctamente!                        ${NC}"
echo -e "${GREEN}========================================================================${NC}"
echo ""
