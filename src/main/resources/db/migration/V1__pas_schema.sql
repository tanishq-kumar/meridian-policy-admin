-- V1__pas_schema.sql — meridian-policy-admin (pas, 8 tables)
-- Spec: docs/02-data-model-catalog.md §A, docs/01 §1
-- Mirrors infra/postgres/init/01-schemas.sql which also creates these schemas for the compose volume.
CREATE SCHEMA IF NOT EXISTS pas;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- A5 — must exist before commercial_policy_master (FK)
CREATE TABLE pas.policyholder_party_profile (
    policyholder_id             BIGSERIAL PRIMARY KEY,
    legal_name                  VARCHAR(200) NOT NULL,
    dba_name                    VARCHAR(200),
    entity_type_code            VARCHAR(20)  NOT NULL CHECK (entity_type_code IN ('CORP','LLC','PARTNERSHIP','SOLE_PROP','NONPROFIT')),
    fein_encrypted              BYTEA,
    naics_code                  VARCHAR(6),
    sic_code                    VARCHAR(4),
    mailing_address_line_1      VARCHAR(200),
    mailing_city                VARCHAR(100),
    mailing_state_code          VARCHAR(2),
    mailing_postal_code         VARCHAR(10),
    primary_contact_name        VARCHAR(200),
    primary_contact_email       VARCHAR(200),
    primary_contact_phone       VARCHAR(30),
    year_established            INT,
    employee_count              INT,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- A6 — self-referential hierarchy
CREATE TABLE pas.producer_agency_contract (
    producer_id                         BIGSERIAL PRIMARY KEY,
    agency_code                         VARCHAR(12)  NOT NULL UNIQUE,
    agency_name                         VARCHAR(200) NOT NULL,
    dba_name                            VARCHAR(200),
    producer_type_code                  VARCHAR(20)  NOT NULL CHECK (producer_type_code IN ('RETAIL_AGENT','WHOLESALE_BROKER','MGA','DIRECT')),
    license_number                      VARCHAR(40),
    license_state_code                  VARCHAR(2),
    license_expiration_date             DATE,
    contracted_commission_pct           NUMERIC(5,2),
    contingent_commission_eligible_flag BOOLEAN      NOT NULL DEFAULT FALSE,
    binding_authority_limit_amount      NUMERIC(14,2),
    appointment_effective_date          DATE,
    appointment_termination_date        DATE,
    parent_producer_id                  BIGINT       REFERENCES pas.producer_agency_contract(producer_id),
    created_at                          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at                          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- A1 — FKs to A5/A6
CREATE TABLE pas.commercial_policy_master (
    policy_id                   BIGSERIAL PRIMARY KEY,
    policy_number               VARCHAR(20)  NOT NULL UNIQUE,
    policyholder_id             BIGINT       NOT NULL REFERENCES pas.policyholder_party_profile(policyholder_id),
    producer_id                 BIGINT       NOT NULL REFERENCES pas.producer_agency_contract(producer_id),
    product_code                VARCHAR(10)  NOT NULL CHECK (product_code IN ('CGL','CPROP','WC','CAUTO')),
    underwriting_company_code   VARCHAR(5)   NOT NULL CHECK (underwriting_company_code IN ('MMIC','MMGA')),
    policy_term_effective_date  DATE         NOT NULL,
    policy_term_expiration_date DATE         NOT NULL,
    policy_status_code          VARCHAR(15)  NOT NULL CHECK (policy_status_code IN ('QUOTED','BOUND','IN_FORCE','ENDORSED','CANCELLED','EXPIRED','NON_RENEWED')),
    written_premium_amount      NUMERIC(14,2) NOT NULL DEFAULT 0,
    original_effective_date     DATE,
    renewal_of_policy_number    VARCHAR(20),
    cancellation_date           DATE,
    cancellation_reason_code    VARCHAR(20)  CHECK (cancellation_reason_code IS NULL OR cancellation_reason_code IN ('NONPAY','INSURED_REQUEST','UNDERWRITING','AUDIT_NONCOMPLIANCE')),
    created_at                  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CHECK (policy_term_expiration_date > policy_term_effective_date)
);
CREATE INDEX idx_pas_policy_holder   ON pas.commercial_policy_master(policyholder_id);
CREATE INDEX idx_pas_policy_producer ON pas.commercial_policy_master(producer_id);
CREATE INDEX idx_pas_policy_status   ON pas.commercial_policy_master(policy_status_code);

-- A2
CREATE TABLE pas.policy_coverage_line_detail (
    coverage_line_id            BIGSERIAL PRIMARY KEY,
    policy_id                   BIGINT       NOT NULL REFERENCES pas.commercial_policy_master(policy_id) ON DELETE CASCADE,
    line_of_business_code       VARCHAR(10)  NOT NULL CHECK (line_of_business_code IN ('GL','PROP','WC','AUTO')),
    coverage_part_code          VARCHAR(20)  NOT NULL,
    limit_per_occurrence_amount NUMERIC(14,2),
    limit_aggregate_amount      NUMERIC(14,2),
    deductible_amount           NUMERIC(14,2),
    coinsurance_pct             NUMERIC(5,2),
    peril_set_code              VARCHAR(20)  CHECK (peril_set_code IN ('SPECIAL','NAMED_PERILS','WC_STATUTORY')),
    coverage_premium_amount     NUMERIC(14,2) NOT NULL DEFAULT 0,
    coverage_effective_date     DATE,
    coverage_expiration_date    DATE
);
CREATE INDEX idx_pas_cov_policy ON pas.policy_coverage_line_detail(policy_id);

-- A3
CREATE TABLE pas.insured_risk_location_schedule (
    risk_location_id                BIGSERIAL PRIMARY KEY,
    policy_id                       BIGINT       NOT NULL REFERENCES pas.commercial_policy_master(policy_id) ON DELETE CASCADE,
    location_sequence_number        INT          NOT NULL,
    address_line_1                  VARCHAR(200),
    city                            VARCHAR(100),
    state_code                      VARCHAR(2),
    postal_code                     VARCHAR(10),
    county_fips                     VARCHAR(5),
    latitude                        NUMERIC(10,7),
    longitude                       NUMERIC(10,7),
    construction_class_code         VARCHAR(20)  CHECK (construction_class_code IN ('FRAME','JOISTED_MASONRY','NON_COMBUSTIBLE','MASONRY_NC','FIRE_RESISTIVE')),
    occupancy_class_code            VARCHAR(20),
    sprinklered_flag                BOOLEAN,
    building_value_amount           NUMERIC(14,2),
    contents_value_amount           NUMERIC(14,2),
    business_income_value_amount    NUMERIC(14,2),
    annual_receipts_amount          NUMERIC(14,2),
    catastrophe_zone_code           VARCHAR(20),
    UNIQUE (policy_id, location_sequence_number)
);

-- A4 — retro-effective endorsements (effective < processed)
CREATE TABLE pas.policy_endorsement_transaction (
    endorsement_id              BIGSERIAL PRIMARY KEY,
    policy_id                   BIGINT       NOT NULL REFERENCES pas.commercial_policy_master(policy_id) ON DELETE CASCADE,
    endorsement_sequence_number INT          NOT NULL,
    endorsement_type_code       VARCHAR(20)  NOT NULL CHECK (endorsement_type_code IN ('ADD_LOCATION','REMOVE_LOCATION','LIMIT_CHANGE','ADDL_INSURED','CLASS_CODE_CHANGE','PREMIUM_AUDIT','CANCEL_REWRITE')),
    endorsement_effective_date  DATE         NOT NULL,
    endorsement_processed_date  DATE         NOT NULL,
    premium_delta_amount        NUMERIC(14,2) NOT NULL DEFAULT 0,
    initiating_source_code      VARCHAR(20)  CHECK (initiating_source_code IN ('INSURED','AGENT','COMPANY','AUDIT')),
    transaction_timestamp       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (policy_id, endorsement_sequence_number)
);
CREATE INDEX idx_pas_endorse_policy ON pas.policy_endorsement_transaction(policy_id);
CREATE INDEX idx_pas_endorse_effective ON pas.policy_endorsement_transaction(endorsement_effective_date);

-- A7 — rating basis; nullable FK on coverage line (some LOBs are policy-level exposures)
CREATE TABLE pas.premium_exposure_rating_basis (
    exposure_basis_id       BIGSERIAL PRIMARY KEY,
    coverage_line_id        BIGINT       REFERENCES pas.policy_coverage_line_detail(coverage_line_id) ON DELETE CASCADE,
    exposure_type_code      VARCHAR(20)  NOT NULL CHECK (exposure_type_code IN ('PAYROLL','GROSS_RECEIPTS','SQUARE_FOOTAGE','VEHICLE_COUNT','PER_LOCATION')),
    exposure_units          NUMERIC(14,2) NOT NULL,
    rate_per_unit           NUMERIC(10,4) NOT NULL,
    basis_period_start_date DATE,
    basis_period_end_date   DATE,
    audit_status_code       VARCHAR(20)  NOT NULL DEFAULT 'ESTIMATED' CHECK (audit_status_code IN ('ESTIMATED','AUDITED','AUDIT_WAIVED'))
);

-- A8
CREATE TABLE pas.underwriting_submission_clearance (
    submission_id           BIGSERIAL PRIMARY KEY,
    policy_id               BIGINT       REFERENCES pas.commercial_policy_master(policy_id) ON DELETE SET NULL,
    submission_received_date DATE        NOT NULL,
    underwriter_id          VARCHAR(40),
    clearance_status_code   VARCHAR(20)  NOT NULL CHECK (clearance_status_code IN ('CLEAR','REFERRED','DECLINED','WITHDRAWN')),
    referral_reason_code    VARCHAR(40),
    authority_level_code    VARCHAR(20),
    decision_date           DATE,
    bound_flag              BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_pas_submission_policy ON pas.underwriting_submission_clearance(policy_id);

-- Generic updated_at trigger for pas tables that carry updated_at (02 §§A1,A5,A6)
CREATE OR REPLACE FUNCTION pas.touch_updated_at() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at := now(); RETURN NEW; END; $$ LANGUAGE plpgsql;
CREATE TRIGGER trg_pas_policyholder_updated_at BEFORE UPDATE ON pas.policyholder_party_profile
    FOR EACH ROW EXECUTE FUNCTION pas.touch_updated_at();
CREATE TRIGGER trg_pas_producer_updated_at BEFORE UPDATE ON pas.producer_agency_contract
    FOR EACH ROW EXECUTE FUNCTION pas.touch_updated_at();
CREATE TRIGGER trg_pas_policy_updated_at BEFORE UPDATE ON pas.commercial_policy_master
    FOR EACH ROW EXECUTE FUNCTION pas.touch_updated_at();
