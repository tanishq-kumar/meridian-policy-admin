package com.meridian.policy.service;

import com.meridian.policy.domain.*;
import com.meridian.policy.dto.*;
import com.meridian.policy.events.EventPublisher;
import com.meridian.policy.repo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
public class PolicyAdminService {

    private final CommercialPolicyMasterRepository policyRepo;
    private final PolicyCoverageLineDetailRepository coverageRepo;
    private final InsuredRiskLocationScheduleRepository locationRepo;
    private final PolicyEndorsementTransactionRepository endorsementRepo;
    private final PremiumExposureRatingBasisRepository exposureRepo;
    private final PolicyholderPartyProfileRepository partyRepo;
    private final ProducerAgencyContractRepository producerRepo;
    private final NamedParameterJdbcTemplate jdbc;
    private final EventPublisher eventPublisher;

    @Value("${meridian.pii.key:change-me-in-prod}")
    private String piiKey;

    public PolicyAdminService(CommercialPolicyMasterRepository policyRepo,
                              PolicyCoverageLineDetailRepository coverageRepo,
                              InsuredRiskLocationScheduleRepository locationRepo,
                              PolicyEndorsementTransactionRepository endorsementRepo,
                              PremiumExposureRatingBasisRepository exposureRepo,
                              PolicyholderPartyProfileRepository partyRepo,
                              ProducerAgencyContractRepository producerRepo,
                              NamedParameterJdbcTemplate jdbc,
                              EventPublisher eventPublisher) {
        this.policyRepo = policyRepo;
        this.coverageRepo = coverageRepo;
        this.locationRepo = locationRepo;
        this.endorsementRepo = endorsementRepo;
        this.exposureRepo = exposureRepo;
        this.partyRepo = partyRepo;
        this.producerRepo = producerRepo;
        this.jdbc = jdbc;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PolicyResponse createPolicy(CreatePolicyRequest req) {
        if (!partyRepo.existsById(req.policyholderId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "policyholder not found: " + req.policyholderId());
        if (!producerRepo.existsById(req.producerId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "producer not found: " + req.producerId());
        if (req.policyTermExpirationDate().isBefore(req.policyTermEffectiveDate().plusDays(1)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "expiration must be after effective");

        String policyNumber = nextPolicyNumber(req.productCode());

        CommercialPolicyMaster p = new CommercialPolicyMaster();
        p.setPolicyNumber(policyNumber);
        p.setPolicyholderId(req.policyholderId());
        p.setProducerId(req.producerId());
        p.setProductCode(req.productCode());
        p.setUnderwritingCompanyCode(req.underwritingCompanyCode());
        p.setPolicyTermEffectiveDate(req.policyTermEffectiveDate());
        p.setPolicyTermExpirationDate(req.policyTermExpirationDate());
        p.setPolicyStatusCode("BOUND");
        p.setWrittenPremiumAmount(req.writtenPremiumAmount() != null ? req.writtenPremiumAmount() : BigDecimal.ZERO);
        p.setRenewalOfPolicyNumber(req.renewalOfPolicyNumber());
        p.setOriginalEffectiveDate(req.policyTermEffectiveDate());
        p = policyRepo.save(p);

        if (req.coverageLines() != null) {
            for (CreatePolicyRequest.CoverageLineRequest cl : req.coverageLines()) {
                PolicyCoverageLineDetail c = new PolicyCoverageLineDetail();
                c.setPolicyId(p.getPolicyId());
                c.setLineOfBusinessCode(cl.lineOfBusinessCode());
                c.setCoveragePartCode(cl.coveragePartCode());
                c.setLimitPerOccurrenceAmount(cl.limitPerOccurrenceAmount());
                c.setLimitAggregateAmount(cl.limitAggregateAmount());
                c.setDeductibleAmount(cl.deductibleAmount());
                c.setCoinsurancePct(cl.coinsurancePct());
                c.setPerilSetCode(cl.perilSetCode());
                c.setCoveragePremiumAmount(cl.coveragePremiumAmount());
                c.setCoverageEffectiveDate(cl.coverageEffectiveDate() != null ? cl.coverageEffectiveDate() : req.policyTermEffectiveDate());
                c.setCoverageExpirationDate(cl.coverageExpirationDate() != null ? cl.coverageExpirationDate() : req.policyTermExpirationDate());
                c = coverageRepo.save(c);
                if (cl.exposureBases() != null) {
                    for (CreatePolicyRequest.ExposureBasisRequest eb : cl.exposureBases()) {
                        PremiumExposureRatingBasis e = new PremiumExposureRatingBasis();
                        e.setCoverageLineId(c.getCoverageLineId());
                        e.setExposureTypeCode(eb.exposureTypeCode());
                        e.setExposureUnits(eb.exposureUnits());
                        e.setRatePerUnit(eb.ratePerUnit());
                        e.setBasisPeriodStartDate(eb.basisPeriodStartDate());
                        e.setBasisPeriodEndDate(eb.basisPeriodEndDate());
                        e.setAuditStatusCode(eb.auditStatusCode() != null ? eb.auditStatusCode() : "ESTIMATED");
                        exposureRepo.save(e);
                    }
                }
            }
        }
        if (req.riskLocations() != null) {
            int locSeq = 1;
            for (CreatePolicyRequest.RiskLocationRequest r : req.riskLocations()) {
                InsuredRiskLocationSchedule loc = new InsuredRiskLocationSchedule();
                loc.setPolicyId(p.getPolicyId());
                loc.setLocationSequenceNumber(r.locationSequenceNumber() != null ? r.locationSequenceNumber() : locSeq++);
                loc.setAddressLine1(r.addressLine1());
                loc.setCity(r.city());
                loc.setStateCode(r.stateCode());
                loc.setPostalCode(r.postalCode());
                loc.setCountyFips(r.countyFips());
                loc.setLatitude(r.latitude());
                loc.setLongitude(r.longitude());
                loc.setConstructionClassCode(r.constructionClassCode());
                loc.setOccupancyClassCode(r.occupancyClassCode());
                loc.setSprinkleredFlag(r.sprinkleredFlag());
                loc.setBuildingValueAmount(r.buildingValueAmount());
                loc.setContentsValueAmount(r.contentsValueAmount());
                loc.setBusinessIncomeValueAmount(r.businessIncomeValueAmount());
                loc.setAnnualReceiptsAmount(r.annualReceiptsAmount());
                loc.setCatastropheZoneCode(r.catastropheZoneCode());
                locationRepo.save(loc);
            }
        }
        eventPublisher.publish("pas.policy.events", "PolicyBoundEvent",
                Map.of("policy_number", p.getPolicyNumber(),
                        "policyholder_id", String.valueOf(p.getPolicyholderId()),
                        "producer_id", String.valueOf(p.getProducerId()),
                        "written_premium", p.getWrittenPremiumAmount().toString(),
                        "term_effective", p.getPolicyTermEffectiveDate().toString(),
                        "term_expiration", p.getPolicyTermExpirationDate().toString()));
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public PolicyResponse getByNumber(String policyNumber) {
        CommercialPolicyMaster p = policyRepo.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "policy not found"));
        return toResponse(p);
    }

    @Transactional
    public PolicyEndorsementTransaction addEndorsement(String policyNumber, CreateEndorsementRequest req) {
        CommercialPolicyMaster p = policyRepo.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "policy not found"));
        int nextSeq = endorsementRepo.findByPolicyIdOrderByEndorsementSequenceNumberAsc(p.getPolicyId())
                .stream().mapToInt(PolicyEndorsementTransaction::getEndorsementSequenceNumber).max().orElse(0) + 1;
        PolicyEndorsementTransaction e = new PolicyEndorsementTransaction();
        e.setPolicyId(p.getPolicyId());
        e.setEndorsementSequenceNumber(nextSeq);
        e.setEndorsementTypeCode(req.endorsementTypeCode());
        e.setEndorsementEffectiveDate(req.endorsementEffectiveDate());
        e.setEndorsementProcessedDate(req.endorsementProcessedDate());
        e.setPremiumDeltaAmount(req.premiumDeltaAmount());
        e.setInitiatingSourceCode(req.initiatingSourceCode());
        e = endorsementRepo.save(e);
        p.setWrittenPremiumAmount(p.getWrittenPremiumAmount().add(req.premiumDeltaAmount()));
        if (!"CANCELLED".equals(p.getPolicyStatusCode())) p.setPolicyStatusCode("ENDORSED");
        policyRepo.save(p);
        eventPublisher.publish("pas.policy.events", "EndorsementIssuedEvent",
                Map.of("policy_number", p.getPolicyNumber(),
                        "endorsement_seq", String.valueOf(e.getEndorsementSequenceNumber()),
                        "premium_delta", e.getPremiumDeltaAmount().toString()));
        return e;
    }

    @Transactional
    public PolicyResponse cancelPolicy(String policyNumber, String cancellationDateStr, String reasonCode) {
        CommercialPolicyMaster p = policyRepo.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "policy not found"));
        if ("CANCELLED".equals(p.getPolicyStatusCode()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "already cancelled");
        LocalDate cancellationDate = null;
        if (cancellationDateStr != null && !cancellationDateStr.isBlank()) {
            try {
                cancellationDate = LocalDate.parse(cancellationDateStr);
            } catch (DateTimeParseException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid cancellationDate: " + cancellationDateStr);
            }
        }
        p.setPolicyStatusCode("CANCELLED");
        p.setCancellationDate(cancellationDate != null ? cancellationDate : LocalDate.now());
        p.setCancellationReasonCode(reasonCode);
        p = policyRepo.save(p);
        eventPublisher.publish("pas.policy.events", "PolicyCancelledEvent",
                Map.of("policy_number", p.getPolicyNumber(),
                        "cancellation_date", p.getCancellationDate().toString(),
                        "reason_code", reasonCode != null ? reasonCode : ""));
        return toResponse(p);
    }

