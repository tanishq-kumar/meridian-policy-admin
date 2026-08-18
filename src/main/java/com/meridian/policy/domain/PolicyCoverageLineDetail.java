package com.meridian.policy.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "policy_coverage_line_detail", schema = "pas")
public class PolicyCoverageLineDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coverage_line_id") private Long coverageLineId;
    @Column(name = "policy_id", nullable = false) private Long policyId;
    @Column(name = "line_of_business_code", nullable = false, length = 10) private String lineOfBusinessCode;
    @Column(name = "coverage_part_code", nullable = false, length = 20) private String coveragePartCode;
    @Column(name = "limit_per_occurrence_amount", precision = 14, scale = 2) private BigDecimal limitPerOccurrenceAmount;
    @Column(name = "limit_aggregate_amount", precision = 14, scale = 2) private BigDecimal limitAggregateAmount;
    @Column(name = "deductible_amount", precision = 14, scale = 2) private BigDecimal deductibleAmount;
    @Column(name = "coinsurance_pct", precision = 5, scale = 2) private BigDecimal coinsurancePct;
    @Column(name = "peril_set_code", length = 20) private String perilSetCode;
    @Column(name = "coverage_premium_amount", nullable = false, precision = 14, scale = 2) private BigDecimal coveragePremiumAmount = BigDecimal.ZERO;
    @Column(name = "coverage_effective_date") private LocalDate coverageEffectiveDate;
    @Column(name = "coverage_expiration_date") private LocalDate coverageExpirationDate;

    public Long getCoverageLineId() { return coverageLineId; }
    public void setCoverageLineId(Long v) { coverageLineId = v; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long v) { policyId = v; }
    public String getLineOfBusinessCode() { return lineOfBusinessCode; }
    public void setLineOfBusinessCode(String v) { lineOfBusinessCode = v; }
    public String getCoveragePartCode() { return coveragePartCode; }
    public void setCoveragePartCode(String v) { coveragePartCode = v; }
    public BigDecimal getLimitPerOccurrenceAmount() { return limitPerOccurrenceAmount; }
    public void setLimitPerOccurrenceAmount(BigDecimal v) { limitPerOccurrenceAmount = v; }
    public BigDecimal getLimitAggregateAmount() { return limitAggregateAmount; }
    public void setLimitAggregateAmount(BigDecimal v) { limitAggregateAmount = v; }
    public BigDecimal getDeductibleAmount() { return deductibleAmount; }
    public void setDeductibleAmount(BigDecimal v) { deductibleAmount = v; }
    public BigDecimal getCoinsurancePct() { return coinsurancePct; }
    public void setCoinsurancePct(BigDecimal v) { coinsurancePct = v; }
    public String getPerilSetCode() { return perilSetCode; }
    public void setPerilSetCode(String v) { perilSetCode = v; }
    public BigDecimal getCoveragePremiumAmount() { return coveragePremiumAmount; }
    public void setCoveragePremiumAmount(BigDecimal v) { coveragePremiumAmount = v; }
    public LocalDate getCoverageEffectiveDate() { return coverageEffectiveDate; }
    public void setCoverageEffectiveDate(LocalDate v) { coverageEffectiveDate = v; }
    public LocalDate getCoverageExpirationDate() { return coverageExpirationDate; }
    public void setCoverageExpirationDate(LocalDate v) { coverageExpirationDate = v; }
}
