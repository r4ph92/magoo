# Présentation — Projet Magoo

## Stack technologique

- **Java + Spring Boot** — framework web backend, gère le routing HTTP, l'injection de dépendances, les transactions
- **PostgreSQL** — base de données relationnelle
- **JPA / Hibernate** — ORM (Object-Relational Mapper) : mappe les tables SQL en classes Java
- **Thymeleaf** — moteur de templates HTML côté serveur
- **Lombok** — génère automatiquement getters, setters, constructeurs via annotations

---

## Architecture — Séparation des couches

```
Navigateur → Controller → Service → Repository → PostgreSQL
```

- **Controller** (`PatientController.java`) — reçoit les requêtes HTTP, appelle le service, retourne la vue. Aucun accès direct à la BD.
- **Service** (`PatientService.java`) — toute la logique métier est ici. Méthodes annotées `@Transactional`.
- **Repository** (`PatientRepository.java`) — interface Spring Data JPA. Accès à la BD uniquement. Le controller n'importe jamais un repository directement.

---

## A. Interaction et Manipulation des données (40 pts)

### Opérations CRUD (20 pts)

- Tous les modules ont les 4 opérations : **Créer, Lire, Modifier, Supprimer**
- Modules couverts : Patients, Docteurs, Cliniques, Villes, Examens, Listes d'examens
- Les téléphones sont ajoutés / supprimés directement sur la fiche patient
- Aucune erreur d'intégrité référentielle : les suppressions respectent les contraintes FK définies dans le schéma SQL (ex. : supprimer un patient supprime en cascade ses téléphones et examens)

### Requêtes avancées & filtres (10 pts)

- **Profil complet d'un patient** : une seule requête charge le patient + docteur + clinique + ville (3 LEFT JOINs)

```java
// PatientRepository.java
@Query("SELECT p FROM Patient p LEFT JOIN FETCH p.docteur d LEFT JOIN FETCH d.clinique LEFT JOIN FETCH p.ville WHERE p.id = :id")
// SQL généré par Hibernate :
// SELECT p.*, d.*, c.*, v.*
// FROM patient p
// LEFT JOIN docteur d ON p.id_docteur = d.id
// LEFT JOIN clinique c ON d.id_clinique = c.id
// LEFT JOIN ville v ON p.id_ville = v.id
// WHERE p.id = ?
Optional<Patient> findByIdWithDetails(@Param("id") Integer id);
```

- **Filtres de recherche patient** : `GET /patients?villeId=2&sexe=F&docteurId=3&nePasRappeler=true`
  - Tous les paramètres sont optionnels
  - Filtrés en Java (stream) après la requête principale

```java
// PatientService.java
return patientRepository.findAllWithDetails().stream()
    .filter(p -> villeId == null || p.getVille().getId().equals(villeId))
    .filter(p -> sexe == null || sexe.equals(p.getSexe()))
    .filter(p -> nePasRappeler == null || nePasRappeler.equals(p.getNePasRappeler()))
    .toList();
```

### Gestion des connexions et sécurité (10 pts)

- **Prepared statements systématiques** : JPA / Hibernate génère toujours `WHERE id = ?` avec les valeurs passées séparément. Aucune concaténation de string SQL → injection SQL impossible.

```java
// Exemple : même si l'utilisateur entre "1 OR 1=1", c'est traité comme une valeur littérale
@Query("... WHERE p.id = :id")
// Hibernate envoie : WHERE p.id = ? avec la valeur séparée
```

- **Connection pool automatique (HikariCP)** : Spring Boot configure un pool de connexions. On ne crée jamais de connexion manuellement. Spring en prend une au début de chaque requête et la remet dans le pool à la fin → aucun risque de fuite de connexion.

---

## B. Techniques de programmation (30 pts)

### Robustesse et logique transactionnelle (15 pts)

- Toutes les méthodes qui modifient la BD sont annotées `@Transactional`
- Si une erreur survient, Spring fait automatiquement un **rollback** — la base reste cohérente
- Si tout réussit, Spring fait le **commit**

```java
// PatientService.java
@Transactional
public void addTelephone(Integer patientId, Telephone telephone) {
    telephone.setPatient(patientRepository.getReferenceById(patientId));
    telephoneRepository.save(telephone);
    // Si le save() plante → rollback, le téléphone n'est pas inséré
}

@Transactional
public void save(Patient patient, Integer villeId, Integer docteurId) {
    patient.setVille(villeId != null ? villeRepository.getReferenceById(villeId) : null);
    patient.setDocteur(docteurId != null ? docteurRepository.getReferenceById(docteurId) : null);
    patientRepository.save(patient);
    // INSERT ou UPDATE atomique — tout ou rien
}
```

### Architecture du code (15 pts)

- Séparation stricte : le controller ne parle qu'au service, le service parle aux repositories
- Le controller ne contient aucune logique métier — juste du routing HTTP
- Les repositories ne contiennent pas de logique — juste des requêtes
- Lombok (`@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor`) évite ~150 lignes de boilerplate par classe

---

## JPA / ORM — Pourquoi ?

