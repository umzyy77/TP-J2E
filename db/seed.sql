-- Données de test pour MasterAnnonce

-- Utilisateurs avec mots de passe hachés BCrypt
-- Hash généré avec BCrypt, 12 rounds (password: password123)
INSERT INTO users (id, username, email, password, created_at) VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin', 'admin@masterannonce.fr', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('22222222-2222-2222-2222-222222222222', 'jean', 'jean@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('33333333-3333-3333-3333-333333333333', 'marie', 'marie@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW());

-- Catégories
INSERT INTO category (id, label) VALUES
    ('aaaa0001-0001-0001-0001-000000000001', 'Immobilier'),
    ('aaaa0002-0002-0002-0002-000000000002', 'Emploi'),
    ('aaaa0003-0003-0003-0003-000000000003', 'Services'),
    ('aaaa0004-0004-0004-0004-000000000004', 'Vehicules'),
    ('aaaa0005-0005-0005-0005-000000000005', 'Formation');

-- Annonces de test
INSERT INTO annonce (title, description, adress, mail, date, status, author_id, category_id) VALUES
    ('Location studio Paris', 'Studio lumineux proche transports, 25m2.', '12 rue des Lilas, Paris', 'contact@studio.fr', NOW(), 'PUBLISHED', '11111111-1111-1111-1111-111111111111', 'aaaa0001-0001-0001-0001-000000000001'),
    ('Cours de Java', 'Cours particuliers Java/Jakarta pour debutants.', 'Universite Paris 13', 'java.tutor@mail.com', NOW(), 'PUBLISHED', '22222222-2222-2222-2222-222222222222', 'aaaa0005-0005-0005-0005-000000000005'),
    ('Vente Renault Clio', 'Clio 2018, 50000km, tres bon etat.', '45 avenue Jean Jaures, Bobigny', 'velo@annonces.com', NOW(), 'PUBLISHED', '33333333-3333-3333-3333-333333333333', 'aaaa0004-0004-0004-0004-000000000004'),
    ('Colocation 3 pieces', 'Chambre disponible dans appartement 3 pieces.', '7 rue Victor Hugo, Saint-Denis', 'coloc@annonce.fr', NOW(), 'DRAFT', '22222222-2222-2222-2222-222222222222', 'aaaa0001-0001-0001-0001-000000000001'),
    ('Stage dev web', 'Stage 3 mois en developpement web React/Node.', 'Campus Galilee', 'stage@entreprise.fr', NOW(), 'PUBLISHED', '11111111-1111-1111-1111-111111111111', 'aaaa0002-0002-0002-0002-000000000002'),
    ('Aide demenagement', 'Besoin d aide pour demenager samedi.', '18 rue de la Republique, Pantin', 'demenagement@help.fr', NOW(), 'ARCHIVED', '33333333-3333-3333-3333-333333333333', 'aaaa0003-0003-0003-0003-000000000003');
