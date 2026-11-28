# Week 13 (F1) – Checkpoint
 
**Student:** Stephanie Mhay D. De Leon (2510576)

**Course:** WEBDEV2

**Branch:** `week13` (I created it from `week11`)

**Repository:** https://github.com/stepanotchie/webdev2/tree/week13

**My app is at:** the repository root

**In one sentence:** my Pet Clinic API now has a plain HTML/CSS/JS page that lists pets, owners, and vaccines from MySQL and shows one pet's details with its vaccination history.
 
---
 
## 1. Versions I used
 
| Item | Version |
|---|---|
| JDK | 17 (set in my `pom.xml`) – _add the output of `java -version`_ |
| Spring Boot | 4.1.2 |
| MySQL | _add the output of `SELECT VERSION();`_ |
| Browser | Chrome (incognito) – _add the version number_ |
| Database name | `webdev2_clinic` in the instructions below. On my laptop I used `webdev2_clinic_test`, and my screenshots show that name. |
 
---
 
## 2. How I run it
 
```bash
git clone https://github.com/stepanotchie/webdev2.git
cd webdev2
git switch week13
 
# 1. I make my private config (I never commit .env), then set DB_USERNAME / DB_PASSWORD
cp .env.example .env          # PowerShell: Copy-Item .env.example .env
 
# 2. I build my database from the scripts (they own the schema, see db/README.md)
mysql -u <user> -p -e "CREATE DATABASE webdev2_clinic CHARACTER SET utf8mb4"
mysql -u <user> -p webdev2_clinic < db/schema.sql
mysql -u <user> -p webdev2_clinic < db/seed.sql
mysql -u <user> -p webdev2_clinic < db/views.sql
#   PowerShell has no "<":  Get-Content db\schema.sql | mysql -u <user> -p webdev2_clinic
 
# 3. I start the app (Hibernate only checks the schema)
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
 
# 4. I open the page (Spring Boot serves it from the same origin as my API)
#    http://localhost:8080/
 
# 5. Optional: I compare SQL with the API and the page
mysql -u <user> -p webdev2_clinic < db/compare_queries.sql
 
# 6. Optional: I run my API smoke test (real HTTP calls, then it cleans up its own data)
./mvnw spring-boot:run -Dspring-boot.run.profiles=smoke
```
 
If `DB_URL` in my `.env` points to a different database name, my app uses that one.
 
---
 
## 3. My requirements and where to find the proof
 
| ID | What it should do | Where it lives | Test | Evidence |
|---|---|---|---|---|
| R1 | My Pet Clinic runs on MySQL with related saved data | `application.properties`, `model/*`, `db/schema.sql`, `db/seed.sql` | T1 | `07_mysql_tables.png` |
| R2 | My browser lists pets from the real API (records read from `content`) | `app.js` (`loadPets`), `api.js` | T2 | `01_pet_list.png` |
| R3 | My browser shows one pet's details, owner, and vaccination history | `showPet`, `OwnerController`, `VaccinationController` | T3 | `02_pet_detail.png` |
| R4 | Loading, empty, and failed-request states are visible and safe | `app.js` (`setStatus`, `loadList`, `textContent`) | T4, T5 | `03_empty_state.png`, `03_backend_down.png` |
| R5 | A missing ID gives a clear not-found message | `showPet` 404 branch, `ResourceNotFoundException` | T6 | `03_not_found.png` |
| R6 | My page is served over HTTP; CORS is limited to named origins; no secrets in the browser | `static/`, `WebConfig`, `config.js` | T10 | `05_cors_error_network.png`, `06_cors_fixed_network.png` |
| R7 | Browser row = API JSON = SQL row | `db/compare_queries.sql` | T2, T3 | `04_sql_row.png`, `04_api_pet1.png`, `02_pet_detail.png` |
| R8 | Someone else can set it up from scratch | this file, `.env.example`, `db/README.md`, `API.md` | clean clone | I haven't tested this yet |
| R9 | My week 11 behavior still works | `smoke/PetApiSmokeRunner` | T11 | I haven't captured this yet |
 
---
 
## 4. My tests: what I expected and what happened
 
