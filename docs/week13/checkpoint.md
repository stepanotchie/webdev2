# Week 13 (F1) - Checkpoint

**Student:** Stephanie Mhay D. De Leon (2510576) | **Course:** WEBDEV2 | **Branch:** `week13` (from `week11`)
**Repository:** https://github.com/stepanotchie/webdev2/tree/week13 | **Application path:** repository root
**INFOMAN1 cross-reference:** https://github.com/stepanotchie/infoman1/tree/main/week13 (schema, seed, views, integrity tests). `db/` here holds identical copies of the four runnable scripts so this repository can be reproduced on its own.

## 1. Versions

| Item | Version |
|---|---|
| JDK | 17 (`java.version` in `pom.xml`) - [FILL IN: output of `java -version`] |
| Spring Boot | 4.1.2 (`spring-boot-starter-parent`) |
| MySQL | [FILL IN: output of `SELECT VERSION();`] |
| Database name | `webdev2_clinic` |
| Browser | [FILL IN: e.g. Chrome 1xx] |

## 2. Setup and run commands

```bash
git clone https://github.com/stepanotchie/webdev2.git
cd webdev2
git switch week13

# 1. Create your private config from the example (never commit .env)
cp .env.example .env        # Windows PowerShell: Copy-Item .env.example .env
# edit .env: set DB_USERNAME / DB_PASSWORD for your local MySQL

# 2. Build the database from the SQL scripts (they own the schema; see db/README.md)
mysql -u <user> -p -e "CREATE DATABASE webdev2_clinic CHARACTER SET utf8mb4"
mysql -u <user> -p webdev2_clinic < db/schema.sql
mysql -u <user> -p webdev2_clinic < db/seed.sql
mysql -u <user> -p webdev2_clinic < db/views.sql
#   PowerShell has no "<":  Get-Content db\schema.sql | mysql -u <user> -p webdev2_clinic

# 2b. Start the app (Hibernate only VALIDATES the schema)
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run

# 3. Open the frontend (served by Spring Boot, same origin as the API)
#    http://localhost:8080/

# 4. Optional: read-only comparison queries (SQL vs API vs browser)
mysql -u <user> -p webdev2_clinic < db/compare_queries.sql

# 5. Optional: API regression smoke test (runs real HTTP calls once at startup, then cleans up its data)
./mvnw spring-boot:run -Dspring-boot.run.profiles=smoke
#   PowerShell: ./mvnw spring-boot:run "-Dspring-boot.run.profiles=smoke"
#   or set SPRING_PROFILES_ACTIVE=smoke before starting
```

## 3. Requirements -> feature/file -> test/evidence

| ID | Requirement | Feature / file | Test | Evidence |
|---|---|---|---|---|
| R1 | Existing Pet Clinic runs on MySQL with related persisted data | `application.properties`, `model/*`, `db/schema.sql`, `db/seed.sql` | T1 | `evidence/01_*` |
| R2 | Browser lists pets from the real API (records read from `content`) | `static/js/app.js loadPets`, `api.js` | T2 | `evidence/01_pet_list.png` |
| R3 | Browser shows details of a selected pet incl. owner and vaccination history | `showPet`, `controller/OwnerController`, `controller/VaccinationController` | T3 | `evidence/02_pet_detail.png` |
| R4 | Loading, empty and failed-request states are visible and safe | `static/js/app.js` (`setStatus`, `loadList`, `textContent`) | T4, T5, T7, T8 | `evidence/03_*` |
| R5 | Missing ID gives a clear not-found result | `showPet` 404 branch, `ResourceNotFoundException` | T6 | `evidence/03_not_found.png` |
| R6 | Frontend served over HTTP; origin config is scoped, no secrets in browser | `static/`, `config/WebConfig`, `static/js/config.js` | T9, T10 | `evidence/05_*`, `06_*` |
| R7 | UI row equals API JSON equals SQL row | `db/compare_queries.sql`, `db/views.sql` | T2, T3 | `evidence/04_*` |
| R8 | Reproducible setup | this file, `.env.example`, `API.md` | clean-clone run | - |
| R9 | Week 11 API behavior is preserved | `smoke/PetApiSmokeRunner` | T11 | `evidence/08_smoke_test_console.png` |

## 4. Test cases (execute and record ACTUAL results)

