# MasterAnnonce

Application Jakarta Servlet/JSP/JDBC pour la gestion d'annonces (CRUD) avec PostgreSQL.

## Prerequis
- Java 25
- Maven 3.9+
- Tomcat 10.1.52
- PostgreSQL (local ou Docker)

## Initialisation base de donnees
### Option 1 : Docker (recommande)
```
docker compose up -d
```
Les scripts `db/init.sql` et `db/seed.sql` sont executes automatiquement au premier demarrage.
Le script active l'extension `pgcrypto` pour generer les UUID (necessite un user superuser).

### Option 2 : PostgreSQL local
1. Creer la base `MasterAnnonce`.
2. Executer le schema :
   - `db/init.sql`
3. Inserer des donnees :
   - `db/seed.sql`

## Configuration
Fichier `src/main/resources/application.properties` :
```
db.url=jdbc:postgresql://localhost:5432/MasterAnnonce
db.user=postgres
db.password=postgres
```

Variables Docker dans `.env` :
```
DB_NAME=MasterAnnonce
DB_USER=postgres
DB_PASSWORD=postgres
DB_PORT=5432
```

Si vous changez les valeurs dans `.env`, mettez a jour aussi `application.properties`.

## Build
```
mvn clean package
```
## Run avec maven cargo (tomcat depuis le pom.xml)
```
mvn cargo:run
```

## Run avec Tomcat embarque (Maven)
```
mvn tomcat10:run
```
Puis ouvrir: `http://localhost:8080/MasterAnnonce`

## Coverage (JaCoCo)
```
mvn test
```
Rapport: `target/site/jacoco/index.html`

## SonarQube (optionnel)
Demarrer SonarQube en local:
```
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest
```
Puis analyser:
```
mvn clean verify sonar:sonar -Dsonar.projectKey=MasterAnnonce -Dsonar.host.url=http://localhost:9000 -Dsonar.login=YOUR_TOKEN
```

## Deploiement
1. Deployer le WAR genere (`target/MasterAnnonce.war`) dans Tomcat.
2. Demarrer Tomcat.

## URLs utiles
- Liste: `/MasterAnnonce/AnnonceList`
- Ajout: `/MasterAnnonce/AnnonceAdd`
- Modification: `/MasterAnnonce/AnnonceUpdate?id=<uuid>`
- Suppression: `/MasterAnnonce/AnnonceDelete?id=<uuid>`
