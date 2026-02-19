# MasterAnnonce - TP3 Backend API REST

Backend Java/Jakarta EE expose en API REST JSON, securise via JAAS + Bearer token.

## Consigne TP

- Redirection vers le sujet: [TP_AIR_3_Backend_API](./fichier%20md%20prog%20java%202e/semaine%203/TP_AIR_3_Backend_API.md)

## Stack

- Java 25
- Jakarta EE 10 (JAX-RS, Validation, Persistence)
- Jersey 3.1.6
- Hibernate 6.6.4.Final
- PostgreSQL (runtime) / H2 in-memory (tests)

## Architecture

```
Client HTTP -> Resource (JAX-RS) -> Service (metier + transactions) -> Repository (JPA) -> DB
```

## Endpoints principaux

- `POST /api/login` : authentification, renvoie `{ token, expiresIn }`
- `GET /api/annonces`
- `GET /api/annonces/{id}`
- `POST /api/annonces` (auth)
- `PUT /api/annonces/{id}` (auth)
- `PATCH /api/annonces/{id}` (auth)
- `DELETE /api/annonces/{id}` (auth)
- `GET /api/openapi` : spec OpenAPI JSON

## Regles metier cle

- Seul l'auteur (derive du token) peut modifier/supprimer.
- Une annonce `PUBLISHED` est immutable.
- Suppression autorisee uniquement en statut `ARCHIVED`.
- Concurrence optimiste via `@Version`.

## Lancer

```bash
mvn clean package
mvn cargo:run
```

## Tests

```bash
# Unitaires uniquement
mvn test

# Integration uniquement
mvn verify -DskipUnitTests=true

# Tous les tests (unitaires + integration)
mvn verify
```

## Logging structure

- Logback JSON: `src/main/resources/logback.xml`

## OpenAPI

- Source: `src/main/resources/openapi.yaml`
- Exposition HTTP (JSON): `GET /api/openapi`

## Test de charge simple

- Script k6: `docs/load-test-k6.js`
- Exemple:

```bash
k6 run -e BASE_URL=http://localhost:8080/MasterAnnonce/api docs/load-test-k6.js
```
