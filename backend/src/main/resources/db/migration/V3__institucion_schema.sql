-- =============================================================================
-- V3__institucion_schema.sql -- Modulo de Gestion Institucional y Geografia
-- =============================================================================

-- ----------------------------------------------------------------------------
-- Tablas Geograficas
-- ----------------------------------------------------------------------------
CREATE TABLE geografia_provincia (
    id        BIGINT GENERATED ALWAYS AS IDENTITY,
    nombre    VARCHAR(100) NOT NULL,
    codigo    VARCHAR(10)  NOT NULL,
    creado_en TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_geografia_provincia PRIMARY KEY (id),
    CONSTRAINT uq_geografia_provincia_codigo UNIQUE (codigo)
);

CREATE TABLE geografia_departamento (
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    provincia_id BIGINT       NOT NULL,
    nombre       VARCHAR(100) NOT NULL,
    creado_en    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_geografia_departamento PRIMARY KEY (id),
    CONSTRAINT fk_geografia_departamento_provincia
        FOREIGN KEY (provincia_id) REFERENCES geografia_provincia (id) ON DELETE CASCADE
);

CREATE INDEX ix_geografia_departamento_provincia_id ON geografia_departamento (provincia_id);

CREATE TABLE geografia_localidad (
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    departamento_id BIGINT       NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    codigo_postal   VARCHAR(20)  NOT NULL,
    creado_en       TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_geografia_localidad PRIMARY KEY (id),
    CONSTRAINT fk_geografia_localidad_departamento
        FOREIGN KEY (departamento_id) REFERENCES geografia_departamento (id) ON DELETE CASCADE
);

CREATE INDEX ix_geografia_localidad_departamento_id ON geografia_localidad (departamento_id);

-- ----------------------------------------------------------------------------
-- Semillas basicas de Geografia (La Rioja con detalle de Chilecito y Capital,
-- y provincias principales de Argentina)
-- ----------------------------------------------------------------------------
INSERT INTO geografia_provincia (nombre, codigo) VALUES
    ('La Rioja', 'LR'),
    ('Córdoba', 'CBA'),
    ('Buenos Aires', 'BA'),
    ('Ciudad Autónoma de Buenos Aires', 'CABA'),
    ('Catamarca', 'CA'),
    ('San Juan', 'SJ'),
    ('Mendoza', 'MZ'),
    ('Tucumán', 'TU'),
    ('Santa Fe', 'SF')
ON CONFLICT (codigo) DO NOTHING;

-- Departamentos de La Rioja
INSERT INTO geografia_departamento (provincia_id, nombre)
SELECT p.id, d.nombre
FROM geografia_provincia p
CROSS JOIN (VALUES
    ('Chilecito'),
    ('Capital'),
    ('Famatina'),
    ('Arauco'),
    ('Castro Barros'),
    ('Coronel Felipe Varela'),
    ('Chamical'),
    ('Rosario Vera Peñaloza')
) AS d(nombre)
WHERE p.codigo = 'LR';

-- Localidades de Chilecito (La Rioja)
INSERT INTO geografia_localidad (departamento_id, nombre, codigo_postal)
SELECT d.id, l.nombre, l.cp
FROM geografia_departamento d
JOIN geografia_provincia p ON d.provincia_id = p.id
CROSS JOIN (VALUES
    ('Chilecito', '5360'),
    ('Los Sarmientos', '5361'),
    ('San Miguel', '5363'),
    ('Anguinán', '5365'),
    ('Malligasta', '5363'),
    ('Tilimuqui', '5363'),
    ('Vichigasta', '5367'),
    ('Nonogasta', '5365')
) AS l(nombre, cp)
WHERE p.codigo = 'LR' AND d.nombre = 'Chilecito';

-- Localidades de Capital (La Rioja)
INSERT INTO geografia_localidad (departamento_id, nombre, codigo_postal)
SELECT d.id, 'La Rioja', '5300'
FROM geografia_departamento d
JOIN geografia_provincia p ON d.provincia_id = p.id
WHERE p.codigo = 'LR' AND d.nombre = 'Capital';

-- Localidades de Famatina (La Rioja)
INSERT INTO geografia_localidad (departamento_id, nombre, codigo_postal)
SELECT d.id, 'Famatina', '5361'
FROM geografia_departamento d
JOIN geografia_provincia p ON d.provincia_id = p.id
WHERE p.codigo = 'LR' AND d.nombre = 'Famatina';

-- Departamentos de Córdoba
INSERT INTO geografia_departamento (provincia_id, nombre)
SELECT p.id, d.nombre
FROM geografia_provincia p
CROSS JOIN (VALUES
    ('Capital'),
    ('Colón'),
    ('Punilla')
) AS d(nombre)
WHERE p.codigo = 'CBA';

INSERT INTO geografia_localidad (departamento_id, nombre, codigo_postal)
SELECT d.id, 'Córdoba', '5000'
FROM geografia_departamento d
JOIN geografia_provincia p ON d.provincia_id = p.id
WHERE p.codigo = 'CBA' AND d.nombre = 'Capital';

-- Departamentos de Buenos Aires
INSERT INTO geografia_departamento (provincia_id, nombre)
SELECT p.id, d.nombre
FROM geografia_provincia p
CROSS JOIN (VALUES
    ('La Plata'),
    ('General Pueyrredón')
) AS d(nombre)
WHERE p.codigo = 'BA';

INSERT INTO geografia_localidad (departamento_id, nombre, codigo_postal)
SELECT d.id, 'La Plata', '1900'
FROM geografia_departamento d
JOIN geografia_provincia p ON d.provincia_id = p.id
WHERE p.codigo = 'BA' AND d.nombre = 'La Plata';

