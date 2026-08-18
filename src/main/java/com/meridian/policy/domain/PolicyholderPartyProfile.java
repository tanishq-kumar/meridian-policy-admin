package com.meridian.policy.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "policyholder_party_profile", schema = "pas")
public class PolicyholderPartyProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policyholder_id") private Long policyholderId;
    @Column(name = "legal_name", nullable = false, length = 200) private String legalName;
    @Column(name = "dba_name", length = 200) private String dbaName;
    @Column(name = "entity_type_code", nullable = false, length = 20) private String entityTypeCode;
    @Column(name = "fein_encrypted") private byte[] feinEncrypted;
    @Column(name = "naics_code", length = 6) private String naicsCode;
    @Column(name = "sic_code", length = 4) private String sicCode;
    @Column(name = "mailing_address_line_1", length = 200) private String mailingAddressLine1;
    @Column(name = "mailing_city", length = 100) private String mailingCity;
    @Column(name = "mailing_state_code", length = 2) private String mailingStateCode;
    @Column(name = "mailing_postal_code", length = 10) private String mailingPostalCode;
    @Column(name = "primary_contact_name", length = 200) private String primaryContactName;
    @Column(name = "primary_contact_email", length = 200) private String primaryContactEmail;
    @Column(name = "primary_contact_phone", length = 30) private String primaryContactPhone;
    @Column(name = "year_established") private Integer yearEstablished;
    @Column(name = "employee_count") private Integer employeeCount;
    @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;

    @PrePersist void prePersist() { createdAt = updatedAt = OffsetDateTime.now(); }
    @PreUpdate  void preUpdate()  { updatedAt = OffsetDateTime.now(); }

    public Long getPolicyholderId() { return policyholderId; }
    public void setPolicyholderId(Long v) { policyholderId = v; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String v) { legalName = v; }
    public String getDbaName() { return dbaName; }
    public void setDbaName(String v) { dbaName = v; }
    public String getEntityTypeCode() { return entityTypeCode; }
    public void setEntityTypeCode(String v) { entityTypeCode = v; }
    public byte[] getFeinEncrypted() { return feinEncrypted; }
    public void setFeinEncrypted(byte[] v) { feinEncrypted = v; }
    public String getNaicsCode() { return naicsCode; }
    public void setNaicsCode(String v) { naicsCode = v; }
    public String getSicCode() { return sicCode; }
    public void setSicCode(String v) { sicCode = v; }
    public String getMailingAddressLine1() { return mailingAddressLine1; }
    public void setMailingAddressLine1(String v) { mailingAddressLine1 = v; }
    public String getMailingCity() { return mailingCity; }
    public void setMailingCity(String v) { mailingCity = v; }
    public String getMailingStateCode() { return mailingStateCode; }
    public void setMailingStateCode(String v) { mailingStateCode = v; }
    public String getMailingPostalCode() { return mailingPostalCode; }
    public void setMailingPostalCode(String v) { mailingPostalCode = v; }
    public String getPrimaryContactName() { return primaryContactName; }
    public void setPrimaryContactName(String v) { primaryContactName = v; }
    public String getPrimaryContactEmail() { return primaryContactEmail; }
    public void setPrimaryContactEmail(String v) { primaryContactEmail = v; }
    public String getPrimaryContactPhone() { return primaryContactPhone; }
    public void setPrimaryContactPhone(String v) { primaryContactPhone = v; }
    public Integer getYearEstablished() { return yearEstablished; }
    public void setYearEstablished(Integer v) { yearEstablished = v; }
    public Integer getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Integer v) { employeeCount = v; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
