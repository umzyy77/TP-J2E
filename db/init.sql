-- Schema JPA/Hibernate pour MasterAnnonce
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Table Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table Category
CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    label VARCHAR(100) NOT NULL UNIQUE
);

-- Table Annonce
CREATE TABLE IF NOT EXISTS annonce (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(64) NOT NULL,
    description VARCHAR(256) NOT NULL,
    adress VARCHAR(64) NOT NULL,
    mail VARCHAR(64) NOT NULL,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    author_id UUID REFERENCES users(id),
    category_id UUID REFERENCES category(id)
);

-- Index pour les recherches
CREATE INDEX IF NOT EXISTS idx_annonce_status ON annonce(status);
CREATE INDEX IF NOT EXISTS idx_annonce_date ON annonce(date DESC);
CREATE INDEX IF NOT EXISTS idx_annonce_author ON annonce(author_id);
CREATE INDEX IF NOT EXISTS idx_annonce_category ON annonce(category_id);
