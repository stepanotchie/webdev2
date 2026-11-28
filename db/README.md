# Database scripts (shared with INFOMAN1)

These four files are **copies** of `infoman1/week13/` (https://github.com/stepanotchie/infoman1/tree/main/week13), which holds the
authoritative versions plus the constraint-rejection tests and the view demo. Keep the copies identical.

| File | Purpose |
|---|---|
| `schema.sql` | Tables, keys, UNIQUE / CHECK / FOREIGN KEY constraints. **Owns the schema.** |
| `seed.sql` | Fictional, deterministic demo data |
| `views.sql` | `v_pet_vaccination_report` (join) and `v_vaccine_stock_usage` (aggregate) |
| `compare_queries.sql` | Read-only queries to compare SQL rows with API JSON and the browser |

## Execution order (MySQL 8.0.16+)

```bash
mysql -u <user> -p -e "CREATE DATABASE webdev2_clinic CHARACTER SET utf8mb4"
mysql -u <user> -p webdev2_clinic < db/schema.sql
mysql -u <user> -p webdev2_clinic < db/seed.sql
mysql -u <user> -p webdev2_clinic < db/views.sql
```

PowerShell has no `<`; use `Get-Content db\schema.sql | mysql -u <user> -p webdev2_clinic`, or open each file in MySQL Workbench and run it.
The scripts contain no `DROP` statements. To start over, create a new empty database (or drop your own **development** database by hand).
Hibernate runs with `ddl-auto=validate`: if a table or column is missing the app stops with a clear "Schema-validation" error.
