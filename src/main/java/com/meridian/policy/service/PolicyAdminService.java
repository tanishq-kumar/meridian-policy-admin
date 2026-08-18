package com.meridian.policy.service;

import com.meridian.policy.domain.*;
import com.meridian.policy.dto.*;
import com.meridian.policy.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PolicyAdminService {

    private final CommercialPolicyMasterRepository policyRepo;
    private final PolicyCoverageLineDetailRepository coverageRepo;
    private final InsuredRiskLocationScheduleRepository locationRepo;
    private final PolicyEndorsementTransactionRepository endorsementRepo;
    private final PremiumExposureRatingBasisRepository exposureRepo;
    private final PolicyholderPartyProfileRepository partyRepo;
    private final ProducerAgencyContractRepository producerRepo;

    private final AtomicInteger seq = new AtomicInteger(1);

    public PolicyAdminService(CommercialPolicyMasterRepository policyRepo,
                              PolicyCoverageLineDetailRepository coverageRepo,
                              InsuredRiskLocationScheduleRepository locationRepo,
                              PolicyEndorsementTransactionRepository endorsementRepo,
                              PremiumExposureRatingBasisRepository exposureRepo,
                              PolicyholderPartyProfileRepository partyRepo,
                              ProducerAgencyContractRepository producerRepo) {
        this.policyRepo = policyRepo;
        this.coverageRepo = coverageRepo;
        this.locationRepo = locationRepo;
        this.endorsementRepo = endorsementRepo;
        this.exposureRepo = exposureRepo;
        this.partyRepo = partyRepo;
        this.producerRepo = producerRepo;
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
        return e;
    }

    @Transactional
    public PolicyResponse cancelPolicy(String policyNumber, LocalDate cancellationDate, String reasonCode) {
        CommercialPolicyMaster p = policyRepo.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "policy not found"));
        if ("CANCELLED".equals(p.getPolicyStatusCode()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "already cancelled");
        p.setPolicyStatusCode("CANCELLED");
        p.setCancellationDate(cancellationDate != null ? cancellationDate : LocalDate.now());
        p.setCancellationReasonCode(reasonCode);
        p = policyRepo.save(p);
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

    private String nextPolicyNumber(String productCode) {
        int n = seq.getAndIncrement();
        String lob = switch (productCode) {
            case "CGL" -> "GL"; case "CPROP" -> "PROP"; case "WC" -> "WC"; case "CAUTO" -> "CAUTO"; default -> productCode;
        };
        return "MMG-%s-%d-%06d".formatted(lob, LocalDate.now().getYear(), n);
    }
}
