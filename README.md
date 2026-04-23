# Magoo — Système d'administration de cliniques

Application web de gestion administrative pour les cliniques Magoo, développée dans le cadre du cours de bases de données (session 4).

## Stack technique

| Couche | Technologie |
|--------|------------|
| Langage | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Persistance | Spring Data JPA / Hibernate |
| Base de données | PostgreSQL |
| Vues | Thymeleaf |
| Utilitaires | Lombok |
| Build | Maven |

L'application tourne sur le port **8020**.

---

## Fonctionnalités

### Patients
- Liste complète des patients avec statut (Actif / Ne pas rappeler / Décédé)
- Fiche détaillée : informations personnelles, adresse, médecin traitant, téléphones, examens
- Création, modification et suppression d'un dossier patient
- **Filtres de recherche** : ville, médecin, sexe, statut de rappel — combinables entre eux
- **Gestion des téléphones** directement depuis la fiche patient (ajout / suppression) — opération transactionnelle multi-tables

### Docteurs
- Liste des médecins avec leur clinique rattachée
- Création, modification et suppression
- Association à une clinique via liste déroulante

### Cliniques
- Liste des cliniques avec leur ville
- Création, modification et suppression
- Association à une ville via liste déroulante

### Villes
- Gestion complète des villes (nom, province, pays)
- Création, modification et suppression

### Examens
- Liste de tous les examens, triés par date décroissante
- Création et modification : choix du patient, du type d'examen et de la date
- Suppression avec confirmation

### Types d'examens (`liste_examen`)
- Référentiel des types d'examens disponibles (Complet, Partiel, Urgence oculaire, etc.)
- Création, modification et suppression

---

## Architecture du code

L'application suit une architecture **MVC à trois couches** strictement séparées :

```
Controller  →  Service  →  Repository  →  Base de données
   (HTTP)     (Logique)    (Requêtes)       (PostgreSQL)
```

### Couche Controller
Responsable uniquement de la couche HTTP : recevoir les requêtes, appeler le service approprié, passer les données au modèle Thymeleaf et rediriger. Les controllers ne contiennent aucune logique métier ni accès direct à la base de données.

### Couche Service
Contient toute la logique applicative. C'est ici que sont définies les règles métier et les opérations transactionnelles. Chaque méthode de modification (`save`, `delete`, `addTelephone`, etc.) est annotée `@Transactional` pour garantir l'intégrité des données.

### Couche Repository
Interfaces Spring Data JPA qui définissent les requêtes vers la base de données. Chaque requête personnalisée est documentée avec le SQL équivalent en commentaire.

### Scalabilité

Cette architecture est pensée pour grandir sans réécriture :