    private PolicyResponse toResponse(CommercialPolicyMaster p) {
        List<PolicyCoverageLineDetail> covs = coverageRepo.findByPolicyId(p.getPolicyId());
        List<InsuredRiskLocationSchedule> locs = locationRepo.findByPolicyId(p.getPolicyId());
        return new PolicyResponse(
                p.getPolicyId(), p.getPolicyNumber(), p.getPolicyholderId(), p.getProducerId(),
                p.getProductCode(), p.getUnderwritingCompanyCode(),
                p.getPolicyTermEffectiveDate(), p.getPolicyTermExpirationDate(),
                p.getPolicyStatusCode(), p.getWrittenPremiumAmount(),
                p.getRenewalOfPolicyNumber(), p.getCancellationDate(), p.getCancellationReasonCode(),
                covs.stream().map(c -> new PolicyResponse.CoverageLineResponse(
                        c.getCoverageLineId(), c.getLineOfBusinessCode(), c.getCoveragePartCode(),
                        c.getLimitPerOccurrenceAmount(), c.getLimitAggregateAmount(),
                        c.getDeductibleAmount(), c.getCoinsurancePct(), c.getPerilSetCode(),
                        c.getCoveragePremiumAmount())).toList(),
                locs.stream().map(l -> new PolicyResponse.RiskLocationResponse(
                        l.getRiskLocationId(), l.getLocationSequenceNumber(),
                        l.getAddressLine1(), l.getCity(), l.getStateCode(), l.getPostalCode(),
                        l.getCountyFips(), l.getConstructionClassCode(), l.getCatastropheZoneCode(),
                        l.getBuildingValueAmount(), l.getContentsValueAmount(), l.getBusinessIncomeValueAmount())).toList()
        );
    }

