package com.meridian.policy.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "underwriting_submission_clearance", schema = "pas")
public class UnderwritingSubmissionClearance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id") private Long submissionId;
    @Column(name = "policy_id") private Long policyId;
    @Column(name = "submission_received_date", nullable = false) private LocalDate submissionReceivedDate;
    @Column(name = "underwriter_id", length = 40) private String underwriterId;
    @Column(name = "clearance_status_code", nullable = false, length = 20) private String clearanceStatusCode;
    @Column(name = "referral_reason_code", length = 40) private String referralReasonCode;
    @Column(name = "authority_level_code", length = 20) private String authorityLevelCode;
    @Column(name = "decision_date") private LocalDate decisionDate;
    @Column(name = "bound_flag", nullable = false) private Boolean boundFlag = false;

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long v) { submissionId = v; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long v) { policyId = v; }
    public LocalDate getSubmissionReceivedDate() { return submissionReceivedDate; }
    public void setSubmissionReceivedDate(LocalDate v) { submissionReceivedDate = v; }
    public String getUnderwriterId() { return underwriterId; }
    public void setUnderwriterId(String v) { underwriterId = v; }
    public String getClearanceStatusCode() { return clearanceStatusCode; }
    public void setClearanceStatusCode(String v) { clearanceStatusCode = v; }
    public String getReferralReasonCode() { return referralReasonCode; }
    public void setReferralReasonCode(String v) { referralReasonCode = v; }
    public String getAuthorityLevelCode() { return authorityLevelCode; }
    public void setAuthorityLevelCode(String v) { authorityLevelCode = v; }
    public LocalDate getDecisionDate() { return decisionDate; }
    public void setDecisionDate(LocalDate v) { decisionDate = v; }
    public Boolean getBoundFlag() { return boundFlag; }
    public void setBoundFlag(Boolean v) { boundFlag = v; }
}