- **Ajouter un module** (ex. : assurances) = créer une entité, un repository, un service et un controller. Les autres modules ne sont pas touchés.
- **Changer de base de données** (ex. : MySQL, H2) = changer le driver dans `pom.xml` et `application.properties`. Le code Java reste identique grâce à JPA.
- **Ajouter de la logique métier** (ex. : envoyer un courriel à la création d'un patient) = modifier uniquement le `PatientService`. Le controller et le repository ne changent pas.
- **Exposer une API REST** = ajouter des `@RestController` qui appellent les mêmes services. Aucune duplication de logique.

---

## JPA vs SQL brut

Sans JPA, chaque accès à la base de données demande d'écrire manuellement la connexion, la requête, la lecture du `ResultSet` et la fermeture des ressources :

```java
// Sans JPA — accès manuel
Connection conn = DriverManager.getConnection(url, user, password);
PreparedStatement stmt = conn.prepareStatement(
    "SELECT p.*, d.nom_complet FROM patient p LEFT JOIN docteur d ON p.id_docteur = d.id WHERE p.id = ?"
);
stmt.setInt(1, id);
ResultSet rs = stmt.executeQuery();
Patient patient = new Patient();
if (rs.next()) {
    patient.setId(rs.getInt("id"));
    patient.setPrenom(rs.getString("prenom"));
    patient.setNom(rs.getString("nom"));
    // ... 15 autres champs
}
rs.close();
stmt.close();
conn.close();
```

Avec JPA, la même opération devient :

```java
// Avec JPA
Optional<Patient> patient = patientRepository.findByIdWithDetails(id);
```

### Avantages concrets de JPA dans ce projet

| Problème | Sans JPA | Avec JPA |
|----------|----------|----------|
| Connexions non fermées | Risque de fuite mémoire | Géré automatiquement par HikariCP |
| SQL injection | Requiert attention constante | Impossible — toutes les requêtes sont des `PreparedStatement` |
| Mapping objet-relationnel | Écriture manuelle champ par champ | Annotations `@Entity`, `@Column`, `@ManyToOne` |
| Jointures multiples | Requêtes SQL longues et fragiles | `LEFT JOIN FETCH` en JPQL, résultat directement typé |
| Transactions | `conn.setAutoCommit(false)` / `conn.rollback()` manuels | `@Transactional` sur la méthode de service |
| Changement de BD | Réécriture des requêtes SQL | Changer uniquement le driver |

### Requêtes préparées et sécurité

Toutes les requêtes de l'application passent par JPA/Hibernate, qui utilise **systématiquement des `PreparedStatement`**. Les paramètres ne sont jamais concaténés dans une chaîne SQL — ils sont liés via des placeholders (`?` ou `:param`). L'injection SQL est donc structurellement impossible.

### Gestion des connexions

Spring Boot configure automatiquement un **pool de connexions HikariCP**. Chaque requête emprunte une connexion du pool et la retourne immédiatement après usage. Il n'y a aucune connexion ouverte en permanence et aucun risque de fuite.

### Transactions

L'ajout d'un téléphone à un patient, par exemple, touche deux tables (`patient` et `telephone`). La méthode `PatientService.addTelephone()` est annotée `@Transactional` : si une erreur survient après l'écriture dans `telephone` mais avant la confirmation, Hibernate effectue un **rollback automatique** et la base reste dans un état cohérent.

```java
@Transactional
public void addTelephone(Integer patientId, Telephone telephone) {
    telephone.setPatient(patientRepository.getReferenceById(patientId));
    telephoneRepository.save(telephone);
}
```

---

## Thymeleaf

Thymeleaf est un moteur de templates Java qui génère du HTML côté serveur. Contrairement à une approche API + JavaScript, les vues sont des fichiers HTML valides que le navigateur peut ouvrir directement — ce qui facilite la collaboration et les tests visuels.

### Héritage de layout

Un fragment partagé (`fragments/layout.html`) définit la structure commune (balise `<head>`, sidebar de navigation). Chaque page l'inclut avec une seule ligne :

```html
<head th:replace="~{fragments/layout :: head('Patients')}"></head>
<div th:replace="~{fragments/layout :: sidebar}"></div>
```

Sans Thymeleaf, il faudrait copier-coller le HTML de navigation dans chaque fichier. Une modification du menu demanderait de toucher toutes les pages.

### Binding automatique des formulaires

Thymeleaf lie les champs d'un formulaire directement à un objet Java :

```html
<form th:object="${patient}">
    <input type="text" th:field="*{prenom}">
    <input type="text" th:field="*{nom}">
</form>
```

Spring MVC reçoit le formulaire et reconstruit automatiquement l'objet `Patient` — aucun parsing manuel des paramètres HTTP.

### Logique dans les vues

Les conditions, boucles et formatages se font directement dans le HTML sans mélanger Java :

```html
<!-- Boucle sur une liste -->
<tr th:each="p : ${patients}">
    <td th:text="${p.nomComplet}"></td>
    <td th:text="${#temporals.format(p.dateNaissance, 'dd/MM/yyyy')}"></td>
    <span th:if="${p.estDecede}" class="badge badge-red">Décédé</span>
</tr>
```

### Sélection conservée après filtre

Thymeleaf permet de pré-sélectionner la valeur d'un `<select>` après soumission d'un filtre, sans JavaScript :

```html
<option th:each="v : ${villes}"
        th:value="${v.id}"
        th:text="${v.nom}"
        th:selected="${villeId != null and villeId == v.id}">
</option>
```

---

## Lombok

Lombok est un processeur d'annotations qui génère automatiquement du code Java répétitif à la compilation. Il ne modifie pas le bytecode à l'exécution — le code généré est identique à ce qu'un développeur écrirait à la main.

### Sans Lombok — une entité typique

```java
public class Ville {
    private Integer id;
    private String nom;
    private String province;
    private String pays;

    public Ville() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}
```

### Avec Lombok

```java
@Getter @Setter @NoArgsConstructor
public class Ville {
    private Integer id;
    private String nom;
    private String province;
    private String pays;
}
```

Lombok génère les getters, setters et le constructeur sans argument à la compilation. Le résultat compilé est strictement identique.

### `@RequiredArgsConstructor` dans les services et controllers

```java
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final VilleRepository villeRepository;
    private final DocteurRepository docteurRepository;
    private final TelephoneRepository telephoneRepository;
}
```

Lombok génère un constructeur avec tous les champs `final` comme paramètres. Spring utilise ce constructeur pour l'**injection de dépendances** — c'est la méthode recommandée par Spring car elle garantit que les dépendances sont toujours initialisées et permet de tester les classes sans Spring.

---

## Lancer le projet

### Prérequis
- Java 21
- PostgreSQL (base `magoo`, utilisateur `raphaelhoule`, port 5432)
- Maven

### Initialisation de la base
Exécuter le script SQL fourni par l'enseignant pour créer les tables et insérer les données de départ.

### Démarrage
```bash
./mvnw spring-boot:run
```

L'application est accessible à l'adresse : [http://localhost:8020](http://localhost:8020)
