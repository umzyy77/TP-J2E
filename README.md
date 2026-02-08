# MasterAnnonce - Application Web JPA/Hibernate

Application de gestion d'annonces développée avec Jakarta EE 10, JPA/Hibernate et PostgreSQL.

**Statut :** MVP (TP universitaire)
**Périmètre :** Backend + Frontend JSP, authentification, CRUD annonces avec recherche/filtrage/pagination.

## Prérequis & versions

- **Java 25** ou supérieur
- **Maven 3.8+**
- **PostgreSQL 14+** (ou Docker)
- **Apache Tomcat 10.1+** (compatible Jakarta EE 10)

## Installation

```bash
# Cloner le projet
git clone https://github.com/umzyy77/TP-J2E.git
cd TP-J2E

# Compiler
mvn clean compile
```

## Configuration

### Base de données (persistence.xml)

La configuration JPA se trouve dans `src/main/resources/META-INF/persistence.xml` :

| Propriété | Valeur |
|-----------|--------|
| Persistence Unit | `MasterAnnoncePU` |
| Driver | `org.postgresql.Driver` |
| URL | `jdbc:postgresql://localhost:5432/MasterAnnonce` |
| User / Password | `postgres` / `postgres` |
| Dialect | `PostgreSQLDialect` |
| DDL auto | `update` |

### Tests (H2 in-memory)

Les tests utilisent une base H2 en mémoire configurée dans `src/test/resources/META-INF/persistence.xml` avec `hbm2ddl.auto=create-drop`.

## Lancement en local

### Option 1 : Docker Compose (base de données)

```bash
# Lancer PostgreSQL avec init + seed automatiques
docker-compose up -d

# Arrêter et supprimer les volumes
docker-compose down -v
```

Le `docker-compose.yml` lance uniquement PostgreSQL et exécute automatiquement `db/init.sql` puis `db/seed.sql` au premier démarrage.

### Option 2 : PostgreSQL local

```bash
# Créer la base
psql -U postgres -c "CREATE DATABASE MasterAnnonce;"

# Initialiser le schéma et les données de test
psql -U postgres -d MasterAnnonce -f db/init.sql
psql -U postgres -d MasterAnnonce -f db/seed.sql
```

### Démarrer l'application

```bash
# créer le WAR et le déployer manuellement
mvn clean package -DskipTests

# Via le plugin Cargo (Tomcat embarqué, port 8080)
mvn cargo:run
```

Accès : `http://localhost:8080/MasterAnnonce/`

## Architecture du code

```
src/main/java/org/example/tpj2eannonces/
├── config/          # AppContextListener (init/destroy EntityManagerFactory)
├── exception/       # DatabaseException, ValidationException
├── filter/          # AuthenticationFilter, EncodingFilter
├── model/           # Entités JPA (User, Category, Annonce, AnnonceStatus)
├── repository/      # Couche persistence JPQL (+ RepositoryException)
├── service/         # Couche métier + transactions (+ ServiceException)
├── servlet/         # Contrôleurs Web (Servlets)
│   ├── annonce/     # AnnonceList, AnnonceDetail, AnnonceAdd, AnnoncePatch, AnnonceDelete
│   └── auth/        # Login, Register, Logout
└── utils/           # JPAUtil, ValidationUtils, PasswordUtils
```

### Couches applicatives

| Couche | Responsabilité |
|--------|----------------|
| **Servlet** | Gestion HTTP, validation entrées, délégation au service |
| **Service** | Logique métier, gestion des transactions (via `JPAUtil.inTransaction` / `inReadOnly`) |
| **Repository** | Accès aux données via JPQL exclusivement |
| **Model** | Entités JPA avec Bean Validation |

## Technologies & dépendances

| Dépendance | Version | Rôle |
|------------|---------|------|
| Jakarta Servlet API | 6.0.0 | API Servlet |
| Jakarta JSTL | 3.0.1 | Tags JSP |
| Hibernate Core | 6.6.4.Final | Implémentation JPA |
| Jakarta Persistence API | 3.1.0 | Spécification JPA |
| PostgreSQL Driver | 42.7.7 | Driver JDBC |
| Hibernate Validator | 8.0.1.Final | Bean Validation |
| jBCrypt | 0.4 | Hachage mot de passe |
| SLF4J | 2.0.16 | Logging |
| Tomcat Embed | 10.1.50 | Serveur embarqué |
| JUnit 5 | 5.11.0 | Tests |
| Mockito | 5.14.2 | Mocks |
| AssertJ | 3.27.3 | Assertions fluides |
| H2 | 2.3.232 | Base de tests |
| JaCoCo | 0.8.14 | Couverture de code |

## Modèle de données

### User (`users`)

| Champ | Type | Contraintes |
|-------|------|-------------|
| id | UUID | PK, auto-généré |
| username | String (50) | unique, @NotBlank, @Size(3-50) |
| email | String (100) | unique, @Email |
| password | String (255) | @NotBlank, haché BCrypt |
| createdAt | LocalDateTime | auto via @PrePersist |

### Category (`category`)

