-- =================================================================================================
-- SCRIPT DE DATOS INICIALES - USUARIOS DE PRUEBA
-- =================================================================================================

-- Contraseña para todos los usuarios: "password123"
-- El hash BCrypt es: $2a$10$wPHxwfsfTnOJAdgYcerBt.utdAvC24B/DWfuXfzKBSDHO0etB1ica

-- 1. Insertar Usuarios
INSERT INTO users (email, password, user_type, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at)
VALUES
('admin@rockstadium.com', '$2a$10$wPHxwfsfTnOJAdgYcerBt.utdAvC24B/DWfuXfzKBSDHO0etB1ica', 'ADMIN', true, true, true, true, NOW()),
('mario@test.com', '$2a$10$wPHxwfsfTnOJAdgYcerBt.utdAvC24B/DWfuXfzKBSDHO0etB1ica', 'USER', true, true, true, true, NOW()),
('luigi@test.com', '$2a$10$wPHxwfsfTnOJAdgYcerBt.utdAvC24B/DWfuXfzKBSDHO0etB1ica', 'USER', true, true, true, true, NOW());

-- 2. Asignar Roles
-- Admin tiene ROLE_ADMIN y ROLE_USER
INSERT INTO user_roles (user_id, role)
VALUES
((SELECT user_id FROM users WHERE email = 'admin@rockstadium.com'), 'ROLE_ADMIN'),
((SELECT user_id FROM users WHERE email = 'admin@rockstadium.com'), 'ROLE_USER'),
-- Mario y Luigi son solo ROLE_USER
((SELECT user_id FROM users WHERE email = 'mario@test.com'), 'ROLE_USER'),
((SELECT user_id FROM users WHERE email = 'luigi@test.com'), 'ROLE_USER');

-- 3. Crear Perfiles Básicos (Opcional)
INSERT INTO profiles (first_name, last_name, bio, user_id)
VALUES
('Admin', 'User', 'System Administrator', (SELECT user_id FROM users WHERE email = 'admin@rockstadium.com')),
('Mario', 'Bros', 'It''s me, Mario!', (SELECT user_id FROM users WHERE email = 'mario@test.com')),
('Luigi', 'Bros', 'Player 2', (SELECT user_id FROM users WHERE email = 'luigi@test.com'));
