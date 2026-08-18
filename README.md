# meridian-policy-admin

**Owner: Product & Underwriting · Schema `pas` · Design → `../docs/`**

System of record for the full policy lifecycle at Meridian Mutual — submission clearance, quote, bind, mid-term endorsement, renewal, cancellation/reinstatement. This is the origin of every policy number, insured party, producer relationship, and line-level premium in the platform.

## Role in the platform

- **Produces** working policy and coverage truth for the rest of the platform (01 §1).
- **Downstream consumers:** claims (coverage read-model via events), billing (installment plans), reinsurance (cession allocation), warehouse (premium analytics via CDC→`dwh.fact_written_premium_transaction`).
- **Consumes** `InvoiceGeneratedEvent` (to start statutory non-pay cancellation) and `CoverageVerificationRequest` (authoritative coverage reply at FNOL) per 01, 03.

## Tables owned — 8 (schema `pas`) — specs in 02

| Table | Grain |
|---|---|
| `commercial_policy_master` | one row per policy **term** (`MMG-{LOB}-{YYYY}-{seq}`) |
| `policy_coverage_line_detail` | one row per policy × coverage line (line limits/deductibles map) |
| `insured_risk_location_schedule` | one row per scheduled location (COC/occupancy/TIV; joins `dim_geographic_risk_territory`) |
| `policy_endorsement_transaction` | one row per mid-term endorsement (retro-effective — see 09 §1/§6) |
| `policyholder_party_profile` | one row per named insured entity — **PII** (`fein_encrypted`, contact); source for C7→C2 |
| `producer_agency_contract` | one row per appointed producer contract with `parent_producer_id` hierarchy; drives `dim_producer_organization` and C8 |
| `premium_exposure_rating_basis` | one row per coverage line × exposure basis (`PAYROLL`/`GROSS_RECEIPTS`/…; `AUDITED` arrives 12–15 mo. post-expiry) |
| `underwriting_submission_clearance` | one row per UW submission (`CLEAR`/`REFERRED`/`DECLINED` — funnel analytics) |

`commercial_policy_master.written_premium_amount` stays the policy-level total; Σ `policy_coverage_line_detail.coverage_premium_amount` reconciles to it (DQ-03, 06). `policy_endorsement_transaction.premium_delta_amount` may be negative (`REMOVE_LOCATION` etc.).

## Cross-repo contracts (hybrid — 00)

| Kafka topic | Events published/consumed | Doc |
|---|---|---|
| `pas.policy.events` (Avro) | `PolicyBoundEvent`, `EndorsementIssuedEvent`, `PolicyCancelledEvent`/`PolicyReinstatedEvent`, `PolicyRenewedEvent` | 01 §1 |
| same | `CoverageVerificationRequest` → `CoverageVerificationReply` (3 s timeout; claims uses its `claim_coverage_verification_snapshot` fallback) | 01 §1–2, 03 J1–J2 |

Value references (`policy_number`) only — no foreign keys across repos (01 §Anti-corruption). Debezium CDC on this schema feeds `stg.stg_policy_transaction_raw` (land→S1–S6); warehouse verifications in 06. PII masking/encryption in 07.

## Journeys it participates in

- **J1 — Policy bind → premium analytics:** bound row → `PolicyBoundEvent` → reinsurance (QS-2026-PROP-01 40% etc.) + billing (10-pay) + claims read-model + CDC land → facts → marts (`mart_producer_book_performance`, `mart_statutory_annual_statement_line`, `mart_cat_exposure_accumulation`) → R1/R4/R5/R8 (03).
- **J2 — FNOL coverage proof:** claims' `claim_coverage_verification_snapshot` carries PAS coverage frozen at verification time with `kafka_message_id` correlation (03).

## Transformations & reports

Standard S1/S2/S3/S5/S6 on the CDC→land typed projection; warehouse side C1 (pro-rata earned, UPR), C2 (SCD Type 2 on `dim_insured_party_scd`), C3 (signed `BIND`/`ENDORSEMENT`/`CANCELLATION` net written), C7 (party golden-record), C8 (sliding-scale commission source rate), C9 (CAT accumulation per `catastrophe_zone_code`) — rules and SQL in 04. Marts feed R1 premium bordereaux / R4 book-of-business / R5 statutory / R8 CAT extracts (05).

## Build & stack

Java 21 · Spring Boot 3.x · Maven · PostgreSQL 16 · Flyway (`db/migration/pas`). Stack shared across all six repos — see 00 §Technology stack.

```
meridian-policy-admin/
  src/main/java/  — this repo's service
  src/main/resources/db/migration/  — Flyway: schema pas
  ../docs/        — platform design spec (read first)
```

Design owned in `../docs/`; this folder is the operational-side repo shell under the parent git.
