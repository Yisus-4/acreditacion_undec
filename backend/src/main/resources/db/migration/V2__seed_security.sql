-- =============================================================================
-- V2__seed_security.sql  --  Seed inicial de roles y permisos (Etapa 3, iter. 2)
-- =============================================================================
-- Alcance aprobado:
--   * Insertar los 5 roles funcionales iniciales con codigo estable y nombre
--     editable.
--   * Insertar los permisos minimos del modulo de seguridad para usuarios,
--     roles, permisos y cuenta propia.
--   * Relacionar roles y permisos mediante rol_permiso.
--   * NO se crea el usuario administrador ni se almacenan contrasenas en SQL
--     (queda para una migracion/iteracion posterior).
--
-- Decisiones de modelado (no obvias):
--   * Los roles sembrados se marcan es_sistema = TRUE: sus codigos son
--     estables (inmutables por reglas del dominio) y no deben eliminarse ni
--     desactivarse. El campo `nombre` sigue siendo editable; es_sistema solo
--     gobierna borrado/desactivacion, no la edicion del nombre. La constraint
--     ck_rol_sistema_activo (NOT es_sistema OR activo) se satisface con
--     activo = TRUE por defecto.
--   * Idempotencia: Flyway registra cada migracion en flyway_schema_history y
--     no la reejecuta, pero el script se escribe idempotente con
--     ON CONFLICT (codigo) DO NOTHING para que un reintento manual, una
--     inspeccion o una reaplicacion sobre una base ya sembrada no duplique ni
--     sobrescriba ediciones hechas sobre los nombres (el seed respeta los
--     valores existentes).
--   * Las asignaciones rol_permiso referencian por codigo (subquery) porque
--     los ids son UUID autogenerados y no se hardcodean. La PK compuesta
--     (rol_id, permiso_id) + ON CONFLICT DO NOTHING evita duplicados.
--   * Codigos de permiso siguen el formato de la constraint
--     ck_permiso_codigo_formato: ^[A-Z0-9]+(_[A-Z0-9]+)*$ (segmentos en
--     MAYUSCULAS separados por un guion bajo).
--   * No se insertan filas en usuario ni usuario_rol en esta iteracion.
-- =============================================================================

-- ----------------------------------------------------------------------------
-- Permisos minimos del modulo de seguridad
-- ----------------------------------------------------------------------------
-- Agrupados por recurso:
--   usuario   -> SECURITY_USER_*
--   rol       -> SECURITY_ROLE_*
--   permiso   -> SECURITY_PERMISSION_*
--   propia    -> ACCOUNT_SELF_*  (cuenta del usuario autenticado)

INSERT INTO permiso (codigo, nombre, descripcion) VALUES
    ('SECURITY_USER_READ',
     'Consultar usuarios',
     'Permite listar y consultar usuarios del modulo de seguridad.'),
    ('SECURITY_USER_WRITE',
     'Crear y editar usuarios',
     'Permite crear y actualizar usuarios del modulo de seguridad.'),
    ('SECURITY_USER_MANAGE_ROLES',
     'Asignar roles a usuarios',
     'Permite asignar y revocar roles de un usuario.'),
    ('SECURITY_ROLE_READ',
     'Consultar roles',
     'Permite listar y consultar roles del modulo de seguridad.'),
    ('SECURITY_ROLE_WRITE',
     'Crear y editar roles',
     'Permite crear y actualizar roles del modulo de seguridad.'),
    ('SECURITY_PERMISSION_READ',
     'Consultar permisos',
     'Permite listar y consultar permisos del modulo de seguridad.'),
    ('SECURITY_PERMISSION_WRITE',
     'Crear y editar permisos',
     'Permite crear y actualizar permisos del modulo de seguridad.'),
    ('ACCOUNT_SELF_READ',
     'Consultar cuenta propia',
     'Permite al usuario autenticado consultar su propia cuenta.'),
    ('ACCOUNT_SELF_UPDATE_PASSWORD',
     'Cambiar contrasena propia',
     'Permite al usuario autenticado cambiar su propia contrasena.')
