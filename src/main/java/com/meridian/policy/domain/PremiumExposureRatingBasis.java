package com.meridian.policy.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "premium_exposure_rating_basis", schema = "pas")
public class PremiumExposureRatingBasis {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exposure_basis_id") private Long exposureBasisId;
    @Column(name = "coverage_line_id") private Long coverageLineId;
    @Column(name = "exposure_type_code", nullable = false, length = 20) private String exposureTypeCode;
    @Column(name = "exposure_units", nullable = false, precision = 14, scale = 2) private BigDecimal exposureUnits;
    @Column(name = "rate_per_unit", nullable = false, precision = 10, scale = 4) private BigDecimal ratePerUnit;
    @Column(name = "basis_period_start_date") private LocalDate basisPeriodStartDate;
    @Column(name = "basis_period_end_date") private LocalDate basisPeriodEndDate;
    @Column(name = "audit_status_code", nullable = false, length = 20) private String auditStatusCode = "ESTIMATED";

    public Long getExposureBasisId() { return exposureBasisId; }
    public void setExposureBasisId(Long v) { exposureBasisId = v; }
    public Long getCoverageLineId() { return coverageLineId; }
    public void setCoverageLineId(Long v) { coverageLineId = v; }
    public String getExposureTypeCode() { return exposureTypeCode; }
    public void setExposureTypeCode(String v) { exposureTypeCode = v; }
    public BigDecimal getExposureUnits() { return exposureUnits; }
    public void setExposureUnits(BigDecimal v) { exposureUnits = v; }
    public BigDecimal getRatePerUnit() { return ratePerUnit; }
    public void setRatePerUnit(BigDecimal v) { ratePerUnit = v; }
    public LocalDate getBasisPeriodStartDate() { return basisPeriodStartDate; }
    public void setBasisPeriodStartDate(LocalDate v) { basisPeriodStartDate = v; }
    public LocalDate getBasisPeriodEndDate() { return basisPeriodEndDate; }
    public void setBasisPeriodEndDate(LocalDate v) { basisPeriodEndDate = v; }
    public String getAuditStatusCode() { return auditStatusCode; }
    public void setAuditStatusCode(String v) { auditStatusCode = v; }
}
