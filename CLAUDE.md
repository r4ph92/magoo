# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build without running tests
./mvnw clean package -DskipTests

# Run tests
./mvnw test
```

App runs on **port 8020**. Requires PostgreSQL on port 5432, database `magoo`, user `raphaelhoule`.
`ddl-auto=none` — the schema must be created manually via the teacher's SQL init script before first run.

## Architecture

Standard Spring Boot MVC with three strict layers:

```
Controller → Service → Repository → PostgreSQL
```

- **Controllers** (`controller/`) — HTTP only. Parse path variables/request params, call one service, pass data to the model, redirect. No repository imports allowed in controllers.
- **Services** (`service/`) — All business logic lives here. All mutating methods are `@Transactional`. Cross-table operations (e.g. adding a phone to a patient) are handled here, not in controllers.
- **Repositories** (`repository/`) — Spring Data JPA interfaces. Custom queries use `@Query` with JPQL and `LEFT JOIN FETCH` to avoid N+1 problems. Every custom query has a SQL comment below it showing the equivalent SQL Hibernate generates.
- **Entities** (`entity/`) — JPA entities with Lombok (`@Getter @Setter @NoArgsConstructor`). Relationships use `FetchType.LAZY`.

## Key patterns

**Entity dropdowns in forms** — entity FK selects use `name="entityId"` (not `th:field`) and the controller receives `@RequestParam Integer entityId`, then calls `repository.getReferenceById(entityId)` to set the relation. See `PatientController.store()` for reference.

**Layout** — all pages include the shared sidebar and head via `th:replace="~{fragments/layout :: sidebar}"`. Never duplicate nav HTML.

**Flash messages** — success feedback uses `RedirectAttributes.addFlashAttribute("success", "...")` and is displayed in templates with `th:if="${success}"`.

**Telephones** — phones have no standalone module. They are added/deleted exclusively through `POST /patients/{id}/telephones` and `POST /patients/{id}/telephones/{telId}/delete`, handled in `PatientController` and `PatientService`.

**Patient filters** — `GET /patients` accepts optional `villeId`, `docteurId`, `sexe`, `nePasRappeler` query params. Filtering is applied in `PatientService.findWithFilters()` as Java stream filters on top of `findAllWithDetails()` (Hibernate's JPQL `IS NULL` parameter checks are unreliable for join path expressions in this version).

## Modules

6 modules: **Patients**, **Docteurs**, **Cliniques**, **Villes**, **Examens**, **Liste d'examens**. Téléphones is managed through the patient show page only (no sidebar link, no standalone controller).
