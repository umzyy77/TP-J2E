# MasterAnnonce - TP4 Spring Boot

API REST Spring Boot (JWT, JPA, Actuator) avec outillage DevOps pour execution locale, conteneurisation et qualite.

## Documentation

- Sujet TP4: `fichier-md-prog-java-2e/semaine4/TP_AIR_4_Spring_Boot.md`
- Details architecture/metier: `docs/TP4_README.md`

## Stack technique

- Java 25
- Spring Boot 4.0.2
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Spring Actuator
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

## Demarrage full Docker (app + postgres)

Commande unique:

```bash
docker compose up --build -d
```

Variables de configuration:

- Fichier exemple: `.env.example`
- Port API Docker: `APP_PORT` (defaut `8080`)
- Port DB Docker: `DB_PORT` (defaut `5432`)

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

Le workflow GitHub Actions n'est pas encore versionne dans ce repo (`.github/workflows/ci.yml` absent).

Commande cible pour CI:

```bash
mvn -B clean verify
```

## Sonar

Configuration projet:

- `sonar-project.properties`

Exemple d'analyse:

```bash
mvn clean verify sonar:sonar -Dsonar.token=<TOKEN>
```
