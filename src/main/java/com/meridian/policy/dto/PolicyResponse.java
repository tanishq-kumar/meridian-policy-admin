package com.meridian.policy.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PolicyResponse(
        Long policyId, String policyNumber, Long policyholderId, Long producerId,
        String productCode, String underwritingCompanyCode,
        LocalDate policyTermEffectiveDate, LocalDate policyTermExpirationDate,
        String policyStatusCode, BigDecimal writtenPremiumAmount,
        String renewalOfPolicyNumber, LocalDate cancellationDate, String cancellationReasonCode,
        List<CoverageLineResponse> coverageLines,
        List<RiskLocationResponse> riskLocations
) {
    public record CoverageLineResponse(
            Long coverageLineId, String lineOfBusinessCode, String coveragePartCode,
            BigDecimal limitPerOccurrenceAmount, BigDecimal limitAggregateAmount,
            BigDecimal deductibleAmount, BigDecimal coinsurancePct, String perilSetCode,
            BigDecimal coveragePremiumAmount) {}
    public record RiskLocationResponse(
            Long riskLocationId, Integer locationSequenceNumber,
            String addressLine1, String city, String stateCode, String postalCode,
            String countyFips, String constructionClassCode, String catastropheZoneCode,
            BigDecimal buildingValueAmount, BigDecimal contentsValueAmount, BigDecimal businessIncomeValueAmount) {}
}
