# Sistema de Gestión para Acreditaciones de Grado — UNdeC

Monorepo que contiene el backend, frontend e infraestructura para el Sistema de Acreditación de la Universidad Nacional de Chilecito (UNdeC).

---

## 🚀 Inicio Rápido (Levantar Todo en un Solo Paso)

Para levantar todo el entorno de desarrollo (Base de datos en Docker + Backend Spring Boot + Frontend Angular) ejecutá el script en la raíz del repositorio:

### En Windows (PowerShell):
```powershell
.\start-dev.ps1
```
*(O en CMD / Doble clic: `start-dev.bat`)*

### En macOS y Linux (Bash):
```bash
./start-dev.sh
```

> **¿Qué hace este script?**
> 1. Comprueba los archivos `.env` (creándolos desde sus `.env.example` si no existen).
> 2. Levanta los contenedores de **PostgreSQL** y **MinIO** con Docker Compose.
> 3. Espera a que PostgreSQL esté listo en el puerto `5432`.
> 4. En Windows y macOS abre terminales dedicadas con logs en vivo para el **Backend** (`mvn spring-boot:run`) y el **Frontend** (`pnpm start`).

Para **detener** todos los servicios y contenedores:
* En Windows: `.\stop-dev.ps1` (o `stop-dev.bat`)
* En macOS / Linux: `./stop-dev.sh`

---

## 🌐 URLs y Servicios

| Servicio | URL / Host | Credenciales por Defecto |
|---|---|---|
| **Frontend (Angular)** | [http://localhost:4200](http://localhost:4200) | Iniciar sesión en `/login` |
| **Backend API (Spring Boot)** | [http://localhost:8080](http://localhost:8080) | — |
| **PostgreSQL** | `127.0.0.1:5432` / DB: `acreditacion` | Usuario: `acreditacion_user` <br> Clave: `acreditacion_pass` |
| **MinIO (Consola Web)** | [http://localhost:9001](http://localhost:9001) | Usuario: `minioadmin` <br> Clave: `minioadmin123` |

### Credenciales del Usuario Administrador Inicial
* **Email:** `admin@undec.edu.ar`
* **Contraseña:** `admin123`
* **Rol:** `ADMINISTRATOR`

---

## 📁 Estructura del Monorepo

* **[`backend/`](backend/)**: API REST desarrollada en **Java 21** y **Spring Boot 3.2.5** bajo principios de **Clean Architecture**:
  * `domain/`: Entidades puras e invariantes de negocio (`User`, `Role`, `Permission`).
  * `application/`: Casos de uso y puertos de entrada/salida (`Login`, `RegisterUser`, `ListUsers`, `UpdateUser`, `ChangeUserStatus`, `ListRoles`).
  * `infrastructure/`: Implementaciones de persistencia (PostgreSQL + Spring Data JPA + Flyway), seguridad (Spring Security stateless, JWT HMAC-SHA256, Rate Limiting con Bucket4j) y controladores REST.
* **[`frontend/`](frontend/)**: Aplicación SPA en **Angular 21** (Standalone Components, TypeScript strict, RxJS, SCSS nativo):
  * `core/`: Servicios de autenticación (`Auth`), interceptor de Bearer token (`authInterceptor`), `authGuard` y servicio de usuarios (`UsersService`).
  * `features/`: Vistas de Login (`/login`), Dashboard institucional (`/dashboard`) y Administración de usuarios (`/admin/users`).
* **[`infra/`](infra/)**: Orquestación de infraestructura local con **Docker Compose** (`PostgreSQL 16` y `MinIO`).

---

## 🧪 Ejecución Manual y Pruebas

Si preferís levantar cada componente por separado en terminales independientes:

### 1. Infraestructura
```bash
cd infra
docker compose up -d
```

### 2. Backend
```bash
cd backend
mvn spring-boot:run
```
* Para correr todos los tests automatizados (83 pruebas unitarias y de integración):
  ```bash
  mvn test "-Dspring.profiles.active=test"
  ```

### 3. Frontend
```bash
cd frontend
pnpm install
pnpm start
```
* Para verificar la compilación de producción:
  ```bash
  pnpm build
  ```
