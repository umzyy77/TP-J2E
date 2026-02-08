-- Données de test pour MasterAnnonce

-- Utilisateurs avec mots de passe hachés BCrypt
-- Hash généré avec BCrypt, 12 rounds (password: password123)
INSERT INTO users (id, username, email, password, created_at) VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin', 'admin@masterannonce.fr', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('22222222-2222-2222-2222-222222222222', 'jean', 'jean@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('33333333-3333-3333-3333-333333333333', 'marie', 'marie@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('44444444-4444-4444-4444-444444444444', 'lucas', 'lucas@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('55555555-5555-5555-5555-555555555555', 'sophie', 'sophie@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('66666666-6666-6666-6666-666666666666', 'karim', 'karim@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('77777777-7777-7777-7777-777777777777', 'claire', 'claire@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW()),
    ('88888888-8888-8888-8888-888888888888', 'mehdi', 'mehdi@example.com', '$2a$12$1CW7XKT9Hn/4yDlV4qVCmOayeRFfzlgeakfBvLZUYelmFc18Pi.GS', NOW());

-- Catégories
INSERT INTO category (id, label) VALUES
    ('aaaa0001-0001-0001-0001-000000000001', 'Immobilier'),
    ('aaaa0002-0002-0002-0002-000000000002', 'Emploi'),
    ('aaaa0003-0003-0003-0003-000000000003', 'Services'),
    ('aaaa0004-0004-0004-0004-000000000004', 'Vehicules'),
    ('aaaa0005-0005-0005-0005-000000000005', 'Formation');

-- Annonces de test (39 annonces)
INSERT INTO annonce (title, description, adress, mail, date, status, author_id, category_id) VALUES

-- === IMMOBILIER (8 annonces) ===
('Location studio Paris 15e', 'Studio lumineux proche transports, 25m2, cuisine equipee.', '12 rue des Lilas, Paris 15', 'contact@studio.fr', NOW() - INTERVAL '1 day', 'PUBLISHED', '11111111-1111-1111-1111-111111111111', 'aaaa0001-0001-0001-0001-000000000001'),
('Colocation 3 pieces Saint-Denis', 'Chambre disponible dans appartement 3 pieces, 14m2.', '7 rue Victor Hugo, Saint-Denis', 'coloc@annonce.fr', NOW() - INTERVAL '2 days', 'DRAFT', '22222222-2222-2222-2222-222222222222', 'aaaa0001-0001-0001-0001-000000000001'),
('Appartement T3 Montreuil', 'T3 refait a neuf, balcon, proche metro Robespierre.', '25 boulevard Rouget de Lisle, Montreuil', 'appart.montreuil@mail.fr', NOW() - INTERVAL '3 days', 'PUBLISHED', '33333333-3333-3333-3333-333333333333', 'aaaa0001-0001-0001-0001-000000000001'),
('Studio meuble Belleville', 'Studio 20m2, meuble, 5 min metro Belleville.', '8 rue de Belleville, Paris 20', 'belleville@loc.fr', NOW() - INTERVAL '5 days', 'PUBLISHED', '44444444-4444-4444-4444-444444444444', 'aaaa0001-0001-0001-0001-000000000001'),
('Chambre chez l habitant Pantin', 'Chambre calme 12m2 dans maison, acces jardin.', '14 rue Hoche, Pantin', 'chambre.pantin@mail.fr', NOW() - INTERVAL '6 days', 'PUBLISHED', '55555555-5555-5555-5555-555555555555', 'aaaa0001-0001-0001-0001-000000000001'),
('Local commercial Aubervilliers', 'Local 80m2 ideal commerce ou bureau, RDC.', '3 avenue de la Republique, Aubervilliers', 'local.auber@pro.fr', NOW() - INTERVAL '7 days', 'DRAFT', '66666666-6666-6666-6666-666666666666', 'aaaa0001-0001-0001-0001-000000000001'),
('T2 lumineux Vincennes', 'Appartement 2 pieces, parquet, cave, proche RER A.', '19 rue de Fontenay, Vincennes', 'vincennes.t2@mail.fr', NOW() - INTERVAL '10 days', 'PUBLISHED', '77777777-7777-7777-7777-777777777777', 'aaaa0001-0001-0001-0001-000000000001'),
('Garage a louer Bobigny', 'Garage ferme securise, acces 24h/24.', '5 rue Pablo Neruda, Bobigny', 'garage.bob@mail.fr', NOW() - INTERVAL '12 days', 'ARCHIVED', '88888888-8888-8888-8888-888888888888', 'aaaa0001-0001-0001-0001-000000000001'),

-- === EMPLOI (8 annonces) ===
('Stage dev web React/Node', 'Stage 3 mois en developpement web React/Node.', 'Campus Galilee, Villetaneuse', 'stage@entreprise.fr', NOW() - INTERVAL '1 day', 'PUBLISHED', '11111111-1111-1111-1111-111111111111', 'aaaa0002-0002-0002-0002-000000000002'),
('CDI Developpeur Java Senior', 'Recherche dev Java 5 ans exp, Spring Boot, microservices.', '100 avenue de France, Paris 13', 'recrutement@techcorp.fr', NOW() - INTERVAL '2 days', 'PUBLISHED', '44444444-4444-4444-4444-444444444444', 'aaaa0002-0002-0002-0002-000000000002'),
('Alternance data analyst', 'Alternance 12 mois, Python, SQL, Power BI.', '55 rue du Faubourg, Paris 10', 'alternance@data.fr', NOW() - INTERVAL '3 days', 'PUBLISHED', '55555555-5555-5555-5555-555555555555', 'aaaa0002-0002-0002-0002-000000000002'),
('CDD Graphiste 6 mois', 'Graphiste maitrise Adobe Suite pour agence de com.', '12 rue de Rivoli, Paris 4', 'graphiste@agence.fr', NOW() - INTERVAL '5 days', 'DRAFT', '66666666-6666-6666-6666-666666666666', 'aaaa0002-0002-0002-0002-000000000002'),
('Freelance DevOps', 'Mission 4 mois, Docker, Kubernetes, CI/CD.', 'Remote / Paris', 'devops@freelance.fr', NOW() - INTERVAL '7 days', 'PUBLISHED', '77777777-7777-7777-7777-777777777777', 'aaaa0002-0002-0002-0002-000000000002'),
('Emploi saisonnier restauration', 'Serveur/serveuse pour saison estivale, exp souhaitee.', '8 place de la Bastille, Paris 12', 'resto.bastille@mail.fr', NOW() - INTERVAL '8 days', 'ARCHIVED', '88888888-8888-8888-8888-888888888888', 'aaaa0002-0002-0002-0002-000000000002'),
('Stage marketing digital', 'Stage 6 mois SEO, Google Ads, reseaux sociaux.', '40 rue Lafayette, Paris 9', 'marketing@startup.fr', NOW() - INTERVAL '9 days', 'PUBLISHED', '33333333-3333-3333-3333-333333333333', 'aaaa0002-0002-0002-0002-000000000002'),
('Technicien support informatique', 'Support N1/N2, Windows, reseau, ticketing.', '15 rue de Bercy, Paris 12', 'support@itservice.fr', NOW() - INTERVAL '11 days', 'PUBLISHED', '22222222-2222-2222-2222-222222222222', 'aaaa0002-0002-0002-0002-000000000002'),

-- === SERVICES (8 annonces) ===
('Aide demenagement samedi', 'Besoin d aide pour demenager samedi prochain.', '18 rue de la Republique, Pantin', 'demenagement@help.fr', NOW() - INTERVAL '1 day', 'ARCHIVED', '33333333-3333-3333-3333-333333333333', 'aaaa0003-0003-0003-0003-000000000003'),
('Plombier urgence 93', 'Plombier disponible 7j/7, depannage rapide.', 'Secteur Seine-Saint-Denis', 'plombier93@service.fr', NOW() - INTERVAL '2 days', 'PUBLISHED', '66666666-6666-6666-6666-666666666666', 'aaaa0003-0003-0003-0003-000000000003'),
('Garde d enfants mercredi', 'Nounou experimentee propose garde les mercredis.', '5 rue des Ecoles, Noisy-le-Sec', 'nounou@garde.fr', NOW() - INTERVAL '4 days', 'PUBLISHED', '55555555-5555-5555-5555-555555555555', 'aaaa0003-0003-0003-0003-000000000003'),
('Cours de guitare a domicile', 'Prof diplome, tous niveaux, acoustique et electrique.', 'Paris et proche banlieue', 'guitare@musique.fr', NOW() - INTERVAL '5 days', 'PUBLISHED', '44444444-4444-4444-4444-444444444444', 'aaaa0003-0003-0003-0003-000000000003'),
('Menage et repassage', 'Femme de menage serieuse, 3h/semaine minimum.', '93 / 75 Nord-Est', 'menage@propre.fr', NOW() - INTERVAL '6 days', 'PUBLISHED', '77777777-7777-7777-7777-777777777777', 'aaaa0003-0003-0003-0003-000000000003'),
('Depannage informatique', 'Reparation PC/Mac, virus, installation, a domicile.', 'Paris et 93', 'depannage@info.fr', NOW() - INTERVAL '8 days', 'DRAFT', '88888888-8888-8888-8888-888888888888', 'aaaa0003-0003-0003-0003-000000000003'),
('Photographe evenementiel', 'Mariages, anniversaires, portraits, tarifs pros.', 'Ile-de-France', 'photo@event.fr', NOW() - INTERVAL '10 days', 'PUBLISHED', '33333333-3333-3333-3333-333333333333', 'aaaa0003-0003-0003-0003-000000000003'),
('Petsitter vacances', 'Garde animaux a domicile pendant vos vacances.', '20 rue Voltaire, Montreuil', 'petsitter@animaux.fr', NOW() - INTERVAL '11 days', 'PUBLISHED', '22222222-2222-2222-2222-222222222222', 'aaaa0003-0003-0003-0003-000000000003'),

-- === VEHICULES (8 annonces) ===
('Vente Renault Clio 2018', 'Clio 2018, 50000km, tres bon etat, CT OK.', '45 avenue Jean Jaures, Bobigny', 'clio@annonces.com', NOW() - INTERVAL '1 day', 'PUBLISHED', '33333333-3333-3333-3333-333333333333', 'aaaa0004-0004-0004-0004-000000000004'),
('Peugeot 208 occasion', '208 2020, 30000km, essence, premiere main.', '10 rue de Paris, Drancy', 'peugeot208@vente.fr', NOW() - INTERVAL '3 days', 'PUBLISHED', '44444444-4444-4444-4444-444444444444', 'aaaa0004-0004-0004-0004-000000000004'),
('Scooter MBK 125cc', 'Scooter 125, 2019, 15000km, ideal trajet quotidien.', '33 rue Gabriel Peri, Saint-Ouen', 'scooter@vente.fr', NOW() - INTERVAL '4 days', 'PUBLISHED', '55555555-5555-5555-5555-555555555555', 'aaaa0004-0004-0004-0004-000000000004'),
('Velo electrique Decathlon', 'VTC electrique, batterie neuve, ideal velo-taf.', '2 place de la Gare, Aulnay-sous-Bois', 'velo.elec@mail.fr', NOW() - INTERVAL '6 days', 'PUBLISHED', '66666666-6666-6666-6666-666666666666', 'aaaa0004-0004-0004-0004-000000000004'),
('Volkswagen Golf VII TDI', 'Golf 7 2017, diesel, 80000km, full options.', '50 avenue Stalingrad, Stains', 'golf7@vente.fr', NOW() - INTERVAL '7 days', 'DRAFT', '77777777-7777-7777-7777-777777777777', 'aaaa0004-0004-0004-0004-000000000004'),
('Fiat 500 rose 2021', 'Fiat 500 hybride, 20000km, parfait etat, garantie.', '6 rue Jean Moulin, Le Raincy', 'fiat500@vente.fr', NOW() - INTERVAL '9 days', 'PUBLISHED', '88888888-8888-8888-8888-888888888888', 'aaaa0004-0004-0004-0004-000000000004'),
('Citroen C3 Aircross', 'C3 Aircross 2019, SUV compact, 45000km, GPS.', '17 rue de Meaux, Sevran', 'citroen@vente.fr', NOW() - INTERVAL '10 days', 'PUBLISHED', '11111111-1111-1111-1111-111111111111', 'aaaa0004-0004-0004-0004-000000000004'),
('Trottinette electrique Xiaomi', 'Xiaomi Pro 2, bon etat, autonomie 40km.', '9 rue du Commerce, Romainville', 'trott@vente.fr', NOW() - INTERVAL '12 days', 'ARCHIVED', '22222222-2222-2222-2222-222222222222', 'aaaa0004-0004-0004-0004-000000000004'),

-- === FORMATION (7 annonces) ===
('Cours de Java Jakarta EE', 'Cours particuliers Java/Jakarta pour debutants.', 'Universite Paris 13, Villetaneuse', 'java.tutor@mail.com', NOW() - INTERVAL '1 day', 'PUBLISHED', '22222222-2222-2222-2222-222222222222', 'aaaa0005-0005-0005-0005-000000000005'),
('Formation Python intensif', 'Formation Python 5 jours, de zero a autonome.', '15 rue du Progres, Montreuil', 'python@formation.fr', NOW() - INTERVAL '3 days', 'PUBLISHED', '44444444-4444-4444-4444-444444444444', 'aaaa0005-0005-0005-0005-000000000005'),
('Preparation TOEIC anglais', 'Cours collectifs TOEIC, objectif 800+, 10 seances.', '3 place du Marche, Noisy-le-Grand', 'toeic@english.fr', NOW() - INTERVAL '4 days', 'PUBLISHED', '55555555-5555-5555-5555-555555555555', 'aaaa0005-0005-0005-0005-000000000005'),
('Atelier cuisine japonaise', 'Apprenez sushi, ramen, gyoza, cours de 3h.', '11 rue de Tokyo, Paris 16', 'cuisine@japon.fr', NOW() - INTERVAL '6 days', 'PUBLISHED', '66666666-6666-6666-6666-666666666666', 'aaaa0005-0005-0005-0005-000000000005'),
('Formation Photoshop debutant', 'Maitrisez Photoshop en 4 seances, TP pratiques.', '20 rue des Arts, Pantin', 'photoshop@design.fr', NOW() - INTERVAL '8 days', 'DRAFT', '77777777-7777-7777-7777-777777777777', 'aaaa0005-0005-0005-0005-000000000005'),
('Cours de maths lycee/prepa', 'Prof agrege donne cours maths, tous niveaux.', 'Paris 5e et visio', 'maths@prof.fr', NOW() - INTERVAL '9 days', 'PUBLISHED', '88888888-8888-8888-8888-888888888888', 'aaaa0005-0005-0005-0005-000000000005'),
('Formation permis moto A2', 'Auto-ecole, formation acceleree permis moto A2.', '4 rue de la Liberte, Livry-Gargan', 'moto@permis.fr', NOW() - INTERVAL '11 days', 'ARCHIVED', '33333333-3333-3333-3333-333333333333', 'aaaa0005-0005-0005-0005-000000000005');
