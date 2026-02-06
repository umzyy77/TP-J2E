# MasterAnnonce - Application Web JPA/Hibernate

Application de gestion d'annonces développée avec Jakarta EE, JPA/Hibernate et PostgreSQL.

## 🏗️ Architecture

```
src/main/java/org/example/tpj2eannonces/
├── config/         # Configuration (DatabaseConfig)
├── exception/      # Exceptions personnalisées
├── filter/         # Filtres Servlet (AuthenticationFilter)
├── model/          # Entités JPA (User, Category, Annonce, AnnonceStatus)
├── repository/     # Couche persistence JPA (JPQL uniquement)
├── service/        # Couche métier avec gestion des transactions
├── servlet/        # Contrôleurs Web (Servlets)
└── utils/          # Utilitaires (JPAUtil, ValidationUtils)
```

### Couches applicatives

| Couche | Responsabilité |
|--------|----------------|
| **Servlet** | Gestion HTTP, validation entrées, délégation au service |
| **Service** | Logique métier, gestion des transactions |
| **Repository** | Accès aux données via JPQL |
| **Model** | Entités JPA avec Bean Validation |

## 🔧 Technologies

- **Java 21** avec Jakarta EE 10
- **JPA 3.1** avec Hibernate 6.4
- **PostgreSQL** (production) / **H2** (tests)
- **Maven** pour la gestion de projet
- **JUnit 5** + **AssertJ** pour les tests

## 📊 Modèle de données

### Entités

- **User** : utilisateurs avec authentification
- **Category** : catégories d'annonces
- **Annonce** : annonces avec statut (DRAFT, PUBLISHED, ARCHIVED)

### Relations

- `User` ←→ `Annonce` : OneToMany / ManyToOne
- `Category` ←→ `Annonce` : OneToMany / ManyToOne

## 🚀 Fonctionnalités

### Authentification
- Login / Logout avec gestion de session
- Inscription avec validation
- Filtre de sécurité protégeant les pages privées

### Gestion des annonces
- CRUD complet
- Recherche par mot-clé
- Filtrage par catégorie et statut
- Pagination des résultats
- Actions Publish / Archive

## ⚠️ Problèmes rencontrés et solutions

### 1. Configuration persistence.xml

**Problème** : Les entités n'étaient pas détectées par Hibernate.

**Solution** : Déclaration explicite des classes dans `persistence.xml` :
```xml
<class>org.example.tpj2eannonces.model.Annonce</class>
<class>org.example.tpj2eannonces.model.User</class>
<class>org.example.tpj2eannonces.model.Category</class>
```

### 2. Gestion des transactions

**Problème** : Ouverture de transactions dans les servlets menant à des fuites de ressources.

**Solution** : Transactions gérées exclusivement dans la couche Service avec pattern try-catch-finally :
```java
EntityManager em = JPAUtil.getEntityManager();
try {
    em.getTransaction().begin();
    // ... opérations
    em.getTransaction().commit();
} catch (Exception e) {
    if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
    }
    throw e;
} finally {
    em.close();
}
```

### 3. Lazy Loading et LazyInitializationException

**Problème** : Accès aux relations après fermeture de l'EntityManager.

**Solution** : Utilisation de `JOIN FETCH` dans les requêtes JPQL :
```java
String jpql = "SELECT a FROM Annonce a " +
    "LEFT JOIN FETCH a.author " +
    "LEFT JOIN FETCH a.category " +
    "WHERE a.id = :id";
```

### 4. Type d'ID UUID vs Long

**Problème** : Migration de l'existant utilisant UUID vers JPA.

**Solution** : Conservation de UUID avec `@GeneratedValue(strategy = GenerationType.UUID)` pour compatibilité et éviter les collisions.

### 5. Contraintes d'intégrité à la suppression

**Problème** : Suppression de catégories avec des annonces liées.

**Solution** : Vérification dans le service avant suppression :
```java
Long count = em.createQuery("SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :id", Long.class)
    .setParameter("id", categoryId)
    .getSingleResult();
if (count > 0) {
    throw new ServiceException("Impossible de supprimer: " + count + " annonce(s) liée(s)");
}
```

## 🧪 Tests

### Exécution
```bash
mvn test
```

### Couverture
- **EntityMappingTest** : Mapping JPA des entités (8 tests)
- **AnnonceRepositoryTest** : CRUD et requêtes JPQL (13 tests)
- **UserRepositoryTest** : Gestion utilisateurs (6 tests)
- **AnnonceServiceTest** : Logique métier et transactions (9 tests)
- **CategoryServiceTest** : Contraintes d'intégrité (4 tests)
- **JPAUtilTest** : Configuration JPA (4 tests)

**Total : 44 tests**

## 📝 Configuration

### Production (PostgreSQL)
```properties
# application.properties
db.url=jdbc:postgresql://localhost:5432/MasterAnnonce
db.user=postgres
db.password=postgres
```

### Tests (H2)
Configuration automatique via `src/test/resources/META-INF/persistence.xml` avec base H2 in-memory.

## 🚀 Déploiement

### Prérequis
- **Java 21** ou supérieur
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Apache Tomcat 10.1+** (compatible Jakarta EE 10)

### Configuration de la base de données

1. Créer la base de données PostgreSQL :
```sql
CREATE DATABASE MasterAnnonce;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE MasterAnnonce TO postgres;
```

2. Configurer `src/main/resources/application.properties` :
```properties
db.url=jdbc:postgresql://localhost:5432/MasterAnnonce
db.user=postgres
db.password=postgres
```

### Compilation et packaging

```bash
# Nettoyer et compiler
mvn clean compile

# Exécuter les tests
mvn test

# Créer le WAR
mvn package -DskipTests
```

### Déploiement sur Tomcat

1. **Copier le WAR** :
```bash
cp target/MasterAnnonce.war $CATALINA_HOME/webapps/
```

2. **Démarrer Tomcat** :
```bash
$CATALINA_HOME/bin/startup.sh  # Linux/Mac
$CATALINA_HOME/bin/startup.bat # Windows
```

3. **Accéder à l'application** :
```
http://localhost:8080/MasterAnnonce/
```

### Déploiement avec Docker (optionnel)

```bash
# Build de l'image
docker build -t masterannonce .

# Lancer avec docker-compose
docker-compose up -d
```

### Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/MasterAnnonce` |
| `DB_USER` | Utilisateur BDD | `postgres` |
| `DB_PASSWORD` | Mot de passe BDD | `postgres` |

## 📁 Structure des fichiers clés

```
persistence.xml          # Configuration JPA
├── src/main/resources/META-INF/   (PostgreSQL)
└── src/test/resources/META-INF/   (H2)

JPAUtil.java            # Singleton EntityManagerFactory
GenericRepository.java  # Repository générique CRUD
AnnonceService.java     # Service métier avec transactions
AuthenticationFilter.java  # Filtre de sécurité
```
