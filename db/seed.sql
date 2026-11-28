-- ============================================================================
-- Pet Clinic database - seed.sql   (FICTIONAL, deterministic data)
-- PREREQUISITE: schema.sql has been run on the same (empty) database.
-- Same order as schema.sql: parents first. Explicit IDs keep every run identical.
-- ============================================================================

INSERT INTO app_user (id, username, email, created_at) VALUES
    (1, 'maria.santos',  'maria.santos@example.com',  '2026-11-01'),
    (2, 'juan.delacruz', 'juan.delacruz@example.com', '2026-11-01'),
    (3, 'ana.reyes',     'ana.reyes@example.com',     '2026-11-02');

INSERT INTO vaccine (id, name, stock_doses) VALUES
    (1, 'Rabies',     25),
    (2, 'DHPP',       18),
    (3, 'FVRCP',      12),
    (4, 'Bordetella',  0);   -- out of stock on purpose: used by the week 14 failure tests

INSERT INTO pet (id, owner_id, name, species, micro_chip_number, contact_email) VALUES
    (1, 1, 'Buddy', 'Dog',    'MC-1001', 'maria.santos@example.com'),
    (2, 1, 'Luna',  'Cat',    'MC-1002', NULL),
    (3, 1, 'Coco',  'Dog',    'MC-1007', NULL),
    (4, 2, 'Max',   'Dog',    'MC-1003', NULL),
    (5, 2, 'Milo',  'Cat',    'MC-1004', NULL),
    (6, 2, 'Nala',  'Cat',    'MC-1008', NULL),
    (7, 3, 'Rocky', 'Dog',    'MC-1005', NULL),
    (8, 3, 'Bella', 'Rabbit', 'MC-1006', NULL);

INSERT INTO vaccination_record (id, pet_id, vaccine_id, date_given, dose_number) VALUES
    (1, 1, 1, '2026-01-10', 1),   -- Buddy  Rabies  dose 1
    (2, 1, 2, '2026-01-10', 1),   -- Buddy  DHPP    dose 1
    (3, 1, 2, '2026-02-07', 2),   -- Buddy  DHPP    dose 2
    (4, 2, 3, '2026-03-02', 1),   -- Luna   FVRCP   dose 1
    (5, 4, 1, '2026-04-15', 1),   -- Max    Rabies  dose 1
    (6, 7, 2, '2026-05-20', 1);   -- Rocky  DHPP    dose 1
-- Pets 3, 5, 6, 8 (Coco, Milo, Nala, Bella) have no vaccinations yet: used to test LEFT JOIN reports.
