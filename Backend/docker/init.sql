-- =============================================
-- Red Mesh - Esquema de Base de Datos
-- =============================================

-- 1. Habilitar extensión para generar UUIDs 
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Crear el tipo ENUM para los estados del mensaje
CREATE TYPE estado_mensaje AS ENUM (
    'EN_ESPERA',
    'TRANSITANDO',
    'RECIBIDO_EN_NUBE',
    'ENTREGADO'
);

-- 3. Tabla USUARIO
CREATE TABLE usuario (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_usuario VARCHAR(100) UNIQUE NOT NULL,
    public_key TEXT NOT NULL,
    is_online BOOLEAN DEFAULT FALSE,
    fecha_registro TIMESTAMP DEFAULT NOW()
);

-- 4. Tabla NODO_GATEWAY
CREATE TABLE nodo_gateway (
    id_dispositivo VARCHAR(255) PRIMARY KEY,
    bateria_nivel INTEGER,
    ultima_sincronizacion TIMESTAMP,
    ubicacion_lat DECIMAL(10, 7),
    ubicacion_lng DECIMAL(10, 7)
);

-- 5. Tabla MENSAJE
CREATE TABLE mensaje (
    uuid_mensaje UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    emisor_id UUID NOT NULL REFERENCES usuario(id),
    receptor_id UUID NOT NULL REFERENCES usuario(id),
    contenido_cifrado TEXT NOT NULL,
    tipo_mensaje VARCHAR(20) DEFAULT 'TEXTO',
    estado estado_mensaje DEFAULT 'EN_ESPERA',
    contador_saltos INTEGER DEFAULT 0,
    fecha_emision_movil TIMESTAMP NOT NULL,
    fecha_servidor TIMESTAMP DEFAULT NOW()
);

-- 6. Tabla MULTIMEDIA
CREATE TABLE multimedia (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    mensaje_id UUID NOT NULL REFERENCES mensaje(uuid_mensaje),
    url_almacenamiento VARCHAR(500) NOT NULL,
    peso_bytes BIGINT,
    thumbnail_base64 TEXT
);
