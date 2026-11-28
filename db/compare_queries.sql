-- Read-only queries used to compare the DATABASE with the API JSON and the browser (week 13 evidence).
-- PREREQUISITE: schema.sql, seed.sql, views.sql loaded in the database the application uses.
SELECT DATABASE() AS database_in_use, VERSION() AS mysql_version;

-- Row counts: should equal "totalElements" in the API pages (pets 8, owners 3, vaccines 4)
SELECT (SELECT COUNT(*) FROM pet)                AS pets,
       (SELECT COUNT(*) FROM app_user)           AS owners,
       (SELECT COUNT(*) FROM vaccine)            AS vaccines,
       (SELECT COUNT(*) FROM vaccination_record) AS vaccination_records;

-- Pet #1 three ways: this row == GET /api/pets/1 == the browser detail panel
SELECT id, name, species, micro_chip_number, contact_email, owner_id FROM pet WHERE id = 1;
SELECT id, username, email, created_at FROM app_user WHERE id = (SELECT owner_id FROM pet WHERE id = 1);
SELECT v.name AS vaccine, r.dose_number, r.date_given
FROM vaccination_record r JOIN vaccine v ON v.id = r.vaccine_id
WHERE r.pet_id = 1 ORDER BY r.date_given, r.dose_number;

-- Genuinely empty filter and missing ID (both 0 rows)
SELECT COUNT(*) AS hamsters FROM pet WHERE LOWER(species) = 'hamster';
SELECT COUNT(*) AS pet_999  FROM pet WHERE id = 999;

-- First page the browser shows (page size 5, sorted by id)
SELECT id, name, species, micro_chip_number, owner_id FROM pet ORDER BY id LIMIT 5 OFFSET 0;

-- Reports
SELECT * FROM v_pet_vaccination_report ORDER BY pet_id, date_given, dose_number;
SELECT * FROM v_vaccine_stock_usage ORDER BY vaccine_name;
