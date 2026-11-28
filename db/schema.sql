-- ============================================================================
-- Pet Clinic database - schema.sql            (INFOMAN1 week 13, shared with WEBDEV2)
-- ----------------------------------------------------------------------------
-- OWNS the schema. The Spring Boot app only VALIDATES it (ddl-auto=validate).
-- Requires MySQL 8.0.16+ (CHECK constraints are enforced) and InnoDB.
-- Contains no database name on purpose: create an EMPTY database first, then run
-- this file against it (see checkpoint.md, "Execution order").
-- Tables are created parent-before-child: app_user, vaccine -> pet -> vaccination_record.
-- No DROP statements: to retry, create a NEW empty database instead.
-- ============================================================================

-- Pet owner (the JPA entity AppUser).  1 owner -> many pets
CREATE TABLE app_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL,
    created_at  DATE         NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT pk_app_user          PRIMARY KEY (id),
    CONSTRAINT uk_app_user_username UNIQUE (username),
    CONSTRAINT chk_app_user_email   CHECK (email LIKE '%_@_%')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Vaccine catalogue with remaining stock.  1 vaccine -> many vaccination records
CREATE TABLE vaccine (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    name         VARCHAR(80) NOT NULL,
    stock_doses  INT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_vaccine       PRIMARY KEY (id),
    CONSTRAINT uk_vaccine_name  UNIQUE (name),
    CONSTRAINT chk_vaccine_stock CHECK (stock_doses >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Patient.  Every pet belongs to exactly one owner.  1 pet -> many vaccination records
CREATE TABLE pet (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    owner_id           BIGINT       NOT NULL,
    name               VARCHAR(100) NOT NULL,
    species            VARCHAR(50)  NOT NULL,
    micro_chip_number  VARCHAR(50)  NOT NULL,
    contact_email      VARCHAR(100) NULL,
    CONSTRAINT pk_pet            PRIMARY KEY (id),
    CONSTRAINT uk_pet_microchip  UNIQUE (micro_chip_number),
    CONSTRAINT fk_pet_owner      FOREIGN KEY (owner_id) REFERENCES app_user (id)
                                 ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT chk_pet_name      CHECK (CHAR_LENGTH(TRIM(name)) >= 2),
    CONSTRAINT chk_pet_contact   CHECK (contact_email IS NULL OR contact_email LIKE '%_@_%')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- One dose of one vaccine given to one pet.  Resolves pet <-> vaccine (many-to-many with attributes)
CREATE TABLE vaccination_record (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    pet_id      BIGINT NOT NULL,
    vaccine_id  BIGINT NOT NULL,
    date_given  DATE   NOT NULL,
    dose_number INT    NOT NULL,
    CONSTRAINT pk_vaccination_record PRIMARY KEY (id),
    CONSTRAINT uk_pet_vaccine_dose   UNIQUE (pet_id, vaccine_id, dose_number),
    CONSTRAINT fk_vr_pet             FOREIGN KEY (pet_id)     REFERENCES pet (id)
                                     ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_vr_vaccine         FOREIGN KEY (vaccine_id) REFERENCES vaccine (id)
                                     ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT chk_vr_dose_number    CHECK (dose_number >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
