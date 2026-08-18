package com.meridian.policy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreatePolicyRequest(
        @NotNull Long policyholderId,
        @NotNull Long producerId,
        @NotBlank String productCode,
        @NotBlank String underwritingCompanyCode,
        @NotNull LocalDate policyTermEffectiveDate,
        @NotNull LocalDate policyTermExpirationDate,
        BigDecimal writtenPremiumAmount,
        String renewalOfPolicyNumber,
        List<CoverageLineRequest> coverageLines,
        List<RiskLocationRequest> riskLocations
) {
    public record CoverageLineRequest(
            @NotBlank String lineOfBusinessCode,
            @NotBlank String coveragePartCode,
            BigDecimal limitPerOccurrenceAmount,
            BigDecimal limitAggregateAmount,
            BigDecimal deductibleAmount,
            BigDecimal coinsurancePct,
            String perilSetCode,
            @NotNull BigDecimal coveragePremiumAmount,
            LocalDate coverageEffectiveDate,
            LocalDate coverageExpirationDate,
            List<ExposureBasisRequest> exposureBases
    ) {}
    public record ExposureBasisRequest(
            @NotBlank String exposureTypeCode,
            @NotNull BigDecimal exposureUnits,
            @NotNull BigDecimal ratePerUnit,
            LocalDate basisPeriodStartDate,
            LocalDate basisPeriodEndDate,
            String auditStatusCode
    ) {}
    public record RiskLocationRequest(
            Integer locationSequenceNumber,
            String addressLine1, String city, String stateCode, String postalCode, String countyFips,
            BigDecimal latitude, BigDecimal longitude,
            String constructionClassCode, String occupancyClassCode, Boolean sprinkleredFlag,
            BigDecimal buildingValueAmount, BigDecimal contentsValueAmount,
            BigDecimal businessIncomeValueAmount, BigDecimal annualReceiptsAmount,
            String catastropheZoneCode
    ) {}
}
