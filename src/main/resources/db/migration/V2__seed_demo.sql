-- V2__seed_demo.sql — minimal realistic demo data for J1 verification (Tampa restaurant package).
INSERT INTO pas.policyholder_party_profile (legal_name, dba_name, entity_type_code, naics_code, mailing_city, mailing_state_code, mailing_postal_code, primary_contact_name, primary_contact_email, year_established, employee_count)
VALUES ('Sunshine Hospitality Group LLC','Sunshine Hospitality','LLC','722511','Tampa','FL','33602','Ana Rivera','ana@sunshine.example',2014,85)
ON CONFLICT DO NOTHING;

INSERT INTO pas.producer_agency_contract (agency_code, agency_name, producer_type_code, license_state_code, contracted_commission_pct, contingent_commission_eligible_flag, appointment_effective_date)
VALUES ('BAY-TPA-001','Bayview Independent Agency','RETAIL_AGENT','FL',10.00,true, DATE '2024-01-01')
ON CONFLICT (agency_code) DO NOTHING;