**Sans JPA** — 20+ lignes pour lire un patient :
```java
Connection conn = DriverManager.getConnection("jdbc:postgresql://...", user, pass);
PreparedStatement ps = conn.prepareStatement("SELECT * FROM patient WHERE id = ?");
ps.setInt(1, id);
ResultSet rs = ps.executeQuery();
Patient p = new Patient();
p.setNom(rs.getString("nom"));
p.setPrenom(rs.getString("prenom"));
// ... × 15 champs
conn.close(); // oublier ça = fuite de connexion
```

**Avec JPA** — une ligne :
```java
patientRepository.findById(id);
```

**Ce que JPA fait automatiquement :**
- Génère le SQL (`SELECT`, `INSERT`, `UPDATE`, `DELETE`)
- Mappe les résultats vers les objets Java
- Gère les connexions et les transactions
- Donne gratuitement `findAll()`, `findById()`, `save()`, `deleteById()` via `JpaRepository`

**Entity = table SQL :**
```java
@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                  // colonne id, auto-increment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docteur")
    private Docteur docteur;             // clé étrangère → table docteur
}
```

- `@ManyToOne` = plusieurs patients peuvent avoir le même docteur (FK)
- `FetchType.LAZY` = le docteur n'est chargé que si on y accède → performance

---

## Fichiers clés à montrer pendant la démo

---

### `entity/Patient.java` — Entité JPA, annotations, relations

```java
@Entity                        // cette classe = une table SQL
@Table(name = "patient")
@Getter @Setter @NoArgsConstructor   // Lombok : remplace ~150 lignes de getters/setters
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;              // colonne id, auto-increment

    @Column(nullable = false, length = 100)
    private String nom;              // colonne nom, NOT NULL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docteur")
    private Docteur docteur;         // clé étrangère → table docteur

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ville")
    private Ville ville;             // clé étrangère → table ville
}
```

- `@ManyToOne` = plusieurs patients peuvent avoir le même docteur (FK)
- `FetchType.LAZY` = le docteur n'est chargé que si on y accède → performance
- Lombok génère tous les getters/setters automatiquement via 3 annotations

---

### `repository/PatientRepository.java` — Interface JPA, jointures, SQL commenté

```java
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    // JpaRepository donne GRATUITEMENT : findAll(), findById(), save(), deleteById()

    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.docteur d LEFT JOIN FETCH d.clinique LEFT JOIN FETCH p.ville WHERE p.id = :id")
    // SQL généré par Hibernate :
    // SELECT p.*, d.*, c.*, v.*
    // FROM patient p
    // LEFT JOIN docteur d ON p.id_docteur = d.id
    // LEFT JOIN clinique c ON d.id_clinique = c.id
    // LEFT JOIN ville v ON p.id_ville = v.id
    // WHERE p.id = ?   ← prepared statement, injection SQL impossible
    Optional<Patient> findByIdWithDetails(@Param("id") Integer id);
}
```

- C'est une interface — je n'implémente rien, Spring génère le code automatiquement
- J'ai écrit moi-même les 3 `LEFT JOIN FETCH` pour charger tout le profil en une seule requête
- Sans ça, Hibernate ferait une requête séparée par patient pour charger le docteur (problème N+1)
- Le `:id` est un prepared statement → valeur toujours passée séparément du SQL

---

### `service/PatientService.java` — `@Transactional`, logique métier, filtres

```java
@Service
@RequiredArgsConstructor
public class PatientService {

    // Filtres combinables — tous optionnels
    public List<Patient> findWithFilters(Integer villeId, Integer docteurId, String sexe, Boolean nePasRappeler) {
        return patientRepository.findAllWithDetails().stream()
            .filter(p -> villeId == null || p.getVille().getId().equals(villeId))
            .filter(p -> sexe == null || sexe.equals(p.getSexe()))
            .toList();
    }

    // Transaction : commit si tout réussit, rollback automatique si erreur
    @Transactional
    public void save(Patient patient, Integer villeId, Integer docteurId) {
        patient.setVille(villeId != null ? villeRepository.getReferenceById(villeId) : null);
        patient.setDocteur(docteurId != null ? docteurRepository.getReferenceById(docteurId) : null);
        patientRepository.save(patient);
    }

    // Opération multi-table → transaction obligatoire
    @Transactional
    public void addTelephone(Integer patientId, Telephone telephone) {
        telephone.setPatient(patientRepository.getReferenceById(patientId));
        telephoneRepository.save(telephone);
    }
}
```

- `@Transactional` = si une erreur survient, Spring fait un **rollback** automatique
- Si tout réussit → **commit**
- Pour `addTelephone`, on touche 2 tables simultanément — si l'insert plante, la liaison est annulée aussi

---

### `controller/PatientController.java` — Routing HTTP, séparation des couches

```java
@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;   // parle SEULEMENT au service
    // ← aucun import de repository ici

    @GetMapping                          // READ
    public String index(Model model) { ... }

    @PostMapping                         // CREATE
    public String store(@ModelAttribute Patient patient, ...) { ... }

    @PostMapping("/{id}")                // UPDATE
    public String update(@PathVariable Integer id, ...) { ... }

    @PostMapping("/{id}/delete")         // DELETE
    public String delete(@PathVariable Integer id) { ... }
}
```

- Le controller ne contient aucune logique métier — juste du routing HTTP
- Il reçoit la requête, appelle le service, retourne la vue
- Aucun repository importé directement dans le controller

