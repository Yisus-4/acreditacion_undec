# Infraestructura

## Setup local

1. Copiar variables de entorno:
   cp .env.example .env

2. Levantar servicios:
   docker compose up -d

3. Verificar que todo esté corriendo:
   docker compose ps

## Servicios locales
- PostgreSQL: localhost:5432
- MinIO API: localhost:9000
- MinIO Consola: localhost:9001
