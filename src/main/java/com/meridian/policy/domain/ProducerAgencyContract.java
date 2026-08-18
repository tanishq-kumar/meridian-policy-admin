package com.meridian.policy.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "producer_agency_contract", schema = "pas")
public class ProducerAgencyContract {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producer_id") private Long producerId;
    @Column(name = "agency_code", nullable = false, unique = true, length = 12) private String agencyCode;
    @Column(name = "agency_name", nullable = false, length = 200) private String agencyName;
    @Column(name = "dba_name", length = 200) private String dbaName;
    @Column(name = "producer_type_code", nullable = false, length = 20) private String producerTypeCode;
    @Column(name = "license_number", length = 40) private String licenseNumber;
    @Column(name = "license_state_code", length = 2) private String licenseStateCode;
    @Column(name = "license_expiration_date") private LocalDate licenseExpirationDate;
    @Column(name = "contracted_commission_pct", precision = 5, scale = 2) private BigDecimal contractedCommissionPct;
    @Column(name = "contingent_commission_eligible_flag", nullable = false) private Boolean contingentCommissionEligibleFlag = false;
    @Column(name = "binding_authority_limit_amount", precision = 14, scale = 2) private BigDecimal bindingAuthorityLimitAmount;
    @Column(name = "appointment_effective_date") private LocalDate appointmentEffectiveDate;
    @Column(name = "appointment_termination_date") private LocalDate appointmentTerminationDate;
    @Column(name = "parent_producer_id") private Long parentProducerId;
    @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;

    @PrePersist void prePersist() { createdAt = updatedAt = OffsetDateTime.now(); }
    @PreUpdate  void preUpdate()  { updatedAt = OffsetDateTime.now(); }

    public Long getProducerId() { return producerId; }
    public void setProducerId(Long v) { producerId = v; }
    public String getAgencyCode() { return agencyCode; }
    public void setAgencyCode(String v) { agencyCode = v; }
    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String v) { agencyName = v; }
    public String getDbaName() { return dbaName; }
    public void setDbaName(String v) { dbaName = v; }
    public String getProducerTypeCode() { return producerTypeCode; }
    public void setProducerTypeCode(String v) { producerTypeCode = v; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String v) { licenseNumber = v; }
    public String getLicenseStateCode() { return licenseStateCode; }
    public void setLicenseStateCode(String v) { licenseStateCode = v; }
    public LocalDate getLicenseExpirationDate() { return licenseExpirationDate; }
    public void setLicenseExpirationDate(LocalDate v) { licenseExpirationDate = v; }
    public BigDecimal getContractedCommissionPct() { return contractedCommissionPct; }
    public void setContractedCommissionPct(BigDecimal v) { contractedCommissionPct = v; }
    public Boolean getContingentCommissionEligibleFlag() { return contingentCommissionEligibleFlag; }
    public void setContingentCommissionEligibleFlag(Boolean v) { contingentCommissionEligibleFlag = v; }
    public BigDecimal getBindingAuthorityLimitAmount() { return bindingAuthorityLimitAmount; }
    public void setBindingAuthorityLimitAmount(BigDecimal v) { bindingAuthorityLimitAmount = v; }
    public LocalDate getAppointmentEffectiveDate() { return appointmentEffectiveDate; }
    public void setAppointmentEffectiveDate(LocalDate v) { appointmentEffectiveDate = v; }
    public LocalDate getAppointmentTerminationDate() { return appointmentTerminationDate; }
    public void setAppointmentTerminationDate(LocalDate v) { appointmentTerminationDate = v; }
    public Long getParentProducerId() { return parentProducerId; }
    public void setParentProducerId(Long v) { parentProducerId = v; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
