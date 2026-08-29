-- =============================================================================
-- V1__init.sql  --  Esquema inicial de seguridad (Etapa 3, iteracion 1)
-- =============================================================================
-- Decisiones de modelado (no obvias):
--   * Tablas en español (usuario, rol, permiso) segun el dominio del negocio.
--     Las columnas de conceptos del dominio se nombran en español (codigo,
--     nombre, descripcion, activo, es_sistema). Los identificadores tecnicos
--     estandar se conservan en snake_case ingles (username, email,
--     password_hash) porque son terminos fijos del modelo User.
--   * UUID nativo PostgreSQL: tipo UUID + DEFAULT gen_random_uuid()
--     (disponible en PG >= 13 sin extension pgcrypto).
--   * es_sistema modela entidades no eliminables/desactivables por reglas del
--     dominio (p.ej. Role.isSystemRole). Se aplica a rol y usuario porque el
--     alcance aprobado lo requiere en ambos, aunque la entidad User actual aun
--     no exponga ese flag (la columna queda lista para iteraciones posteriores).
--   * Timestamps: solo creado_en (alta). No se agrega actualizado_en ni trigger
--     en esta iteracion; el refresco de "ultima modificacion" se delega a la
--     capa de aplicacion para mantener el esquema inicial minimo y honesto.
--   * Sin seed ni usuario admin: explicitamente fuera de alcance en esta
--     iteracion (se insertaran en migraciones posteriores).
--   * Claves foraneas de union: ON DELETE CASCADE en rol_permiso y usuario_rol
--     para que borrar el padre limpie las asignaciones. El borrado de un rol
--     con asignaciones activas se controla en el dominio, no con RESTRICT.
-- =============================================================================

-- ----------------------------------------------------------------------------
-- permiso
-- ----------------------------------------------------------------------------
CREATE TABLE permiso (
    id           UUID          NOT NULL DEFAULT gen_random_uuid(),
    codigo       VARCHAR(100)  NOT NULL,
    nombre       VARCHAR(255)  NOT NULL,
    descripcion  VARCHAR(255)  NOT NULL,
    creado_en    TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_permiso PRIMARY KEY (id),
    CONSTRAINT ck_permiso_codigo_formato
        CHECK (codigo ~ '^[A-Z0-9]+(_[A-Z0-9]+)*$')
);

CREATE UNIQUE INDEX uq_permiso_codigo ON permiso (codigo);

-- ----------------------------------------------------------------------------
-- rol
-- ----------------------------------------------------------------------------
CREATE TABLE rol (
    id           UUID          NOT NULL DEFAULT gen_random_uuid(),
    codigo       VARCHAR(100)  NOT NULL,
    nombre       VARCHAR(255)  NOT NULL,
    descripcion  VARCHAR(255)  NOT NULL,
    activo       BOOLEAN       NOT NULL DEFAULT TRUE,
    es_sistema   BOOLEAN       NOT NULL DEFAULT FALSE,
    creado_en    TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_rol PRIMARY KEY (id),
    CONSTRAINT ck_rol_codigo_formato
        CHECK (codigo ~ '^[A-Z0-9]+(_[A-Z0-9]+)*$'),
    CONSTRAINT ck_rol_sistema_activo
        CHECK (NOT es_sistema OR activo)
);

CREATE UNIQUE INDEX uq_rol_codigo ON rol (codigo);

-- ----------------------------------------------------------------------------
-- usuario
-- ----------------------------------------------------------------------------
CREATE TABLE usuario (
    id            UUID          NOT NULL DEFAULT gen_random_uuid(),
    username      VARCHAR(255)  NOT NULL,
    email         VARCHAR(320)  NOT NULL,  -- 320 = limite RFC 5321
    password_hash VARCHAR(255)  NOT NULL,
    activo        BOOLEAN       NOT NULL DEFAULT TRUE,
    es_sistema    BOOLEAN       NOT NULL DEFAULT FALSE,
    creado_en     TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT ck_usuario_email_formato
        CHECK (email ~ '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$'),
    CONSTRAINT ck_usuario_sistema_activo
        CHECK (NOT es_sistema OR activo)
);

CREATE UNIQUE INDEX uq_usuario_username ON usuario (username);
CREATE UNIQUE INDEX uq_usuario_email    ON usuario (email);

-- ----------------------------------------------------------------------------
-- usuario_rol  (asignacion N:M usuario <-> rol)
-- ----------------------------------------------------------------------------
CREATE TABLE usuario_rol (
    usuario_id  UUID        NOT NULL,
    rol_id      UUID        NOT NULL,
    creado_en   TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_usuario_rol PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_rol_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_rol_rol
        FOREIGN KEY (rol_id) REFERENCES rol (id) ON DELETE CASCADE
);

-- Indice de busqueda inversa: roles de un usuario ya esta cubierto por el
-- prefijo de la PK (usuario_id, rol_id). Este indice acelera "usuarios de un rol".
CREATE INDEX ix_usuario_rol_rol_id ON usuario_rol (rol_id);

-- ----------------------------------------------------------------------------
-- rol_permiso  (asignacion N:M rol <-> permiso)
-- ----------------------------------------------------------------------------
CREATE TABLE rol_permiso (
    rol_id      UUID        NOT NULL,
    permiso_id  UUID        NOT NULL,
    creado_en   TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_rol_permiso PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rol_permiso_rol
        FOREIGN KEY (rol_id) REFERENCES rol (id) ON DELETE CASCADE,
    CONSTRAINT fk_rol_permiso_permiso
        FOREIGN KEY (permiso_id) REFERENCES permiso (id) ON DELETE CASCADE
);

-- Busqueda inversa: permisos de un rol cubiertos por prefijo de PK.
-- Este indice acelera "roles que usan un permiso".
CREATE INDEX ix_rol_permiso_permiso_id ON rol_permiso (permiso_id);