| Champ | Type | Contraintes |
|-------|------|-------------|
| id | UUID | PK, auto-généré |
| label | String (50) | unique, @NotBlank |

### Annonce (`annonce`)

| Champ | Type | Contraintes |
|-------|------|-------------|
| id | UUID | PK, auto-généré |
| title | String (64) | @NotBlank, @Size(max=64) |
| description | String (256) | @NotBlank, @Size(max=256) |
| adress | String (64) | @NotBlank, @Size(max=64) |
| mail | String (64) | @Email, @Size(max=64) |
| date | LocalDateTime | auto via @PrePersist / @PreUpdate |
| status | AnnonceStatus | ENUM(DRAFT, PUBLISHED, ARCHIVED), default DRAFT |
| author | User | @ManyToOne LAZY |
| category | Category | @ManyToOne LAZY |

### Relations

- `User` 1 --- * `Annonce` (author)
- `Category` 1 --- * `Annonce`

### Cycle de vie des statuts

```
DRAFT --[publish]--> PUBLISHED --[archive]--> ARCHIVED
```

## Routes (Servlets)

| Méthode | URL | Servlet | Description | Auth requise |
|---------|-----|---------|-------------|--------------|
| GET | `/login` | LoginServlet | Formulaire de connexion | Non |
| POST | `/login` | LoginServlet | Authentification | Non |
| GET | `/register` | RegisterServlet | Formulaire d'inscription | Non |
| POST | `/register` | RegisterServlet | Création de compte | Non |
| GET | `/logout` | LogoutServlet | Déconnexion | Oui |
| GET | `/AnnonceList` | AnnonceListServlet | Liste paginée (filtres: `q`, `category`, `status`, `author`) | Non |
| GET | `/AnnonceDetail` | AnnonceDetailServlet | Détail annonce (`?id=UUID`) | Non |
| GET | `/AnnonceAdd` | AnnonceAddServlet | Formulaire de création | Oui |
| POST | `/AnnonceAdd` | AnnonceAddServlet | Créer une annonce | Oui |
| GET | `/AnnoncePatch` | AnnoncePatchServlet | Formulaire de modification | Oui |
| POST | `/AnnoncePatch` | AnnoncePatchServlet | Modifier / publier / archiver | Oui |
| POST | `/AnnonceDelete` | AnnonceDeleteServlet | Supprimer une annonce | Oui |

## Sécurité

- **Authentification** : session HTTP (`loggedUser`), timeout 30 min
- **AuthenticationFilter** (`/*`) : protège toutes les routes sauf `/login`, `/logout`, `/register`, `/AnnonceList`, `/AnnonceDetail`, `/index.jsp` et les ressources statiques
- **Mots de passe** : hachés avec BCrypt (12 rounds) via `PasswordUtils`
- **EncodingFilter** (`/*`) : force UTF-8 sur toutes les requêtes/réponses

## Validation & gestion des erreurs

### Validation

- **Bean Validation** : annotations `@NotBlank`, `@Size`, `@Email`, `@NotNull` sur les entités
- **ValidationUtils** : validation côté servlet (title, description, adress, email, UUID)
- **Côté client** : attributs HTML5 (`minlength`, `type="email"`, etc.)

### Exceptions

| Exception | Package | Usage |
|-----------|---------|-------|
| `ValidationException` | exception | Erreurs de validation (entrées utilisateur) |
| `DatabaseException` | exception | Erreurs base de données |
| `ServiceException` | service | Erreurs métier (doublons, contraintes) |
| `RepositoryException` | repository | Erreurs d'accès aux données (entité introuvable) |

### Pages d'erreur

- **404** : `/WEB-INF/jsp/features/errors/pages/404.jsp`
- **500** : `/WEB-INF/jsp/features/errors/pages/500.jsp`

Les valeurs saisies sont conservées dans les formulaires en cas d'erreur.

## Tests

### Exécution

```bash
mvn test
```

### Couverture

| Classe de test | Couche | Nombre de tests |
|---------------|--------|-----------------|
| JPAUtilTest | Utils | 4 |
| EntityMappingTest | Model | 8 |
| UserRepositoryTest | Repository | 5 |
| AnnonceRepositoryTest | Repository | 12 |
| CategoryServiceTest | Service | 4 |
| AnnonceServiceTest | Service | 9 |
| **Total** | | **42** |

Les tests utilisent une base H2 in-memory (`create-drop`) et Mockito pour les mocks.

### SonarQube

```bash
mvn sonar:sonar
```

Configuration dans `sonar-project.properties` (projet: `master-annonce`). Rapports JaCoCo générés automatiquement.

## Scripts SQL

Les scripts se trouvent dans le dossier `db/` :

| Fichier | Description |
|---------|-------------|
| `db/init.sql` | Création du schéma (tables, index, extension pgcrypto) |
| `db/seed.sql` | Données de test |

### Données de test (seed.sql)

| Donnée | Quantité | Détails |
|--------|----------|---------|
| **Utilisateurs** | 8 | admin, jean, marie, lucas, sophie, karim, claire, mehdi |
| **Catégories** | 5 | Immobilier, Emploi, Services, Vehicules, Formation |
| **Annonces** | 39 | 26 PUBLISHED, 7 DRAFT, 6 ARCHIVED |

