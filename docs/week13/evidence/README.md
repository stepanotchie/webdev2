# Week 13 evidence checklist

Save screenshots/exports here with these names. Redact passwords, tokens and cookies. Use only the fictional seed data.

| File | What it must show |
|---|---|
| `01_pet_list.png` | Browser pet list with several rows and the pager |
| `01b_api_pets.png` | `GET /api/pets` JSON (records inside `content`) |
| `02_pet_detail.png` | Detail panel for pet #1 (owner + vaccination history) |
| `03_empty_state.png` | Species filter with no matches (empty message) |
| `03_not_found.png` | Look-up of ID 999 -> not-found message, plus DevTools 404 |
| `03_backend_down.png` | Error message when the backend is stopped |
| `04_sql_api_ui_comparison.png` | SQL row, API JSON and UI row for pet #1 side by side |
| `04_sql_view_output.png` | Output of `v_pet_vaccination_report` |
| `05_cors_error_network.png` | DevTools Console + Network row of the CORS failure |
| `06_cors_fixed_network.png` | Same request after the fix (200 + `Access-Control-Allow-Origin`) |
| `07_mysql_tables.png` | `SHOW TABLES;` and row counts in `webdev2_clinic` |
| `08_smoke_test_console.png` | Console output of the `smoke` profile run ending with `SMOKE TEST PASSED (17 requests)` |
| `09_validate_empty_db_error.png` | App started against an EMPTY database: the `Schema-validation: missing table` error (test T1b) |
