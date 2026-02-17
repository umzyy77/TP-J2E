# TP3 - Backend API REST securise

API REST MasterAnnonce avec JAX-RS/Jersey, authentification JAAS stateless, et couverture de tests.

## Architecture

```
Client HTTP  ->  Resource (JAX-RS)  ->  Service (metier)  ->  Repository (JPA)  ->  PostgreSQL / H2
                      |                      |
               SecurityFilter          Transactions
               (JAAS token)           Regles metier
```

| Couche | Responsabilite | Interdit |
|--------|----------------|----------|
| **Resource** | HTTP in/out, validation DTO, delegation | Logique metier, acces BDD |
| **Service** | Regles metier, transactions | HTTP, Request/Response |
| **Repository** | Requetes JPQL | Transactions, logique metier |

---

## Partie I - Exposition REST

### Exo 1 - Mise en place JAX-RS

**Choix** : Jersey 3.1.6 (implementation de reference JAX-RS, compatible Jakarta EE 10 / Tomcat 10.1).

| Dependance | Role |
|------------|------|
| `jersey-container-servlet` | Integration Tomcat |
| `jersey-hk2` | Injection de dependances |
| `jersey-media-json-jackson` | Serialisation JSON |
| `jersey-bean-validation` | Validation sur les DTOs |

Point d'entree : `@ApplicationPath("/api")` dans `RestApplication.java`.

### Exo 2 - API REST Annonce

| Verbe | URI | Auth | Succes | Erreur |
|-------|-----|------|--------|--------|
| GET | `/api/annonces` | Non | 200 | - |
| GET | `/api/annonces/{id}` | Non | 200 | 404 |
| POST | `/api/annonces` | Oui | 201 + Location | 400 |
| PUT | `/api/annonces/{id}` | Oui | 200 | 403/404/409 |
| PATCH | `/api/annonces/{id}` | Oui | 200 | 403/404/409 |
| DELETE | `/api/annonces/{id}` | Oui | 204 | 403/404/409 |
| POST | `/api/login` | Non | 200 | 401 |

**Recherche** : `GET /api/annonces?q=voiture&category=1&status=PUBLISHED&page=0&size=5`

**DTOs** : Records Java avec Bean Validation. `AnnonceResponseDTO` utilise le **pattern Builder** (9 champs dont objets imbriques).

**PATCH** : Modification partielle pour le changement de statut (`{"action": "publish"}`).

---

## Partie II - Validation et robustesse

### Exo 3 - Validation API

`@Valid` sur les parametres declenche la validation Bean Validation. `ValidationExceptionMapper` convertit les erreurs en JSON normalise :

```json
{"error": "VALIDATION_ERROR", "messages": ["title: Le titre est obligatoire"]}
```

### Exo 4 - Gestion des erreurs REST

Chaque exception metier est interceptee par un `ExceptionMapper` dedie :

| Mapper | Exception | HTTP |
|--------|-----------|------|
| `ValidationExceptionMapper` | `ConstraintViolationException` | 400 |
| `JsonParseExceptionMapper` | `JsonProcessingException` | 400 |
| `ForbiddenExceptionMapper` | `ForbiddenException` | 403 |
| `NotFoundExceptionMapper` | `NotFoundException` | 404 |
| `ConflictExceptionMapper` | `ConflictException` + sous-classes | 409 |
| `GenericExceptionMapper` | `Exception` (catch-all) | 500 |

Hierarchie : `ConflictException` est parente de toutes les exceptions de conflit metier (`AnnonceImmutableException`, `InvalidTransitionException`, `ArchiveRequiredException`, `DuplicateUserException`, etc.) -> un seul mapper les intercepte toutes.

---

## Partie III - Securite

### Exo 5 - Authentification stateless + JAAS

**TokenStore** (enum singleton) : genere des UUID tokens, stockes dans une `ConcurrentHashMap` avec expiration 1h.

**JAAS** : deux `LoginModule` configurés dans `jaas.conf` :

| Domaine JAAS | LoginModule | Usage |
|--------------|-------------|-------|
| `MasterAnnonceLogin` | `DbLoginModule` | Login credentials -> verifie en BDD |
| `MasterAnnonceToken` | `TokenLoginModule` | Validation Bearer token |

Les deux heritent de `AbstractLoginModule` (commit/abort/logout/cleanup factorise).

**Flux login** : `AuthResource` -> `LoginContext("MasterAnnonceLogin")` -> `DbLoginModule.authenticate()` -> Subject avec `UserPrincipal` + `RolePrincipal` -> generation token.

### Exo 6 - Filtre de securite

`SecurityFilter` (`ContainerRequestFilter`, `@Priority(AUTHENTICATION)`) :
1. Verifie `@PermitAll` -> skip si public
2. Extrait le Bearer token du header `Authorization`
3. `LoginContext("MasterAnnonceToken")` -> JAAS valide le token
4. Injecte `UserSecurityContext(subject)` dans la requete
5. Sinon -> 401 JSON

### Exo 7 - Regles metier avancees

| Regle | Verification | Exception | HTTP |
|-------|--------------|-----------|------|
| Seul l'auteur peut modifier | `checkOwnership()` | `ForbiddenException` | 403 |
| PUBLISHED = immutable | garde dans `updateFields()` | `AnnonceImmutableException` | 409 |
| Archivage avant suppression | garde dans `delete()` | `ArchiveRequiredException` | 409 |
| Transitions invalides rejetees | garde dans `changeStatus()` | `InvalidTransitionException` | 409 |
| Concurrence | `@Version` JPA | `OptimisticLockException` | - |

Cycle de vie : `DRAFT -> [publish] -> PUBLISHED -> [archive] -> ARCHIVED -> [delete]`

