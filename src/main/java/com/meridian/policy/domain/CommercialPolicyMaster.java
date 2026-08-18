package com.meridian.policy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "commercial_policy_master", schema = "pas",
       uniqueConstraints = @UniqueConstraint(columnNames = "policy_number"))
public class CommercialPolicyMaster {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id") private Long policyId;

    @Column(name = "policy_number", nullable = false, unique = true, length = 20) private String policyNumber;
    @NotNull @Column(name = "policyholder_id", nullable = false) private Long policyholderId;
    @NotNull @Column(name = "producer_id", nullable = false) private Long producerId;
    @Column(name = "product_code", nullable = false, length = 10) private String productCode;
    @Column(name = "underwriting_company_code", nullable = false, length = 5) private String underwritingCompanyCode;
    @Column(name = "policy_term_effective_date", nullable = false) private LocalDate policyTermEffectiveDate;
    @Column(name = "policy_term_expiration_date", nullable = false) private LocalDate policyTermExpirationDate;
    @Column(name = "policy_status_code", nullable = false, length = 15) private String policyStatusCode;
    @Column(name = "written_premium_amount", nullable = false, precision = 14, scale = 2) private BigDecimal writtenPremiumAmount = BigDecimal.ZERO;
    @Column(name = "original_effective_date") private LocalDate originalEffectiveDate;
    @Column(name = "renewal_of_policy_number", length = 20) private String renewalOfPolicyNumber;
    @Column(name = "cancellation_date") private LocalDate cancellationDate;
    @Column(name = "cancellation_reason_code", length = 20) private String cancellationReasonCode;
    @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;

    @PrePersist void prePersist() { createdAt = updatedAt = OffsetDateTime.now(); }
    @PreUpdate  void preUpdate()  { updatedAt = OffsetDateTime.now(); }

    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long v) { policyId = v; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String v) { policyNumber = v; }
    public Long getPolicyholderId() { return policyholderId; }
    public void setPolicyholderId(Long v) { policyholderId = v; }
    public Long getProducerId() { return producerId; }
    public void setProducerId(Long v) { producerId = v; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String v) { productCode = v; }
    public String getUnderwritingCompanyCode() { return underwritingCompanyCode; }
    public void setUnderwritingCompanyCode(String v) { underwritingCompanyCode = v; }
    public LocalDate getPolicyTermEffectiveDate() { return policyTermEffectiveDate; }
    public void setPolicyTermEffectiveDate(LocalDate v) { policyTermEffectiveDate = v; }
    public LocalDate getPolicyTermExpirationDate() { return policyTermExpirationDate; }
    public void setPolicyTermExpirationDate(LocalDate v) { policyTermExpirationDate = v; }
    public String getPolicyStatusCode() { return policyStatusCode; }
    public void setPolicyStatusCode(String v) { policyStatusCode = v; }
    public BigDecimal getWrittenPremiumAmount() { return writtenPremiumAmount; }
    public void setWrittenPremiumAmount(BigDecimal v) { writtenPremiumAmount = v; }
    public LocalDate getOriginalEffectiveDate() { return originalEffectiveDate; }
    public void setOriginalEffectiveDate(LocalDate v) { originalEffectiveDate = v; }
    public String getRenewalOfPolicyNumber() { return renewalOfPolicyNumber; }
    public void setRenewalOfPolicyNumber(String v) { renewalOfPolicyNumber = v; }
    public LocalDate getCancellationDate() { return cancellationDate; }
    public void setCancellationDate(LocalDate v) { cancellationDate = v; }
    public String getCancellationReasonCode() { return cancellationReasonCode; }
    public void setCancellationReasonCode(String v) { cancellationReasonCode = v; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
