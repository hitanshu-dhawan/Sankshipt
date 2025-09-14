-- Initialize databases for Sankshipt
CREATE DATABASE IF NOT EXISTS sankshipt_db;
CREATE DATABASE IF NOT EXISTS sankshipt_auth_db;

-- Grant privileges to root user
GRANT ALL PRIVILEGES ON sankshipt_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON sankshipt_auth_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- Seed roles in auth database
INSERT INTO sankshipt_auth_db.roles (created_at, updated_at, value)
VALUES
    (NOW(), NOW(), 'ADMIN'),
    (NOW(), NOW(), 'USER');