---

## Partie IV - Tests et qualite

### Exo 8 - Tests Repository

Base H2 in-memory (`MasterAnnonceTestPU`, `create-drop`). Chaque test nettoie la BDD dans `@BeforeEach`/`@AfterEach`.

### Exo 9 - Tests API REST

#### Tests unitaires

| Classe | Tests | Couverture |
|--------|-------|------------|
| `AnnonceServiceTest` | 29 | CRUD, ownership, transitions, filtres, recherche, pagination |
| `UserServiceTest` | 11 | CRUD, doublons, authentification |
| `CategoryServiceTest` | 8 | CRUD, doublons, integrite referentielle |
| `DbLoginModuleTest` | 7 | Login/commit, credentials invalides, abort, logout |
| `TokenLoginModuleTest` | 7 | Token valide/invalide/blank, commit, abort, logout |
| `TokenStoreTest` | 6 | Generation, validation, revocation, TTL, enum singleton |
| `UserSecurityContextTest` | 6 | Principal, roles, isSecure, scheme, subject |
| `PasswordUtilsTest` | 7 | Hash BCrypt, verify, fallback plaintext, null |
| `AnnonceMapperTest` | 6 | toEntity, updateEntity, toResponseDTO, toList |
| `ExceptionMappersTest` | 7 | Chaque mapper -> code HTTP + format ApiErrorDTO |
| `RolePrincipalTest` | 5 | getName, equals, hashCode, toString, null |
| `EntityMappingTest` | 8 | Persistance et relations JPA |
| `AnnonceRepositoryTest` | 12 | CRUD, pagination, recherche, filtres |
| `UserRepositoryTest` | 5 | CRUD, findByUsername, findByEmail |
| `JPAUtilTest` | 4 | Transactions, EntityManager |

#### Tests d'integration REST (Jersey Test Framework + Grizzly)

| Classe | Tests | Couverture |
|--------|-------|------------|
| `AuthResourceIT` | 3 | Login 200 + token (`/login`), 401 invalides |
| `AnnonceResourceIT` | 17 | GET list/detail + validation pagination, POST 201/401, PUT 200/404/409, PATCH publish/archive/409, DELETE 204/409/404/401 |

**Total : 148 tests, 0 failures**

#### SonarQube

| Metrique | Valeur |
|----------|--------|
| Bugs | 0 |
| Vulnerabilities | 0 |
| Code Smells | 0 open |
| Duplication | 0.0% |
| Coverage | 81.9% |

### Exo 10 - Industrialisation

1. **Separation unitaires / integration** :
   - Unitaires: `mvn test`
   - Integration: `mvn verify -DskipUnitTests=true`
   - Tous les tests: `mvn verify`
2. **Logging structure** : Logback + sortie JSON (`src/main/resources/logback.xml`).
3. **Tests de charge simples** : script K6 fourni (`docs/load-test-k6.js`).
4. **Documentation API** : specification OpenAPI YAML (`src/main/resources/openapi.yaml`) exposee via `GET /api/openapi`.

---

## Problemes rencontres et solutions

### 1. JAX-RS sur Tomcat

**Pb** : Tomcat ne fournit pas d'implementation JAX-RS.
**Sol** : Jersey 3.1.6 embarque via Maven, `jersey-container-servlet` s'enregistre via `@ApplicationPath`.

### 2. Update annonce avec changement de categorie

**Pb** : `em.merge()` simple ne resout pas la nouvelle Category dans le contexte de persistence.
**Sol** : Methode `updateFields()` qui charge l'entite managed, modifie ses champs, et resout la Category via `em.find()` dans la meme transaction.

### 3. Propagation de l'identite (SecurityContext)

**Pb** : Comment transmettre l'identite apres validation du token aux Resources ?
**Sol** : `SecurityFilter` injecte un `UserSecurityContext(subject)` via `requestContext.setSecurityContext()`. La Resource recupere l'utilisateur via `@Context SecurityContext`.

### 4. JAAS en REST stateless sans serveur d'application

**Pb** : JAAS est concu pour des apps avec session/EJB. Sur Tomcat seul, pas de chargement automatique de `jaas.conf`.
**Sol** : `AppContextListener.initJaas()` charge le fichier depuis le classpath au demarrage. Maven Surefire configure la propriete systeme pour les tests.

### 5. Jackson + LocalDateTime dans Jersey Test Framework

**Pb** : `AnnonceResponseDTO.date` (`LocalDateTime`) n'est pas serialise par Jackson par defaut dans le contexte Grizzly (test). Erreur : "Java 8 date/time type not supported".
**Sol** : Ajout de `jackson-datatype-jsr310` + creation d'un `ObjectMapperProvider` (`ContextResolver<ObjectMapper>`) qui enregistre `JavaTimeModule`. Enregistre dans le `ResourceConfig` des tests IT.

### 6. PATCH non supporte par HttpURLConnection (tests)

**Pb** : Le client HTTP standard Java ne supporte pas le verbe PATCH. Les tests IT echouent avec `ProtocolException: Invalid HTTP method: PATCH`.
**Sol** : `HttpUrlConnectorProvider.useSetMethodWorkaround()` dans `configureClient()` + `--add-opens java.base/java.net=ALL-UNNAMED` dans les args JVM de Surefire.

### 7. Duplication code LoginModules

**Pb** : `DbLoginModule` et `TokenLoginModule` avaient un code identique pour commit/abort/logout/cleanup (35% de duplication SonarQube).
**Sol** : Extraction d'une classe `AbstractLoginModule` avec le cycle de vie commun. Chaque module implemente uniquement `authenticate()`.