| # | Steps | Expected | Actual |
|---|---|---|---|
| T1 | Create empty `webdev2_clinic`, run `schema.sql`, `seed.sql`, `views.sql`, then start the app | Scripts run without error; app starts (`ddl-auto=validate` passes); counts 3 owners, 8 pets, 4 vaccines, 6 vaccination records | [FILL IN] |
| T1b | Start the app against an EMPTY database (no scripts run) | App refuses to start with a `Schema-validation: missing table` error (proves Hibernate no longer creates tables) | [FILL IN] |
| T2 | Open `http://localhost:8080/` | Pets table shows 8 rows, sorted by ID; compare one row to `GET /api/pets` JSON | [FILL IN] |
| T3 | Click **Details** for Buddy (ID 1) | Panel shows pet fields, owner `maria.santos`, 3 vaccination rows | [FILL IN] |
| T4 | Filter species `Hamster` -> Apply | Empty-state message; API returned 200 with `content: []` | [FILL IN] |
| T5 | Stop the backend, click Apply or reload | Error message "Cannot reach the server..."; page does not break; recovers after restart | [FILL IN] |
| T6 | Look up ID `999` | "Pet #999 was not found." (API 404) | [FILL IN] |
| T7 | Look up `abc` | Client message "Enter a whole number greater than 0."; no request sent | [FILL IN] |
| T8 | Add a pet name `<b>x</b>` via curl/Postman, reload list | Shown as plain text, not bold | [FILL IN] |
| T9 | Owners and Vaccines tabs | Tables load; Bordetella shows `0 (out of stock)` | [FILL IN] |
| T10 | Cross-origin experiment (below) | Blocked by CORS before the fix; works after | [FILL IN] |
| T11 | Run with profile `smoke` (command in section 2) | Console ends with `SMOKE TEST PASSED (17 requests)`; week 11 CRUD, 409 duplicate, 404s and empty-filter 200 all still behave as before; `owners` count unchanged afterwards | [FILL IN] |

### Cross-origin experiment (for the DevTools diagnosis)
1. Keep the backend running with the default `CORS_ALLOWED_ORIGINS` (`http://localhost:3000`).
2. In `src/main/resources/static/js/config.js` set `API_BASE: "http://localhost:8080"` (do not commit this change).
3. From `src/main/resources/static`, run `python -m http.server 5500` and open `http://localhost:5500/`.
4. Observe the error in DevTools -> Console/Network; screenshot it (`evidence/05_cors_error_network.png`).
5. Set `CORS_ALLOWED_ORIGINS=http://localhost:5500` in `.env`, restart the backend, reload; screenshot (`evidence/06_cors_fixed_network.png`).
6. Restore `API_BASE: ""` and stop the static server.

## 5. Current defects / limitations

- App connects as MySQL `root` -> restricted user planned for week 15.
- Runtime still connects as MySQL `root`; the restricted account is week 15. (Schema is now owned by `db/schema.sql` with `ddl-auto=validate`.)
- No authentication yet: all endpoints are public (week 15).
- No automated tests yet (week 16); results above are manual.
- [FILL IN: any real defect you found while testing, or "none found"]

## 6. Change log (week 11 -> week 13)

- Schema, constraints, seed data and views now live in SQL scripts (`db/`, mirrored from `infoman1/week13`); `ddl-auto` changed `create` -> `validate`; the Java `DemoDataSeeder` was removed in favour of `seed.sql`.
- Config moved to environment variables; `.env` ignored; committed password removed from the working tree (rotate the old MySQL password; it remains in git history).
- Added `stock_doses` to `Vaccine`.
- Added read endpoints: owners, vaccines, pet vaccination history, with DTOs and services.
- CORS origins configurable; old `Week11SmokeRunner` replaced by `smoke/PetApiSmokeRunner` (opt-in profile, updated messages, also checks the new read endpoints, removes its temporary data); 
- Reorganized the flat `com` package into `com.clinic.{controller,service,repository,model,dto,exception,config}`; renamed `Week11Application` -> `ClinicApplication`; project `taskmanager`/`com.week7` -> `clinic`/`com.clinic`.
- Moved the Postman collection to `docs/api/` and removed old lab names from it.
- Added vanilla JS frontend (`static/`), SQL scripts and views (`db/`), docs.
- Removed generated `target/` files from version control.

## 7. Evidence index (`docs/week13/evidence/`)

See [evidence/README.md](evidence/README.md) for the expected file names.
