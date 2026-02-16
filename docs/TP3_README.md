# TP Dev Avance #3 - Backend API REST securise

Transformation de MasterAnnonce en Backend API REST avec JAX-RS (Jersey), authentification stateless par token, et tests complets.

## Table des matieres

- [Architecture](#architecture)
- [Technologies ajoutees (Semaine 3)](#technologies-ajoutees-semaine-3)
- [Partie I - Exposition REST](#partie-i--exposition-rest)
  - [Exercice 1 - Mise en place JAX-RS](#exercice-1--mise-en-place-de-jax-rs)
  - [Exercice 2 - API REST Annonce](#exercice-2--api-rest-annonce)
- [Partie II - Validation et robustesse](#partie-ii--validation-et-robustesse)
  - [Exercice 3 - Validation API](#exercice-3--validation-api)
  - [Exercice 4 - Gestion des erreurs REST](#exercice-4--gestion-des-erreurs-rest)
- [Partie III - Securite](#partie-iii--securite)
  - [Exercice 5 - Authentification stateless](#exercice-5--authentification-stateless)
  - [Exercice 6 - Filtre de securite](#exercice-6--filtre-de-securite)
  - [Exercice 7 - Regles metier avancees](#exercice-7--regles-metier-avancees)
- [Partie IV - Tests et qualite](#partie-iv--tests-et-qualite)
  - [Exercice 8 - Tests Repository](#exercice-8--tests-repository-integration)
- [Endpoints API](#endpoints-api)
- [Problemes rencontres et solutions](#problemes-rencontres-et-solutions)

---

## Architecture

```
Client (Postman / Front JS)
      | HTTP / JSON
      v
API REST (JAX-RS / Jersey)        <-- Couche presentation (Resources)
      |
      v
Service (transactions + regles)   <-- Couche metier
      |
      v
Repository (JPA / JPQL)           <-- Couche persistence
      |
      v
PostgreSQL                        <-- Base de donnees
```

### Structure du code

```
src/main/java/org/example/tpj2eannonces/
├── api/                                    # Couche API REST (JAX-RS)
│   ├── RestApplication.java                #   Point d'entree @ApplicationPath("/api")
│   ├── resource/                           #   Resources REST (endpoints)
│   │   ├── AnnonceResource.java            #     CRUD /api/annonces
│   │   ├── AuthResource.java              #     POST /api/auth/login
│   │   ├── HelloWorldResource.java         #     GET /api/helloWorld
│   │   └── ParamsResource.java             #     GET /api/params
│   ├── dto/                                #   DTOs organises par feature
│   │   ├── annonce/                        #     DTOs lies aux annonces
│   │   │   ├── AnnonceCreateDTO.java       #       Entree POST
│   │   │   ├── AnnonceUpdateDTO.java       #       Entree PUT
│   │   │   ├── AnnonceStatusDTO.java       #       Entree PATCH
│   │   │   ├── AnnonceResponseDTO.java     #       Sortie (avec Builder)
│   │   │   └── AnnonceSearchParams.java    #       @BeanParam recherche/pagination
│   │   ├── auth/                           #     DTOs lies a l'authentification
│   │   │   ├── LoginDTO.java              #       Entree POST /api/auth/login
│   │   │   └── LoginResponseDTO.java       #       Sortie (token + expiresIn)
│   │   └── common/                         #     DTOs partages/generiques
│   │       ├── ApiErrorDTO.java            #       Format d'erreur normalise
│   │       ├── PaginationParams.java       #       Pagination generique (page/size)
│   │       └── PaginatedResponseDTO.java   #       Reponse paginee generique
│   ├── mapper/AnnonceMapper.java           #   Mapping Entity <-> DTO
│   ├── security/                           #   Securite stateless
│   │   ├── SecurityFilter.java             #     ContainerRequestFilter (token)
│   │   ├── TokenStore.java                 #     Stockage tokens en memoire
│   │   ├── TokenInfo.java                  #     Record token (userId, username, expiry)
│   │   ├── UserPrincipal.java              #     Principal JAX-RS
│   │   └── UserSecurityContext.java        #     SecurityContext custom
│   └── exception/                          #   ExceptionMappers (exception -> HTTP)
│       ├── ValidationExceptionMapper.java  #     400 (validation)
│       ├── JsonParseExceptionMapper.java   #     400 (JSON invalide)
│       ├── ForbiddenExceptionMapper.java   #     403 (ownership)
│       ├── NotFoundExceptionMapper.java    #     404 (not found)
│       ├── ConflictExceptionMapper.java    #     409 (conflit metier)
│       └── GenericExceptionMapper.java     #     500 (catch-all)
├── exception/                              # Exceptions metier (par feature)
│   ├── NotFoundException.java              #   Ressource introuvable (-> 404)
│   ├── ForbiddenException.java             #   Violation de propriete (-> 403)
│   ├── ConflictException.java              #   Classe parente conflit (-> 409)
│   ├── annonce/                            #   Exceptions specifiques annonces
│   │   ├── AnnonceImmutableException.java  #     Annonce publiee non modifiable
│   │   ├── InvalidTransitionException.java #     Transition de statut invalide
│   │   └── ArchiveRequiredException.java   #     Archivage requis avant suppression
│   ├── user/                               #   Exceptions specifiques utilisateurs
│   │   └── DuplicateUserException.java     #     Username/email deja pris
│   └── category/                           #   Exceptions specifiques categories
│       ├── DuplicateCategoryException.java #     Categorie deja existante
│       └── CategoryInUseException.java     #     Annonces liees, suppression impossible
├── config/              # AppContextListener (init/destroy JPA)
├── filter/              # EncodingFilter (UTF-8)
├── model/               # Entites JPA (User, Category, Annonce + @Version)
├── repository/          # Couche persistence JPQL (GenericRepository)
├── service/             # Couche metier + transactions + regles metier
│   ├── AnnonceService.java   # checkOwnership, PUBLISHED immutability, state transitions
│   ├── UserService.java      # Authentification, creation avec verification doublons
│   └── CategoryService.java  # CRUD avec integrite referentielle
└── utils/               # JPAUtil, PasswordUtils
```

### Principe de separation des couches

| Couche | Responsabilite | Interdit |
|--------|----------------|----------|
| **Resource** (API) | Recevoir HTTP, valider entrees, deleguer au service, retourner HTTP | Logique metier, acces BDD, transactions |
| **Service** | Logique metier, gestion des transactions | Acces HTTP, manipulation de Request/Response |
| **Repository** | Acces aux donnees via JPQL | Transactions, logique metier |

### Hierarchie des exceptions metier

Les exceptions sont organisees par **domaine metier** (et non par couche technique). Une classe parente `ConflictException` permet au mapper JAX-RS d'intercepter toutes les sous-classes avec un seul `@Provider` :

```
RuntimeException
├── NotFoundException                       -> 404
├── ForbiddenException                      -> 403
└── ConflictException                       -> 409
    ├── annonce/AnnonceImmutableException
    ├── annonce/InvalidTransitionException
    ├── annonce/ArchiveRequiredException
    ├── user/DuplicateUserException
    ├── category/DuplicateCategoryException
    └── category/CategoryInUseException
```

---

## Technologies ajoutees (Semaine 3)

| Dependance | Version | Role |
|------------|---------|------|
| Jakarta JAX-RS API | 3.1.0 | Specification REST |
| Jersey Container Servlet | 3.1.6 | Implementation JAX-RS pour Tomcat |
| Jersey HK2 | 3.1.6 | Injection de dependances |
| Jersey Media JSON Jackson | 3.1.6 | Serialisation/deserialisation JSON automatique |

---

## Partie I - Exposition REST

### Exercice 1 - Mise en place de JAX-RS

#### Justification du choix de configuration

**Implementation choisie : Jersey 3.1.6**

Jersey est l'**implementation de reference** de la specification JAX-RS, tout comme Hibernate est l'implementation de reference de JPA. Ce choix se justifie par :

1. **Compatibilite Jakarta EE 10** : Jersey 3.1.x utilise le namespace `jakarta.ws.rs` (pas `javax`), compatible avec notre stack Jakarta EE 10 / Tomcat 10.1.

2. **Integration Tomcat native** : Le module `jersey-container-servlet` permet a Jersey de fonctionner comme un servlet standard dans Tomcat, sans necessiter un serveur d'application complet (GlassFish, WildFly).

3. **JSON automatique via Jackson** : Le module `jersey-media-json-jackson` active l'auto-discovery de Jackson. Les objets Java sont automatiquement convertis en JSON et vice-versa, sans configuration manuelle.

4. **Injection de dependances** : `jersey-hk2` fournit l'injection de dependances Jakarta CDI-compatible, necessaire au fonctionnement interne de Jersey.

5. **Deploiement propre** : Jersey est deploye sous `/api/*` via `@ApplicationPath("/api")`. L'application est 100% API REST, sans JSP ni servlets (conformement aux contraintes du TP : "application totalement utilisable sans JSP, communication JSON uniquement").

**Alternative ecartee : RESTEasy**
RESTEasy (Red Hat) est une alternative valide, mais Jersey a ete prefere car c'est l'implementation de reference officielle et la plus documentee pour un contexte pedagogique.

#### Configuration mise en place

| Element | Fichier | Description |
|---------|---------|-------------|
| Dependances Maven | `pom.xml` | 4 dependances Jersey + API JAX-RS |
| Point d'entree | `api/RestApplication.java` | `@ApplicationPath("/api")` |
| Test HelloWorld | `api/resource/HelloWorldResource.java` | `GET /api/helloWorld` |
| Test Params | `api/resource/ParamsResource.java` | QueryParams + PathParams |

#### Endpoints de test

```
GET /api/helloWorld
  -> {"message": "Hello World!"}

GET /api/params?name=John&age=25
  -> {"name": "John", "age": 25}

GET /api/params/42
  -> {"id": 42}
```

---

### Exercice 2 - API REST Annonce

#### Endpoints implementes

| Verbe | URI | Description | Code succes | Code erreur |
|-------|-----|-------------|-------------|-------------|
| **GET** | `/api/annonces` | Liste paginee avec filtres | 200 | - |
| **GET** | `/api/annonces/{id}` | Detail d'une annonce | 200 | 404 |
| **POST** | `/api/annonces` | Creation d'une annonce | 201 + Location | 400 |
| **PUT** | `/api/annonces/{id}` | Mise a jour complete | 200 | 404 |
| **DELETE** | `/api/annonces/{id}` | Suppression | 204 | 404 |
| **PATCH** | `/api/annonces/{id}` | Changement de statut (publish/archive) | 200 | 404, 409 |

#### Parametres de recherche (GET /api/annonces)

| Parametre | Type | Description | Defaut |
|-----------|------|-------------|--------|
| `page` | int | Numero de page (0-indexed) | 0 |
| `size` | int | Nombre d'elements par page | 10 |
| `q` | String | Recherche par mot-cle (titre + description) | - |
| `category` | Long | Filtrer par ID de categorie | - |
| `status` | String | Filtrer par statut (DRAFT, PUBLISHED, ARCHIVED) | - |

#### DTOs

Le **pattern Builder** a ete implemente sur `AnnonceResponseDTO` pour faciliter le mapping Entity -> DTO. Ce pattern est pertinent ici car le DTO de reponse possede 9 champs dont des objets imbriques (`AuthorDTO`, `CategoryDTO`), ce qui rend la construction via constructeur peu lisible.

Les DTOs sont organises par feature dans des sous-packages :

| Package | DTO | Direction | Pattern |
|---------|-----|-----------|---------|
| `dto/annonce` | `AnnonceCreateDTO` | Entree (POST) | Record + Bean Validation |
| `dto/annonce` | `AnnonceUpdateDTO` | Entree (PUT) | Record + Bean Validation |
| `dto/annonce` | `AnnonceStatusDTO` | Entree (PATCH) | Record + Bean Validation |
| `dto/annonce` | `AnnonceResponseDTO` | Sortie | Record + **Builder** |
| `dto/annonce` | `AnnonceSearchParams` | @BeanParam | Classe + @QueryParam |
| `dto/auth` | `LoginDTO` | Entree (POST) | Record + Bean Validation |
| `dto/auth` | `LoginResponseDTO` | Sortie | Record |
| `dto/common` | `PaginatedResponseDTO<T>` | Sortie (liste) | Record generique |
| `dto/common` | `PaginationParams` | Base pagination | Classe + @QueryParam |
| `dto/common` | `ApiErrorDTO` | Sortie (erreur) | Record + factory methods |

#### Pattern Builder - Justification

```java
// SANS Builder (9 params, difficile a lire)
new AnnonceResponseDTO(id, title, desc, adress, mail, date, status, author, category);

// AVEC Builder (clair, chaque champ est nomme)
AnnonceResponseDTO.builder()
    .id(annonce.getId())
    .title(annonce.getTitle())
    .status(annonce.getStatus().name())
    .author(new AuthorDTO(author.getId(), author.getUsername()))
    .build();
```

Le Builder est particulierement utile pour le mapping Entity -> DTO dans `AnnonceMapper.toResponseDTO()`, car certains champs peuvent etre null (author, category en cas de lazy loading non resolu).

#### Mapper

`AnnonceMapper` centralise toutes les conversions :
- `toEntity(AnnonceCreateDTO)` : DTO -> nouvelle entite
- `updateEntity(Annonce, AnnonceUpdateDTO)` : met a jour une entite existante
- `toResponseDTO(Annonce)` : entite -> DTO de reponse (via Builder)
- `toResponseDTOList(List<Annonce>)` : liste d'entites -> liste de DTOs

**Aucune logique metier** dans la Resource : elle recoit le HTTP, mappe vers un DTO, delegue au Service, et retourne le code HTTP adequat.

#### Endpoint PATCH (Bonus)

**Description :** Le verbe `PATCH` est utilise pour les modifications **partielles** d'une ressource. Ici, il sert exclusivement au changement de statut d'une annonce (publish/archive), sans modifier les autres champs.

**Corps de la requete :**
```json
{"action": "publish"}
```

**Resultat attendu :** L'annonce passe au statut suivant dans le cycle de vie :
- `DRAFT` --[publish]--> `PUBLISHED`
- `PUBLISHED` --[archive]--> `ARCHIVED`

Si la transition est invalide (ex: archiver un brouillon), une erreur 409 Conflict sera retournee (gestion dans l'exercice 4).

#### Exemples de requetes

```
POST /api/annonces
Content-Type: application/json
{
  "authorId": "uuid-de-l-utilisateur",
  "title": "Appartement T3",
  "description": "Bel appartement lumineux",
  "adress": "12 rue de Paris",
  "mail": "contact@example.com",
  "categoryId": 1
}
-> 201 Created + Location: /api/annonces/42

GET /api/annonces?q=appartement&status=PUBLISHED&page=0&size=5
-> 200 OK + {"data": [...], "totalCount": 12, "page": 0, "pageSize": 5, "totalPages": 3}

PATCH /api/annonces/42
Content-Type: application/json
{"action": "publish"}
-> 200 OK + annonce avec status "PUBLISHED"
```

---

## Partie II - Validation et robustesse

### Exercice 3 - Validation API

#### Bean Validation sur les DTOs

Les annotations `@NotBlank`, `@Size`, `@Email`, `@NotNull` sont posees directement sur les champs des records DTO. L'annotation `@Valid` sur les parametres des endpoints declenche automatiquement la validation avant l'execution de la methode.

```java
@POST
public Response create(@Valid AnnonceCreateDTO dto, ...) { ... }
```

Si la validation echoue, Jersey intercepte la `ConstraintViolationException` et notre `ValidationExceptionMapper` la transforme en reponse JSON normalisee :

```json
{
  "error": "VALIDATION_ERROR",
  "messages": [
    "title: Le titre est obligatoire",
    "mail: L'email doit etre valide"
  ]
}
```

#### Dependance ajoutee

`jersey-bean-validation` (3.1.6) : module Jersey qui integre Bean Validation dans le cycle de vie des requetes JAX-RS.

### Exercice 4 - Gestion des erreurs REST

#### Gestion centralisee via ExceptionMappers

Chaque type d'erreur est intercepte par un `@Provider` dedie qui retourne un JSON normalise (`ApiErrorDTO`) avec le code HTTP correct :

| ExceptionMapper | Exception interceptee | Code HTTP | error |
|-----------------|----------------------|-----------|-------|
| `ValidationExceptionMapper` | `ConstraintViolationException` | **400** Bad Request | `VALIDATION_ERROR` |
| `JsonParseExceptionMapper` | `JsonProcessingException` | **400** Bad Request | `BAD_REQUEST` |
| `ForbiddenExceptionMapper` | `ForbiddenException` | **403** Forbidden | `FORBIDDEN` |
| `NotFoundExceptionMapper` | `NotFoundException` | **404** Not Found | `NOT_FOUND` |
| `ConflictExceptionMapper` | `ConflictException` (+ sous-classes) | **409** Conflict | `CONFLICT` |
| `GenericExceptionMapper` | `Exception` (catch-all) | **500** Internal Error | `INTERNAL_ERROR` |

Le `ConflictExceptionMapper` intercepte `ConflictException` ainsi que toutes ses sous-classes metier (`AnnonceImmutableException`, `InvalidTransitionException`, `ArchiveRequiredException`, `DuplicateUserException`, etc.) grace au polymorphisme.

#### Format de reponse d'erreur normalise

Toutes les erreurs suivent le meme format JSON :

```json
{
  "error": "VALIDATION_ERROR",
  "messages": ["description du probleme"]
}
```

#### Exemples concrets

```
POST /api/annonces  {"title": ""}
-> 400  {"error": "VALIDATION_ERROR", "messages": ["title: Le titre est obligatoire"]}

GET /api/annonces/99999
-> 404  {"error": "NOT_FOUND", "messages": ["Annonce non trouvee: 99999"]}

PATCH /api/annonces/1  {"action": "archive"}  (alors que l'annonce est en DRAFT)
-> 409  {"error": "CONFLICT", "messages": ["Transition invalide depuis DRAFT avec action archive"]}

PUT /api/annonces/1  (utilisateur non-auteur)
-> 403  {"error": "FORBIDDEN", "messages": ["Vous n'etes pas l'auteur de cette annonce"]}

POST /api/annonces  {json invalide}
-> 400  {"error": "BAD_REQUEST", "messages": ["JSON invalide : ..."]}

Exception non prevue
-> 500  {"error": "INTERNAL_ERROR", "messages": ["Erreur interne du serveur"]}
```

Le `GenericExceptionMapper` agit comme filet de securite : une exception non interceptee ne produit jamais de stack trace cote client, mais est loguee cote serveur.

---

## Partie III - Securite

### Exercice 5 - Authentification stateless

#### Endpoint login

```
POST /api/auth/login
Content-Type: application/json
{"username": "admin", "password": "password123"}

-> 200 OK
{"token": "uuid-token", "expiresIn": 3600}

-> 401 Unauthorized (identifiants invalides)
{"error": "UNAUTHORIZED", "messages": ["Identifiants invalides"]}
```

#### Implementation

| Composant | Fichier | Description |
|-----------|---------|-------------|
| Endpoint | `AuthResource.java` | `POST /api/auth/login`, @PermitAll |
| DTO entree | `dto/auth/LoginDTO.java` | Record(username, password) avec @NotBlank |
| DTO sortie | `dto/auth/LoginResponseDTO.java` | Record(token, expiresIn) |
| Stockage | `TokenStore.java` | Singleton, `ConcurrentHashMap` en memoire |
| Token info | `TokenInfo.java` | Record(userId, username, expiresAt) |

**Fonctionnement** : Le `TokenStore` genere un UUID comme token, l'associe a un `TokenInfo` (userId, username, expiration), et le stocke dans une `ConcurrentHashMap`. Le token expire apres 1 heure (configurable).

### Exercice 6 - Filtre de securite

#### ContainerRequestFilter

`SecurityFilter` implemente `ContainerRequestFilter` et s'execute avant chaque requete JAX-RS :

1. **Lecture du header** : `Authorization: Bearer <token>`
2. **Validation** : verifie le token dans `TokenStore` (existence + expiration)
3. **Injection de l'identite** : cree un `UserSecurityContext` contenant un `UserPrincipal(userId, username)` et l'injecte via `requestContext.setSecurityContext()`
4. **Rejet** : si le token est invalide -> `401 Unauthorized`

#### Endpoints publics

Les endpoints GET (liste, detail) et le login sont annotes `@PermitAll` : ils sont accessibles sans token. Tous les autres endpoints (POST, PUT, DELETE, PATCH) necessitent un token valide.

#### SecurityContext standard JAX-RS

| Classe | Role |
|--------|------|
| `UserPrincipal` | Implemente `java.security.Principal`, porte `userId` + `username` |
| `UserSecurityContext` | Implemente `jakarta.ws.rs.core.SecurityContext`, wrappe le `UserPrincipal` |

Dans la Resource, l'utilisateur authentifie est recupere via :
```java
@Context SecurityContext securityContext;

private UUID getCurrentUserId() {
    UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
    return principal.getUserId();
}
```

### Exercice 7 - Regles metier avancees

#### Regles implementees

| # | Regle | Verification | Exception metier | HTTP |
|---|-------|--------------|------------------|------|
| R1 | Seul l'auteur peut modifier/supprimer/changer le statut | `checkOwnership()` dans `AnnonceService` | `ForbiddenException` | **403** |
| R2 | Une annonce PUBLISHED ne peut plus etre modifiee (PUT) | Garde dans `updateFields()` | `AnnonceImmutableException` | **409** |
| R3 | Archivage obligatoire avant suppression | Garde dans `delete()` | `ArchiveRequiredException` | **409** |
| R4 | Transitions de statut invalides rejetees | Garde dans `changeStatus()` | `InvalidTransitionException` | **409** |
| R5 | Gestion concurrence | `@Version` sur `Annonce.version` | `OptimisticLockException` (JPA) | - |

#### Optimistic Locking (@Version)

```java
@jakarta.persistence.Version
@Column(name = "version")
private Long version;
```

JPA incremente automatiquement le champ `version` a chaque `UPDATE`. Si deux requetes modifient la meme annonce simultanement, la seconde echouera avec une `OptimisticLockException` (pas de verrou en base, juste une comparaison de version).

#### Cycle de vie complet (avec gardes)

```
DRAFT --[publish]--> PUBLISHED --[archive]--> ARCHIVED --[delete]--> supprime
  |                     |                        |
  |  PUT autorise        |  PUT interdit (R2)     |  DELETE autorise (R3)
  |  DELETE interdit     |  DELETE interdit       |
```

### Bonus Exercice 5 - Authentification JAAS

#### Pourquoi JAAS ?

JAAS (Java Authentication and Authorization Service) est l'API standard Java pour gerer l'authentification et l'autorisation de maniere modulaire. Dans notre contexte REST stateless, JAAS apporte :

1. **Separation des mecanismes d'authentification** : chaque strategie (login DB, validation token) est encapsulee dans un `LoginModule` independant.
2. **Subject standard** : apres authentification, l'identite est portee par un `Subject` JAAS contenant des `Principal` (identite + roles), exploitable dans toute la couche metier.
3. **Configuration declarative** : le fichier `jaas.conf` permet de changer de strategie d'authentification sans modifier le code.

#### Architecture JAAS

```
jaas.conf
├── MasterAnnonceLogin   -> DbLoginModule (username/password)
└── MasterAnnonceToken   -> TokenLoginModule (Bearer token)
```

#### 5.1 - Configuration JAAS

**Fichier** : `src/main/resources/jaas.conf`

Deux domaines JAAS :
- `MasterAnnonceLogin` : utilise `DbLoginModule` pour l'authentification par credentials
- `MasterAnnonceToken` : utilise `TokenLoginModule` pour la validation de token

**Chargement** : La propriete systeme `java.security.auth.login.config` est configuree :
- Automatiquement via `AppContextListener.initJaas()` au demarrage de l'application
- Via l'option JVM `-Djava.security.auth.login.config=` dans les plugins Maven (Surefire pour les tests)

#### 5.2 - DbLoginModule

**Fichier** : `api/security/jaas/DbLoginModule.java`

| Phase | Action |
|-------|--------|
| `initialize()` | Recoit le Subject et le CallbackHandler |
| `login()` | Recupere username/password via NameCallback + PasswordCallback, verifie en base via UserRepository + PasswordUtils |
| `commit()` | Ajoute `UserPrincipal(userId, username)` et `RolePrincipal("ROLE_USER")` au Subject |
| `abort()` | Nettoyage en cas d'echec |
| `logout()` | Retire les Principals du Subject |

#### 5.3 - Endpoint /api/auth/login via JAAS

**Fichier** : `api/resource/AuthResource.java`

```java
LoginContext lc = new LoginContext("MasterAnnonceLogin",
        new CredentialsCallbackHandler(username, password));
lc.login();
Subject subject = lc.getSubject();
UserPrincipal principal = extractUserPrincipal(subject);
String token = tokenStore.generateToken(principal.getUserId(), principal.getName());
```

Le endpoint cree un `LoginContext` avec le domaine `MasterAnnonceLogin`, delegue l'authentification au `DbLoginModule`, puis genere un token a partir du `UserPrincipal` present dans le Subject.

#### 5.4 - TokenLoginModule

**Fichier** : `api/security/jaas/TokenLoginModule.java`

| Phase | Action |
|-------|--------|
| `login()` | Recupere le token via NameCallback, valide via `TokenStore.validate()` |
| `commit()` | Reconstitue l'identite : `UserPrincipal` + `RolePrincipal("ROLE_USER")` dans le Subject |

#### 5.5 - SecurityFilter via JAAS

**Fichier** : `api/security/SecurityFilter.java`

```java
LoginContext lc = new LoginContext("MasterAnnonceToken", new TokenCallbackHandler(token));
lc.login();
Subject subject = lc.getSubject();
requestContext.setSecurityContext(new UserSecurityContext(subject, isSecure));
```

Le filtre utilise le domaine `MasterAnnonceToken` pour valider le Bearer token. Le Subject JAAS est injecte dans un `UserSecurityContext` qui supporte desormais `isUserInRole()` via les `RolePrincipal`.

#### 5.6 - Exploitation de l'identite

Le `UserSecurityContext` construit a partir du Subject JAAS expose :
- `getUserPrincipal()` : retourne le `UserPrincipal` (userId + username)
- `isUserInRole(role)` : verifie la presence d'un `RolePrincipal` dans le Subject
- `getSubject()` : acces direct au Subject JAAS si necessaire dans la couche Service

#### Structure des fichiers JAAS

```
api/security/
├── jaas/                              # Package JAAS
│   ├── DbLoginModule.java            #   LoginModule login DB
│   ├── TokenLoginModule.java         #   LoginModule validation token
│   ├── RolePrincipal.java            #   Principal pour les roles
│   ├── CredentialsCallbackHandler.java #  Handler username/password
│   └── TokenCallbackHandler.java      #  Handler token Bearer
├── SecurityFilter.java                # Filtre -> LoginContext JAAS
├── UserPrincipal.java                 # Principal identite (compatible JAAS)
├── UserSecurityContext.java           # SecurityContext -> Subject JAAS
├── TokenStore.java                    # Stockage tokens en memoire
└── TokenInfo.java                     # Record token metadata
```

---

## Partie IV - Tests et qualite

### Exercice 8 - Tests Repository (integration)

#### Base H2 in-memory

Les tests d'integration utilisent une base H2 configuree dans `src/test/resources/META-INF/persistence.xml` :

| Propriete | Valeur |
|-----------|--------|
| Persistence Unit | `MasterAnnonceTestPU` |
| URL | `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1` |
| DDL | `create-drop` (schema recree a chaque test run) |

#### Chargement du jeu de donnees

Chaque classe de test charge son propre jeu de donnees dans `@BeforeEach` et nettoie la base dans `@AfterEach` pour garantir l'isolation entre tests :

```java
@BeforeEach
void setUp() {
    cleanDatabase();
    defaultAuthor = userService.create(new User("author", "author@test.com", "password"));
    defaultCategory = categoryService.create(new Category("Immobilier"));
}

@AfterEach
void tearDown() {
    cleanDatabase();
}

private void cleanDatabase() {
    try (EntityManager em = JPAUtil.getEntityManager()) {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createQuery("DELETE FROM Category").executeUpdate();
        em.getTransaction().commit();
    }
}
```

#### Tests implementes

| Classe de test | Nb tests | Couverture |
|----------------|----------|------------|
| `EntityMappingTest` | 8 | Persistance et relations JPA |
| `AnnonceRepositoryTest` | 12 | CRUD, pagination, recherche, filtres, comptage |
| `UserRepositoryTest` | 5 | CRUD, recherche par username/email |
| `AnnonceServiceTest` | 10 | Logique metier, ownership, transitions de statut |
| `CategoryServiceTest` | 4 | CRUD, doublons, integrite referentielle |
| `JPAUtilTest` | 4 | Gestion transactions et EntityManager |
| **Total** | **43** | **Toutes les couches** |

---

## Endpoints API

### Publics (sans authentification)

| Methode | URI | Description |
|---------|-----|-------------|
| GET | `/api/helloWorld` | Test - Hello World |
| GET | `/api/params?name=X&age=Y` | Test - QueryParams |
| GET | `/api/params/{id}` | Test - PathParam |

### Annonces

| Methode | URI | Auth | Description | Code |
|---------|-----|------|-------------|------|
| GET | `/api/annonces` | Non | Liste paginee (params: `page`, `size`, `q`, `category`, `status`) | 200 |
| GET | `/api/annonces/{id}` | Non | Detail d'une annonce | 200 / 404 |
| POST | `/api/annonces` | Oui | Creer une annonce | 201 / 400 |
| PUT | `/api/annonces/{id}` | Oui | Modifier une annonce (auteur uniquement) | 200 / 403 / 404 / 409 |
| DELETE | `/api/annonces/{id}` | Oui | Supprimer une annonce (auteur, archivee uniquement) | 204 / 403 / 404 / 409 |
| PATCH | `/api/annonces/{id}` | Oui | Changer le statut publish/archive (auteur uniquement) | 200 / 403 / 404 / 409 |

### Authentification

| Methode | URI | Description | Code |
|---------|-----|-------------|------|
| POST | `/api/auth/login` | Connexion (username/password -> token) | 200 / 401 |

---

## Problemes rencontres et solutions

### 1. Choix de l'implementation JAX-RS

**Probleme** : JAX-RS est une specification, pas une implementation. Tomcat 10.1 ne fournit pas d'implementation JAX-RS par defaut (contrairement a GlassFish ou WildFly).

**Solution** : Ajout de Jersey 3.1.6 comme implementation embarquee via Maven. Le module `jersey-container-servlet` enregistre automatiquement le `ServletContainer` de Jersey grace a l'annotation `@ApplicationPath`.

### 2. Mise a jour d'une annonce avec changement de categorie

**Probleme** : La methode `AnnonceService.update(Annonce)` existante fait un simple `em.merge()`, ce qui ne gere pas le changement de categorie (la nouvelle Category doit etre resolue dans le contexte de persistence).

**Solution** : Ajout d'une methode `AnnonceService.updateFields()` qui effectue la mise a jour dans une transaction unique : elle charge l'entite existante (managed), modifie ses champs, et resout la nouvelle Category via `em.find()`. L'entite etant managed, le commit de la transaction persiste automatiquement les modifications.

### 3. Suppression des Servlets/JSP

**Probleme** : L'application du TP #2 utilisait des Servlets et JSP pour le frontend. Le TP #3 exige "application totalement utilisable sans JSP, communication JSON uniquement".

**Solution** : Suppression complete de la couche Servlet/JSP (10 servlets, 11 JSP, `ValidationUtils`, `ValidationException`, `AuthenticationFilter`). L'API REST JAX-RS remplace integralement cette couche.

### 4. Propagation de l'identite utilisateur (SecurityContext)

**Probleme** : Apres validation du token dans le `SecurityFilter`, comment transmettre l'identite de l'utilisateur aux Resources de maniere standard ?

**Solution** : Utilisation du mecanisme JAX-RS standard `SecurityContext`. Le filtre cree un `UserSecurityContext` (wrappant un `UserPrincipal`) et l'injecte via `requestContext.setSecurityContext()`. La Resource recupere l'utilisateur via `@Context SecurityContext`, sans couplage direct avec le filtre.

### 5. @BeanParam pour les parametres de recherche

**Probleme** : L'endpoint GET /api/annonces accepte 5 parametres (page, size, q, category, status), ce qui alourdit la signature de la methode.

**Solution** : Regroupement dans `AnnonceSearchParams` (extends `PaginationParams`) avec `@BeanParam`. `PaginationParams` est generique et reutilisable pour tout endpoint pagine.

### 6. Integration JAAS dans une architecture REST stateless

**Probleme** : JAAS est historiquement concu pour des applications avec session (JNDI, EJB). L'integrer dans une API REST stateless sans serveur d'application (Tomcat seul) necessite d'adapter le chargement de la configuration et la propagation du Subject.

**Solution** : Le fichier `jaas.conf` est charge depuis le classpath via `AppContextListener.initJaas()` au demarrage. La propriete systeme est aussi configuree dans Maven Surefire pour les tests. Le `SecurityFilter` cree un `LoginContext` a chaque requete protegee, le Subject resultant est encapsule dans un `UserSecurityContext` JAX-RS standard, compatible avec `@Context SecurityContext`.

### 7. Organisation des exceptions par domaine metier

**Probleme** : Les exceptions etaient nommees par couche technique (`RepositoryException`, `ServiceException`), ce qui ne communique pas l'intention metier et couple les noms d'exceptions a l'architecture interne.

**Solution** : Refactoring en exceptions semantiques organisees par feature (`AnnonceImmutableException`, `ArchiveRequiredException`, `DuplicateUserException`, etc.). Une classe parente `ConflictException` permet au mapper JAX-RS d'intercepter toutes les sous-classes avec un seul `@Provider`, tout en gardant des noms expressifs dans le code metier.