| # | What I did | What I expected | What happened |
|---|---|---|---|
| T1 | I built the database with the three scripts, then started the app | Scripts run, app starts, counts are 3 owners / 8 pets / 4 vaccines / 6 records | **Pass.** My tables and views exist and the counts match (`07_mysql_tables.png`). |
| T1b | I start the app on an empty database | App refuses to start with a "missing table" error | _I haven't run this yet_ |
| T2 | I opened `http://localhost:8080/` | Pet table shows the first page, pager says 8 total | **Pass.** Page 1 of 2 (8 total), IDs 1–5 (`01_pet_list.png`). |
| T3 | I clicked **Details** for Buddy (ID 1) | Pet fields, owner `maria.santos`, 3 vaccinations | **Pass.** (`02_pet_detail.png`) |
| T4 | I filtered species `Hamster` | Friendly empty message (my API sends 200 with `content: []`) | **Pass.** "No pets found for species "Hamster"." (`03_empty_state.png`) |
| T5 | I stopped the backend, then changed the page | Error message shows and the page doesn't break | **Pass.** The request shows "(failed)" and my message appears (`03_backend_down.png`). I haven't yet recorded that it recovers after a restart. |
| T6 | I looked up ID `999` | "Pet #999 was not found." (my API sends 404) | **Pass.** Network shows 404 for `999` (`03_not_found.png`). |
| T7 | I look up `abc` | Message "Enter a whole number greater than 0." and no request is sent | _I haven't run this yet_ |
| T8 | I create a pet named `<b>x</b>` with Postman and reload | Shown as plain text, not bold | _I haven't run this yet_ |
| T9 | I open the Owners and Vaccines tabs | Both load; Bordetella shows "0 (out of stock)" | _I haven't run this yet_ |
| T10 | I ran my cross-origin experiment (below) | Blocked before the fix, works after | **Pass.** I got a 403 from `127.0.0.1:5500`, then a 200 from `localhost:5500` (`05_*`, `06_*`). |
| T11 | I run the `smoke` profile | Console ends with `SMOKE TEST PASSED (17 requests)` | _I haven't run this yet_ |
 
### My cross-origin experiment
 
1. I set `API_BASE: "http://localhost:8080"` in `static/js/config.js` (I don't commit this change).
2. I started VS Code Live Server on the `static` files. It opened my page at `http://127.0.0.1:5500/...`.
3. In DevTools → Network, my API request got a **403** and the list stayed empty, because `127.0.0.1:5500` is not my allowed origin.
4. I opened the same page at `http://localhost:5500/...`, which matches `CORS_ALLOWED_ORIGINS=http://localhost:5500` in my `.env`. My request got a **200** with `Access-Control-Allow-Origin`.
5. I put `API_BASE` back to `""`.
---
 
## 5. My known problems and limits
 
- My pet details panel is at the bottom of the page, so I have to scroll down after every **Details** click to see the result. I still need to redesign the layout so the details are visible without scrolling.
- My app connects to MySQL as `root`. I will switch to a limited account in week 15.
- I have no login yet, so every endpoint is public (week 15).
- I have no automated tests yet (week 16), so everything above is manual.
- My old MySQL password is still in git history (commit `5cdaa9c`). I need to change it.
- My CORS setup currently allows all headers and credentials. I will tighten this when I add login in week 15.
- My page shows the same message whether the server is down or CORS blocked the request, so I use DevTools to tell them apart.
- I haven't recorded tests T1b, T7, T8, T9, and T11 yet.
---
 
## 6. What I changed since week 11
 
- **Database:** I moved my schema, constraints, seed data, and views into `db/*.sql`. Hibernate only validates (`ddl-auto=validate`).
- **Config and secrets:** my DB settings now come from `.env` (git-ignored), and the password is out of `application.properties`.
- **New API:** I added read endpoints for owners, vaccines, and a pet's vaccination history, with their own DTOs and services. I also added `stock_doses` to `Vaccine`.
- **Code layout:** I split my flat `com` package into `com.clinic.{controller,service,repository,model,dto,exception,config}` and renamed the project `clinic`.
- **CORS:** my allowed origins are now configurable with `CORS_ALLOWED_ORIGINS`.
- **Smoke test:** I replaced `Week11SmokeRunner` with `PetApiSmokeRunner` (opt-in, cleans up after itself).
- **Frontend:** I built a vanilla JS page in `src/main/resources/static/`.
- **Cleanup:** I moved my Postman collection to `docs/api/` and removed generated `target/` files from version control.
---
 
## 7. My evidence
 
All my screenshots are in [`evidence/`](evidence/). See [evidence/README.md](evidence/README.md) for what each file shows.
 