    private synchronized String nextPolicyNumber(String productCode) {
        String lob = switch (productCode) {
            case "CGL" -> "GL"; case "CPROP" -> "PROP"; case "WC" -> "WC"; case "CAUTO" -> "CAUTO"; default -> productCode;
        };
        int year = LocalDate.now().getYear();
        String prefix = "MMG-%s-%d-".formatted(lob, year);
        String likePattern = prefix + "%";
        List<Map<String, Object>> rows = jdbc.queryForList(
                """
                SELECT policy_number FROM pas.commercial_policy_master
                WHERE policy_number LIKE :pattern
                ORDER BY policy_number DESC LIMIT 1
                """,
                Map.of("pattern", likePattern));
        int nextSeq = 1;
        if (!rows.isEmpty()) {
            String maxNum = (String) rows.get(0).get("policy_number");
            String seqStr = maxNum.substring(prefix.length());
            try { nextSeq = Integer.parseInt(seqStr) + 1; } catch (NumberFormatException ignored) {}
        }
        String candidate = "MMG-%s-%d-%06d".formatted(lob, year, nextSeq);
        // retry-on-conflict loop for concurrent inserts
        for (int attempt = 0; attempt < 3; attempt++) {
            if (!policyRepo.existsByPolicyNumber(candidate)) return candidate;
            nextSeq++;
            candidate = "MMG-%s-%d-%06d".formatted(lob, year, nextSeq);
        }
        return candidate;
    }

    /** Encrypt FEIN at write time via pgcrypto; call after saving party with placeholder. */
    public void encryptFein(Long policyholderId, String plainFein) {
        if (plainFein == null || plainFein.isBlank()) return;
        jdbc.update(
                """
                UPDATE pas.policyholder_party_profile
                SET fein_encrypted = pgp_sym_encrypt(:plain, :key)
                WHERE policyholder_id = :id
                """,
                Map.of("plain", plainFein, "key", piiKey, "id", policyholderId));
    }
}
