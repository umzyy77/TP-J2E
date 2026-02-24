# MasterAnnonce - TP4 Spring Boot

## Architecture

### Structure feature-based

```
src/main/java/org/example/tpj2eannonces/
├── MasterAnnonceApplication.java
├── core/                              # Configuration transversale
│   ├── config/SecurityConfig.java     # SecurityFilterChain, JWT, stateless
│   ├── security/                      # JwtService, JwtAuthenticationFilter
│   ├── aop/LoggingAspect.java         # Logging AOP sur les services
│   └── filter/CorrelationIdFilter.java
├── shared/                            # Code generique reutilisable
│   ├── model/                         # BaseEntity, OwnableByUser
│   ├── dto/ApiErrorDTO.java
│   └── exception/                     # Exceptions + GlobalExceptionHandler
└── features/                          # Domaines metier
    ├── annonce/                       # CRUD annonces
    │   ├── model/                     # Annonce, AnnonceStatus
    │   ├── dto/                       # AnnonceFormDTO, AnnonceResponseDTO, AnnonceStatusDTO
    │   ├── mapper/AnnonceMapper.java  # MapStruct
    │   ├── repository/               # JpaRepository + JpaSpecificationExecutor
    │   ├── service/AnnonceService.java
    │   └── controller/               # AnnonceController, AnnonceMetaController
    ├── user/                          # Gestion utilisateurs
    ├── category/                      # Categories d'annonces
    └── auth/                          # Authentification JWT
```

### Choix architecturaux

- **Feature-based** : chaque domaine metier est autonome (model, dto, mapper, repository, service, controller)
- **core/** : configuration Spring transversale (securite, AOP, filtres)
- **shared/** : code generique sans logique metier (BaseEntity, exceptions, DTOs communs)
- **Entite JPA = Modele metier** : pas de separation entity/model (adapte au scope du TP)

## Stack technique

| Composant | Version |
|-----------|---------|
| Java | 25 |
| Spring Boot | 4.0.2 |
| Spring Data JPA | via Spring Boot |
| Spring Security | via Spring Boot |
| MapStruct | 1.6.3 |
| JJWT | 0.12.6 |
| PostgreSQL | runtime |
| BCrypt (jbcrypt) | 0.4 |
| JUnit 5 + Mockito | via Spring Boot Test |
| JaCoCo | 0.8.14 |

Note: ce TP4 est volontairement realise en Java 25 pour rester a jour.

## Endpoints API

### Annonces

| Verbe | URI | Description | Auth |
|-------|-----|-------------|------|
| GET | /api/annonces | Liste paginee + filtres + tri | Oui |
| GET | /api/annonces/{id} | Detail d'une annonce | Oui |
| POST | /api/annonces | Creation | Oui |
| PUT | /api/annonces/{id} | Mise a jour | Oui (auteur) |
| DELETE | /api/annonces/{id} | Suppression | Oui (auteur, archivee) |
| PATCH | /api/annonces/{id} | Changement de statut | Oui (auteur, ADMIN pour archiver) |

### Filtres disponibles (GET /api/annonces)

| Parametre | Type | Description |
|-----------|------|-------------|
| q | String | Recherche par mot-cle (title/description) |
| status | AnnonceStatus | Filtrer par statut (DRAFT, PUBLISHED, ARCHIVED) |
| categoryId | Long | Filtrer par categorie |
| authorId | UUID | Filtrer par auteur |
| fromDate | LocalDateTime | Date minimum |
| toDate | LocalDateTime | Date maximum |
| page | int | Numero de page (defaut: 0) |
| size | int | Taille de page (defaut: 10) |
| sort | String | Tri (ex: date,desc) |

### Authentification

| Verbe | URI | Description |
|-------|-----|-------------|
| POST | /api/auth/login | Login, retourne un JWT |
| POST | /api/auth/refresh | Rafraichit access token + refresh token |

### Meta (introspection)

| Verbe | URI | Description |
|-------|-----|-------------|
| GET | /api/meta/annonces | Champs triables, filtrables, searchables |

## Securite

- **Access JWT** signe HMAC, contient userId + username + role + expiration
- **Refresh JWT** dedie (secret + expiration differents)
- **Stateless** : pas de session HTTP
- **Rate limiting** : protection brute-force sur `POST /api/auth/login` (HTTP 429)
- **Endpoints publics** : /api/auth/login, /api/auth/refresh, /actuator/**
- **Endpoints proteges** : /api/**
- **Regles metier** :
  - Seul l'auteur peut modifier/supprimer son annonce
  - Seul un ADMIN peut archiver
  - Annonce PUBLISHED non modifiable
  - Annonce doit etre ARCHIVED pour etre supprimee

## Logging

- **AOP** : @Around sur toutes les methodes de service (entree, sortie, duree, exceptions)
- **Correlation ID** : genere par filtre, propage via MDC, inclus dans chaque ligne de log
- **Format** : `HH:mm:ss.SSS [thread] [correlationId] LEVEL logger - message`

## Tests

- **Unitaires** (`*Test.java`) : Mockito, dans `features/*/service/`
- **Integration** (`*IT.java`) : @SpringBootTest + MockMvc, dans `features/*/controller/`
- **Separation** : surefire pour *Test, failsafe pour *IT

## Problemes rencontres et solutions

### 1. spring-boot-starter-aop renomme dans Spring Boot 4.x
- **Probleme** : le starter `spring-boot-starter-aop` n'existe plus sous ce nom
- **Solution** : utiliser `spring-boot-starter-aspectj` (nouveau nom dans Spring Boot 4.x)

### 2. Specification.where(null) ambigue dans Spring Data 4.x
- **Probleme** : ambiguite entre `Specification` et `PredicateSpecification` pour `where(null)`
- **Solution** : utiliser `Specification.unrestricted()` (methode officielle Spring Data 4.x)

### 3. @EntityGraph vs JOIN FETCH
- **Choix** : @EntityGraph (declaratif, idiomatique Spring Data) plutot que JPQL JOIN FETCH
- **Raison** : open-in-view: false necessite le chargement eager des relations pour le mapping DTO

### 4. DTO unique pour create/update
- **Choix** : `AnnonceFormDTO` unique au lieu de `AnnonceCreateDTO` + `AnnonceUpdateDTO`
- **Raison** : memes champs, le TP ne demande pas de separation

## Lancement

```bash
# Pre-requis : PostgreSQL sur localhost:5432, base "MasterAnnonce"
mvn spring-boot:run

# Tests unitaires
mvn test

# Tests unitaires + integration
mvn verify
```

## Docker (Exercice 12)

Commande unique pour lancer l'application + PostgreSQL:

```bash
docker compose up --build -d
```

Endpoints utiles:

- API: `http://localhost:8080`
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`

Si le port 8080 est deja pris, definir `APP_PORT` dans `.env` (ex: `APP_PORT=8081`).

Arreter et nettoyer:

```bash
docker compose down -v
```

## Bonus

- Refresh token implemente (`POST /api/auth/refresh`)
- Rate limiting login implemente (`429 TOO_MANY_REQUESTS` + `Retry-After`)
- Couverture de tests > 80% (JaCoCo)
- Pipeline Docker automatique dans GitHub Actions
