-- ============================================================================
-- Pet Clinic database - views.sql
-- PREREQUISITE: schema.sql (and normally seed.sql) have been run.
-- A view stores a SELECT, not data: it always shows the CURRENT base-table rows.
-- CREATE OR REPLACE makes this file safe to re-run without dropping anything.
-- ============================================================================

-- Report 1 (join view): one row per pet/vaccination, pets with no vaccination still appear.
CREATE OR REPLACE VIEW v_pet_vaccination_report AS
SELECT p.id           AS pet_id,
       p.name         AS pet_name,
       p.species      AS species,
       u.username     AS owner_username,
       v.name         AS vaccine_name,
       r.dose_number  AS dose_number,
       r.date_given   AS date_given
FROM pet p
JOIN app_user u                ON u.id = p.owner_id
LEFT JOIN vaccination_record r ON r.pet_id = p.id
LEFT JOIN vaccine v            ON v.id = r.vaccine_id;

-- Report 2 (aggregate view): stock left and how often each vaccine has been given.
CREATE OR REPLACE VIEW v_vaccine_stock_usage AS
SELECT v.id                   AS vaccine_id,
       v.name                 AS vaccine_name,
       v.stock_doses          AS stock_doses,
       COUNT(r.id)            AS doses_given,
       COUNT(DISTINCT r.pet_id) AS pets_vaccinated
FROM vaccine v
LEFT JOIN vaccination_record r ON r.vaccine_id = v.id
GROUP BY v.id, v.name, v.stock_doses;
