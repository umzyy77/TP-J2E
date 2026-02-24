# MasterAnnonce - TP4 Spring Boot

API REST Spring Boot (JWT, JPA, Actuator) avec outillage DevOps pour execution locale, conteneurisation et qualite.

## Documentation

- Sujet TP4: `fichier-md-prog-java-2e/semaine4/TP_AIR_4_Spring_Boot.md`
- Details architecture/metier: `docs/TP4_README.md`
- README complet du TP4: [ouvrir le README du TP](docs/TP4_README.md)

## Stack technique

- Java 25
- Spring Boot 4.0.2
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Spring Actuator
- Rate limiting (login)
- JUnit 5, Mockito, Testcontainers
- Docker / Docker Compose
- JaCoCo + Sonar (configuration dans `sonar-project.properties`)

## Demarrage rapide (local)

1. Lancer PostgreSQL via Docker:

```bash
docker compose up -d postgres
```

2. Lancer l'application:

```bash
mvn spring-boot:run
```

3. Endpoints utiles:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui`
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`
- Auth refresh: `POST http://localhost:8080/api/auth/refresh`

## Demarrage full Docker (app + postgres)

Commande unique:

```bash
docker compose up --build -d
```

Variables de configuration:

- Fichier exemple: `.env.example`
- API: `APP_NAME`, `APP_PORT`, `SWAGGER_UI_PATH`
- DB: `DB_URL`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_PORT`
- Security: `BCRYPT_ROUNDS` (cout BCrypt backend + seed SQL)
- JWT: `JWT_SECRET`, `JWT_EXPIRATION`, `JWT_REFRESH_SECRET`, `JWT_REFRESH_EXPIRATION`
- Rate limit login: `RATE_LIMIT_LOGIN_ENABLED`, `RATE_LIMIT_LOGIN_PATH`, `RATE_LIMIT_LOGIN_MAX_REQUESTS`, `RATE_LIMIT_LOGIN_WINDOW_SECONDS`
- Actuator/info: `ACTUATOR_*`, `INFO_APP_*`
- Seed SQL (Docker init): `SEED_DEFAULT_PASSWORD` (defaut `password123`)

Si le port `8080` est deja pris:

```powershell
$env:APP_PORT='8081'
docker compose up --build -d
```

Endpoints en mode Docker (si `APP_PORT=8081`):

- API: `http://localhost:8081`
- Health: `http://localhost:8081/actuator/health`
- Info: `http://localhost:8081/actuator/info`

## Commandes DevOps utiles

Afficher les services:

```bash
docker compose ps
```

Logs application:

```bash
docker compose logs -f app
```

Arret:

```bash
docker compose down
```

Arret + suppression volume DB:

```bash
docker compose down -v
```

## Tests et qualite

Unitaires:

```bash
mvn test
```

Integration:

```bash
mvn integration-test
```

Suite complete:

```bash
mvn verify
```

## Actuator (Exercice 11)

- `GET /actuator/health` expose l'etat global + composant `db`
- `GET /actuator/info` expose `info.app.name`, `info.app.description`, `info.app.version`

Configuration principale: `src/main/resources/application.yml`

## Dockerisation (Exercice 12)

- `Dockerfile` multi-stage present a la racine
- `docker-compose.yml` lance `app` + `postgres`
- Healthcheck PostgreSQL configure pour la dependance applicative

## CI / Pipeline (Exercice 13)

Workflow GitHub Actions: `.github/workflows/ci.yml`

Declenchements:

- `push` sur n'importe quelle branche
- `pull_request` vers `main`

Choix Java:

- Le projet est maintenu en Java 25, donc la CI est configuree en Java 25.

Etapes executees:

- Checkout (`actions/checkout`)
- Setup Java 25 + cache Maven (`actions/setup-java` avec `cache: maven`)
- Build + tests + packaging: `mvn -B clean verify` (aucun skip)
- Publication artifact JAR nomme `master-annonce-jar`
- Publication artifact JaCoCo (`jacoco-report`)
- Build Docker automatique sur chaque `push`/`pull_request` + publication artifact image (`master-annonce-docker-image`)

Strategie DB en CI (choix demande):

- Option retenue: **Testcontainers**
- Raisons: pas de service PostgreSQL declare dans le workflow, tests d'integration auto-portables, pipeline plus reproductible.

Artifact principal attendu:

- Nom: `master-annonce-jar`
- Contenu: JAR Spring Boot genere dans `target/`

Preuve du workflow vert:

- Lien du dernier run GitHub Actions: a renseigner apres premier push sur GitHub.

Commande cible pour CI:

```bash
mvn -B clean verify
```

Problemes rencontres (court retour):

1. L'enonce propose Java 17/21, mais le projet est deja en Java 25.
Solution: pipeline alignee sur Java 25 pour rester coherent avec le code source et l'environnement local.

2. Les tests d'integration ont besoin d'une base PostgreSQL.
Solution: Testcontainers est utilise pour eviter une configuration manuelle d'un service DB dans le workflow.

3. Le pipeline Docker doit etre automatique pour chaque branche.
Solution: job Docker execute sur tous les `push` et `pull_request`, sans etape manuelle.

## Bonus implementes

- Refresh token JWT (secret/expiration dedies)
- Endpoint `POST /api/auth/refresh`
- Rate limiting sur `POST /api/auth/login` (HTTP 429 + header `Retry-After`)
- Couverture de tests > 80% (JaCoCo)
- Pipeline Docker automatique en CI

## Sonar

Configuration projet:

- `sonar-project.properties`

Exemple d'analyse:

```bash
mvn clean verify sonar:sonar -Dsonar.token=<TOKEN>
```
