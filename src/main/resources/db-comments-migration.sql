-- ========================================
-- Script SQL para crear las tablas de comentarios
-- User Service - Azure AI Comments
-- ========================================

-- Tabla para comentarios generales sobre la plataforma
CREATE TABLE IF NOT EXISTS general_comments (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    comment TEXT NOT NULL,
    sentiment VARCHAR(20),
    confidence_positive DOUBLE PRECISION,
    confidence_neutral DOUBLE PRECISION,
    confidence_negative DOUBLE PRECISION,
    detected_language VARCHAR(10),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para general_comments
CREATE INDEX idx_general_comments_user_id ON general_comments(user_id);
CREATE INDEX idx_general_comments_sentiment ON general_comments(sentiment);
CREATE INDEX idx_general_comments_created_at ON general_comments(created_at DESC);

-- Comentar tabla con descripción
COMMENT ON TABLE general_comments IS 'Almacena comentarios generales sobre la plataforma con análisis de sentimiento de Azure AI';
COMMENT ON COLUMN general_comments.id IS 'ID único del comentario';
COMMENT ON COLUMN general_comments.user_id IS 'ID del usuario que hizo el comentario';
COMMENT ON COLUMN general_comments.comment IS 'Texto del comentario';
COMMENT ON COLUMN general_comments.sentiment IS 'Sentimiento detectado: positive, negative, neutral, mixed';
COMMENT ON COLUMN general_comments.confidence_positive IS 'Score de confianza para sentimiento positivo (0.0-1.0)';
COMMENT ON COLUMN general_comments.confidence_neutral IS 'Score de confianza para sentimiento neutral (0.0-1.0)';
COMMENT ON COLUMN general_comments.confidence_negative IS 'Score de confianza para sentimiento negativo (0.0-1.0)';
COMMENT ON COLUMN general_comments.detected_language IS 'Código ISO 639-1 del idioma detectado';
COMMENT ON COLUMN general_comments.created_at IS 'Marca de tiempo de creación';

-- ========================================

-- Tabla para comentarios sobre conciertos
CREATE TABLE IF NOT EXISTS concert_comments (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    concert_name VARCHAR(200) NOT NULL,
    comment TEXT NOT NULL,
    sentiment VARCHAR(20),
    confidence_positive DOUBLE PRECISION,
    confidence_neutral DOUBLE PRECISION,
    confidence_negative DOUBLE PRECISION,
    detected_language VARCHAR(10),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para concert_comments
CREATE INDEX idx_concert_comments_user_id ON concert_comments(user_id);
CREATE INDEX idx_concert_comments_concert_name ON concert_comments(concert_name);
CREATE INDEX idx_concert_comments_sentiment ON concert_comments(sentiment);
CREATE INDEX idx_concert_comments_created_at ON concert_comments(created_at DESC);

-- Comentar tabla con descripción
COMMENT ON TABLE concert_comments IS 'Almacena comentarios sobre conciertos con análisis de sentimiento de Azure AI';
COMMENT ON COLUMN concert_comments.id IS 'ID único del comentario';
COMMENT ON COLUMN concert_comments.user_id IS 'ID del usuario que hizo el comentario';
COMMENT ON COLUMN concert_comments.concert_name IS 'Nombre del concierto comentado';
COMMENT ON COLUMN concert_comments.comment IS 'Texto del comentario';
COMMENT ON COLUMN concert_comments.sentiment IS 'Sentimiento detectado: positive, negative, neutral, mixed';
COMMENT ON COLUMN concert_comments.confidence_positive IS 'Score de confianza para sentimiento positivo (0.0-1.0)';
COMMENT ON COLUMN concert_comments.confidence_neutral IS 'Score de confianza para sentimiento neutral (0.0-1.0)';
COMMENT ON COLUMN concert_comments.confidence_negative IS 'Score de confianza para sentimiento negativo (0.0-1.0)';
COMMENT ON COLUMN concert_comments.detected_language IS 'Código ISO 639-1 del idioma detectado';
COMMENT ON COLUMN concert_comments.created_at IS 'Marca de tiempo de creación';

-- ========================================
-- Fin del script
-- ========================================
