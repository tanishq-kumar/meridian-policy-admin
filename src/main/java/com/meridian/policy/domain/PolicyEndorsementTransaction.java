package com.meridian.policy.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "policy_endorsement_transaction", schema = "pas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"policy_id","endorsement_sequence_number"}))
public class PolicyEndorsementTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "endorsement_id") private Long endorsementId;
    @Column(name = "policy_id", nullable = false) private Long policyId;
    @Column(name = "endorsement_sequence_number", nullable = false) private Integer endorsementSequenceNumber;
    @Column(name = "endorsement_type_code", nullable = false, length = 20) private String endorsementTypeCode;
    @Column(name = "endorsement_effective_date", nullable = false) private LocalDate endorsementEffectiveDate;
    @Column(name = "endorsement_processed_date", nullable = false) private LocalDate endorsementProcessedDate;
    @Column(name = "premium_delta_amount", nullable = false, precision = 14, scale = 2) private BigDecimal premiumDeltaAmount = BigDecimal.ZERO;
    @Column(name = "initiating_source_code", length = 20) private String initiatingSourceCode;
    @Column(name = "transaction_timestamp", nullable = false) private OffsetDateTime transactionTimestamp;

    @PrePersist void prePersist() { if (transactionTimestamp == null) transactionTimestamp = OffsetDateTime.now(); }

    public Long getEndorsementId() { return endorsementId; }
    public void setEndorsementId(Long v) { endorsementId = v; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long v) { policyId = v; }
    public Integer getEndorsementSequenceNumber() { return endorsementSequenceNumber; }
    public void setEndorsementSequenceNumber(Integer v) { endorsementSequenceNumber = v; }
    public String getEndorsementTypeCode() { return endorsementTypeCode; }
    public void setEndorsementTypeCode(String v) { endorsementTypeCode = v; }
    public LocalDate getEndorsementEffectiveDate() { return endorsementEffectiveDate; }
    public void setEndorsementEffectiveDate(LocalDate v) { endorsementEffectiveDate = v; }
    public LocalDate getEndorsementProcessedDate() { return endorsementProcessedDate; }
    public void setEndorsementProcessedDate(LocalDate v) { endorsementProcessedDate = v; }
    public BigDecimal getPremiumDeltaAmount() { return premiumDeltaAmount; }
    public void setPremiumDeltaAmount(BigDecimal v) { premiumDeltaAmount = v; }
    public String getInitiatingSourceCode() { return initiatingSourceCode; }
    public void setInitiatingSourceCode(String v) { initiatingSourceCode = v; }
    public OffsetDateTime getTransactionTimestamp() { return transactionTimestamp; }
    public void setTransactionTimestamp(OffsetDateTime v) { transactionTimestamp = v; }
}