Mot de passe de tous les comptes : `password123` (pour les tests)

## Commandes utiles

```bash
# Compiler
mvn clean compile

# Lancer les tests
mvn test

# Créer le WAR
mvn package -DskipTests

# Lancer Tomcat embarqué
mvn cargo:run

# Lancer PostgreSQL (Docker)
docker-compose up -d

# Reset complet de la base (Docker)
docker-compose down -v && docker-compose up -d

# SonarQube
mvn sonar:sonar
```

## Problèmes rencontrés et solutions

### 1. Entités non détectées par Hibernate

**Problème** : Hibernate ne trouvait pas les entités au démarrage.

**Solution** : Déclaration explicite dans `persistence.xml` :
```xml
<class>org.example.tpj2eannonces.model.Annonce</class>
<class>org.example.tpj2eannonces.model.User</class>
<class>org.example.tpj2eannonces.model.Category</class>
```

### 2. Transactions dans les servlets

**Problème** : Ouverture de transactions dans les servlets causant des fuites de ressources.

**Solution** : Transactions gérées exclusivement dans la couche Service via `JPAUtil.inTransaction()` et `JPAUtil.inReadOnly()` avec commit/rollback automatique.

### 3. LazyInitializationException

**Problème** : Accès aux relations (`author`, `category`) après fermeture de l'EntityManager.

**Solution** : Utilisation de `LEFT JOIN FETCH` dans les requêtes JPQL :
```java
"SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.id = :id"
```

### 4. UUID comme clé primaire

**Problème** : Migration de l'existant vers JPA avec des UUID.

**Solution** : `@GeneratedValue(strategy = GenerationType.UUID)` pour la génération automatique et la compatibilité avec PostgreSQL.

### 5. Suppression de catégories avec annonces liées

**Problème** : Violation de contrainte d'intégrité référentielle.

**Solution** : Vérification dans `CategoryService.delete()` du nombre d'annonces liées avant suppression, avec message d'erreur explicite.

## Structure complète du projet

```
TP-J2E/
├── db/
│   ├── init.sql
│   └── seed.sql
├── src/
│   ├── main/
│   │   ├── java/org/example/tpj2eannonces/
│   │   │   ├── config/
│   │   │   │   └── AppContextListener.java
│   │   │   ├── exception/
│   │   │   │   ├── DatabaseException.java
│   │   │   │   └── ValidationException.java
│   │   │   ├── filter/
│   │   │   │   ├── AuthenticationFilter.java
│   │   │   │   └── EncodingFilter.java
│   │   │   ├── model/
│   │   │   │   ├── Annonce.java
│   │   │   │   ├── AnnonceStatus.java
│   │   │   │   ├── Category.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── GenericRepository.java
│   │   │   │   ├── AnnonceRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── RepositoryException.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AnnonceService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   ├── ServiceException.java
│   │   │   │   └── UserService.java
│   │   │   ├── servlet/
│   │   │   │   ├── BaseServlet.java
│   │   │   │   ├── annonce/
│   │   │   │   │   ├── AnnonceAddServlet.java
│   │   │   │   │   ├── AnnonceDeleteServlet.java
│   │   │   │   │   ├── AnnonceDetailServlet.java
│   │   │   │   │   ├── AnnonceListServlet.java
│   │   │   │   │   └── AnnoncePatchServlet.java
│   │   │   │   └── auth/
│   │   │   │       ├── LoginServlet.java
│   │   │   │       ├── LogoutServlet.java
│   │   │   │       └── RegisterServlet.java
│   │   │   └── utils/
│   │   │       ├── JPAUtil.java
│   │   │       ├── PasswordUtils.java
│   │   │       └── ValidationUtils.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── META-INF/
│   │   │       └── persistence.xml
│   │   └── webapp/
│   │       ├── index.jsp
│   │       └── WEB-INF/
│   │           ├── web.xml
│   │           └── jsp/
│   │               ├── layout/
│   │               │   ├── header.jsp
│   │               │   └── footer.jsp
│   │               └── features/
│   │                   ├── annonce/pages/
│   │                   │   ├── list.jsp
│   │                   │   ├── detail.jsp
│   │                   │   ├── add.jsp
│   │                   │   └── update.jsp
│   │                   ├── auth/pages/
│   │                   │   ├── login.jsp
│   │                   │   └── register.jsp
│   │                   └── errors/pages/
│   │                       ├── 404.jsp
│   │                       └── 500.jsp
│   └── test/
│       ├── java/org/example/tpj2eannonces/
│       │   ├── model/EntityMappingTest.java
│       │   ├── repository/
│       │   │   ├── AnnonceRepositoryTest.java
│       │   │   └── UserRepositoryTest.java
│       │   ├── service/
│       │   │   ├── AnnonceServiceTest.java
│       │   │   └── CategoryServiceTest.java
│       │   └── utils/JPAUtilTest.java
│       └── resources/META-INF/persistence.xml
├── docker-compose.yml
├── pom.xml
└── sonar-project.properties
```
