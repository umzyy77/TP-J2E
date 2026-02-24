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
- `pull_request` vers `main` uniquement

Choix Java:

- Le projet est maintenu en Java 25, donc la CI est configuree en Java 25.

Etapes executees:

- Checkout (`actions/checkout`)
- Setup Java 25 + cache Maven (`actions/setup-java` avec `cache: maven`)
- Build + tests + packaging: `mvn -B clean verify` (aucun skip)
- Publication artifact JAR nomme `master-annonce-jar`
- Publication artifact JaCoCo (`jacoco-report`)
- Build Docker conditionnel: uniquement sur `push` vers `main` ou sur tag
  - Image taguee `master-annonce:<commit-sha>`
  - Publication artifact `master-annonce-docker-image` (image exportee en `.tar`)

Strategie DB en CI (choix demande):

- Option retenue: **Testcontainers**
- Raisons: pas de service PostgreSQL declare dans le workflow, tests d'integration auto-portables, pipeline plus reproductible.

Artifacts produits:

| Artifact | Contenu |
|----------|---------|
| `master-annonce-jar` | JAR Spring Boot (`target/*-SNAPSHOT.jar`) |
| `jacoco-report` | Rapport de couverture JaCoCo |
| `master-annonce-docker-image` | Image Docker exportee (`.tar`), tag `master-annonce:<commit-sha>` |

Preuve du workflow vert:

- Lien du dernier run GitHub Actions: a renseigner apres premier push sur GitHub.

Commande cible pour CI:

```bash
mvn -B clean verify
```

Checklist de validation:

- [x] Workflow declenche sur `push` + `pull_request`
- [x] Java installe + cache Maven
- [x] `mvn clean verify` execute (aucun skip)
- [x] Tests executes (unitaires + integration)
- [x] Artifact `.jar` publie (`master-annonce-jar`)
- [x] Base de donnees fonctionnelle (Testcontainers)
- [x] Pipeline reproductible et stable
- [x] Build Docker conditionnel (bonus)
- [x] Rapport JaCoCo publie (bonus)

Problemes rencontres (court retour):

1. L'enonce propose Java 17/21, mais le projet est deja en Java 25.
Solution: pipeline alignee sur Java 25 pour rester coherent avec le code source et l'environnement local.

2. Les tests d'integration ont besoin d'une base PostgreSQL.
Solution: Testcontainers est utilise pour eviter une configuration manuelle d'un service DB dans le workflow.

3. Le pipeline Docker doit etre automatique pour chaque branche.
Solution: job Docker execute sur tous les `push` et `pull_request`, sans etape manuelle.

## Collection Postman

Fichier: `MasterAnnonce.postman_collection.json` (import via Postman > Import > Upload File)

## Bonus implementes

- Refresh token JWT (secret/expiration dedies)
- Endpoint `POST /api/auth/refresh`
- Rate limiting sur `POST /api/auth/login` (HTTP 429 + header `Retry-After`)
- Couverture de tests > 80% (JaCoCo)
- Pipeline Docker automatique en CI

## SuperBonus Kubernetes (Minikube)

Manifests dans le dossier `/k8s` :

| Fichier | Description |
|---------|-------------|
| `namespace.yaml` | Namespace `masterannonce` |
| `postgres-secret.yaml` | Secrets PostgreSQL (base64) |
| `app-secret.yaml` | Secrets applicatifs JWT + DB (base64) |
| `app-configmap.yaml` | Configuration non-sensible (env vars) |
| `postgres-init-configmap.yaml` | Scripts SQL init + seed |
| `postgres-pvc.yaml` | PersistentVolumeClaim 1Gi |
| `postgres-deployment.yaml` | Deployment PostgreSQL (1 replica, probes) |
| `postgres-service.yaml` | Service ClusterIP port 5432 |
| `app-deployment.yaml` | Deployment app (2 replicas, readiness/liveness probes) |
| `app-service.yaml` | Service NodePort port 8080 |
| `ingress.yaml` | Ingress sur `masterannonce.local` |

### Commandes de deploiement

```bash
# 1. Demarrer Minikube
minikube start

# 2. Activer Ingress
minikube addons enable ingress

# 3. Utiliser le Docker de Minikube
# Linux/macOS:
eval $(minikube docker-env)
# Windows PowerShell:
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# 4. Build l'image dans Minikube
docker build -t masterannonce:1.0 .

# 5. Deployer tous les manifests
kubectl apply -f k8s/

# 6. Verifier les pods
kubectl get pods -n masterannonce

# 7. Verifier les services
kubectl get svc -n masterannonce

# 8. Verifier l'ingress
kubectl get ingress -n masterannonce

# 9. Acceder a l'API
minikube service app -n masterannonce --url
```

### A) Commandes d'execution

Toutes les commandes sont listees ci-dessus (etapes 1 a 8). Resultat attendu :

```
kubectl get pods -n masterannonce
NAME                        READY   STATUS    RESTARTS   AGE
app-xxxxxxxxxx-xxxxx        1/1     Running   0          2m
app-xxxxxxxxxx-yyyyy        1/1     Running   0          2m
postgres-xxxxxxxxxx-zzzzz   1/1     Running   0          2m
```

### B) Preuve que l'API fonctionne

Recuperer l'URL Minikube :

```bash
minikube service app -n masterannonce --url
# exemple de sortie : http://192.168.49.2:31234
```

Appel login :

```bash
curl -X POST http://<MINIKUBE_URL>/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password123"}'
```

Reponse attendue :

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400000,
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshExpiresIn": 604800000
}
```

Appel liste annonces :

```bash
curl http://<MINIKUBE_URL>/api/annonces \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

Reponse attendue : liste paginee JSON des annonces seedees.

### C) Preuve des probes

```bash
kubectl describe pod -l app=masterannonce -n masterannonce
```

Extrait attendu :

```
Containers:
  masterannonce:
    ...
    Readiness:  http-get http://:8080/actuator/health delay=30s timeout=3s period=10s #success=1 #failure=3
    Liveness:   http-get http://:8080/actuator/health delay=40s timeout=3s period=15s #success=1 #failure=3
    ...
Conditions:
  Ready:  True
```

Les pods en READY 1/1 confirment que les probes passent correctement.

### Points d'attention

- `imagePullPolicy: Never` : l'image est build localement dans Minikube
- Secrets encodes en base64 dans les manifests (ne pas committer de valeurs en clair en production)
- Health probes sur `/actuator/health` (readiness: 30s delay, liveness: 40s delay)
- 2 replicas pour l'application (scalabilite horizontale)

## Sonar

Configuration projet:

- `sonar-project.properties`

Exemple d'analyse:

```bash
mvn clean verify sonar:sonar -Dsonar.token=<TOKEN>
```