-- Departamentos de CABA
INSERT INTO geografia_departamento (provincia_id, nombre)
SELECT p.id, 'Comuna 1'
FROM geografia_provincia p
WHERE p.codigo = 'CABA';

INSERT INTO geografia_localidad (departamento_id, nombre, codigo_postal)
SELECT d.id, 'Retiro', '1001'
FROM geografia_departamento d
JOIN geografia_provincia p ON d.provincia_id = p.id
WHERE p.codigo = 'CABA' AND d.nombre = 'Comuna 1';

-- ----------------------------------------------------------------------------
-- Tabla institucion
-- ----------------------------------------------------------------------------
CREATE TABLE institucion (
    id             UUID          NOT NULL DEFAULT gen_random_uuid(),
    nombre         VARCHAR(255)  NOT NULL,
    sigla          VARCHAR(50)   NOT NULL,
    logo_path      VARCHAR(500)  NOT NULL,
    activo         BOOLEAN       NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    actualizado_en TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_institucion PRIMARY KEY (id),
    CONSTRAINT uq_institucion_sigla UNIQUE (sigla)
);

CREATE UNIQUE INDEX uq_institucion_nombre ON institucion (nombre);

-- ----------------------------------------------------------------------------
-- Tabla institucion_autoridad
-- ----------------------------------------------------------------------------
CREATE TABLE institucion_autoridad (
    id              UUID          NOT NULL DEFAULT gen_random_uuid(),
    institucion_id  UUID          NOT NULL,
    tipo_autoridad  VARCHAR(50)   NOT NULL,
    apellido        VARCHAR(100)  NOT NULL,
    primer_nombre   VARCHAR(100)  NOT NULL,
    segundo_nombre  VARCHAR(100),
    telefono        VARCHAR(50)   NOT NULL,
    email           VARCHAR(320)  NOT NULL,
    cargo           VARCHAR(150),
    ambito          VARCHAR(150),
    creado_en       TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_institucion_autoridad PRIMARY KEY (id),
    CONSTRAINT fk_institucion_autoridad_institucion
        FOREIGN KEY (institucion_id) REFERENCES institucion (id) ON DELETE CASCADE,
    CONSTRAINT ck_institucion_autoridad_tipo
        CHECK (tipo_autoridad IN ('MAXIMA_AUTORIDAD', 'ADMINISTRADOR_INSTITUCIONAL')),
    CONSTRAINT ck_institucion_autoridad_email
        CHECK (email ~ '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$')
);

CREATE INDEX ix_institucion_autoridad_institucion_id ON institucion_autoridad (institucion_id);

-- ----------------------------------------------------------------------------
-- Tabla institucion_sede
-- ----------------------------------------------------------------------------
CREATE TABLE institucion_sede (
    id               UUID          NOT NULL DEFAULT gen_random_uuid(),
    institucion_id   UUID          NOT NULL,
    es_sede_central  BOOLEAN       NOT NULL DEFAULT TRUE,
    calle            VARCHAR(255)  NOT NULL,
    numero           VARCHAR(50)   NOT NULL,
    piso             VARCHAR(20),
    departamento     VARCHAR(20),
    codigo_postal    VARCHAR(20)   NOT NULL,
    provincia_id     BIGINT        NOT NULL,
    departamento_id  BIGINT        NOT NULL,
    localidad_id     BIGINT        NOT NULL,
    creado_en        TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_institucion_sede PRIMARY KEY (id),
    CONSTRAINT fk_institucion_sede_institucion
        FOREIGN KEY (institucion_id) REFERENCES institucion (id) ON DELETE CASCADE,
    CONSTRAINT fk_institucion_sede_provincia
        FOREIGN KEY (provincia_id) REFERENCES geografia_provincia (id),
    CONSTRAINT fk_institucion_sede_departamento
        FOREIGN KEY (departamento_id) REFERENCES geografia_departamento (id),
    CONSTRAINT fk_institucion_sede_localidad
        FOREIGN KEY (localidad_id) REFERENCES geografia_localidad (id)
);

CREATE INDEX ix_institucion_sede_institucion_id ON institucion_sede (institucion_id);

-- ----------------------------------------------------------------------------
-- Permisos y Roles de Seguridad para Institucion
-- ----------------------------------------------------------------------------
INSERT INTO permiso (codigo, nombre, descripcion) VALUES
    ('INSTITUCION_READ',
     'Consultar instituciones',
     'Permite listar y consultar instituciones y catálogos geográficos.'),
    ('INSTITUCION_WRITE',
     'Registrar y editar instituciones',
     'Permite crear y actualizar instituciones.')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO rol (codigo, nombre, descripcion, es_sistema) VALUES
    ('AEA',
     'Área de Evaluación y Acreditación',
     'Gestión institucional y administración de procesos de evaluación y acreditación.',
     TRUE)
ON CONFLICT (codigo) DO NOTHING;

-- Asignar permisos a ADMINISTRATOR
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
JOIN permiso p ON p.codigo IN ('INSTITUCION_READ', 'INSTITUCION_WRITE')
WHERE r.codigo = 'ADMINISTRATOR'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;

-- Asignar permisos a AEA (todos los permisos de ADMINISTRATOR mas INSTITUCION_READ e INSTITUCION_WRITE)
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
    'ACCOUNT_SELF_UPDATE_PASSWORD',
    'INSTITUCION_READ',
    'INSTITUCION_WRITE'
)
WHERE r.codigo = 'AEA'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;

-- Asignar el rol AEA al usuario semilla admin@undec.edu.ar
INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuario u
JOIN rol r ON r.codigo = 'AEA'
WHERE u.email = 'admin@undec.edu.ar'
ON CONFLICT (usuario_id, rol_id) DO NOTHING;