ON CONFLICT (codigo) DO NOTHING;

-- ----------------------------------------------------------------------------
-- Roles funcionales iniciales (codigo estable, nombre editable)
-- ----------------------------------------------------------------------------

INSERT INTO rol (codigo, nombre, descripcion, es_sistema) VALUES
    ('ADMINISTRATOR',
     'Administrador',
     'Administracion total del modulo de seguridad.',
     TRUE),
    ('AC_COORDINATOR',
     'Coordinador de Acreditacion',
     'Coordina el proceso de acreditacion y administra usuarios.',
     TRUE),
    ('CAREER_DIRECTOR',
     'Director de Carrera',
     'Dirige una carrera en el proceso de acreditacion.',
     TRUE),
    ('SELF_EVALUATION_COMMITTEE_MEMBER',
     'Miembro del Comite de Autoevaluacion',
     'Miembro del comite de autoevaluacion de la carrera.',
     TRUE),
    ('INFORMATION_PROVIDER',
     'Proveedor de Informacion',
     'Provee informacion solicitada durante la acreditacion.',
     TRUE)
ON CONFLICT (codigo) DO NOTHING;

-- ----------------------------------------------------------------------------
-- Asignaciones rol <-> permiso  (referenciadas por codigo; idempotentes)
-- ----------------------------------------------------------------------------
-- ADMINISTRATOR: administracion total del modulo de seguridad.

INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
JOIN permiso p ON p.codigo IN (
    'SECURITY_USER_READ',
    'SECURITY_USER_WRITE',
    'SECURITY_USER_MANAGE_ROLES',
    'SECURITY_ROLE_READ',
    'SECURITY_ROLE_WRITE',
    'SECURITY_PERMISSION_READ',
    'SECURITY_PERMISSION_WRITE',
    'ACCOUNT_SELF_READ',
    'ACCOUNT_SELF_UPDATE_PASSWORD'
)
WHERE r.codigo = 'ADMINISTRATOR'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;

-- AC_COORDINATOR: gestiona usuarios y asigna roles; consulta roles/permisos;
-- gestiona su propia cuenta.

INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
JOIN permiso p ON p.codigo IN (
    'SECURITY_USER_READ',
    'SECURITY_USER_WRITE',
    'SECURITY_USER_MANAGE_ROLES',
    'SECURITY_ROLE_READ',
    'SECURITY_PERMISSION_READ',
    'ACCOUNT_SELF_READ',
    'ACCOUNT_SELF_UPDATE_PASSWORD'
)
WHERE r.codigo = 'AC_COORDINATOR'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;

-- CAREER_DIRECTOR: consulta usuarios y roles; gestiona su propia cuenta.

INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
JOIN permiso p ON p.codigo IN (
    'SECURITY_USER_READ',
    'SECURITY_ROLE_READ',
    'ACCOUNT_SELF_READ',
    'ACCOUNT_SELF_UPDATE_PASSWORD'
)
WHERE r.codigo = 'CAREER_DIRECTOR'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;

-- SELF_EVALUATION_COMMITTEE_MEMBER: consulta usuarios; gestiona su cuenta.

INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
JOIN permiso p ON p.codigo IN (
    'SECURITY_USER_READ',
    'ACCOUNT_SELF_READ',
    'ACCOUNT_SELF_UPDATE_PASSWORD'
)
WHERE r.codigo = 'SELF_EVALUATION_COMMITTEE_MEMBER'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;

-- INFORMATION_PROVIDER: solo gestiona su propia cuenta.

INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
JOIN permiso p ON p.codigo IN (
    'ACCOUNT_SELF_READ',
    'ACCOUNT_SELF_UPDATE_PASSWORD'
)
WHERE r.codigo = 'INFORMATION_PROVIDER'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;
