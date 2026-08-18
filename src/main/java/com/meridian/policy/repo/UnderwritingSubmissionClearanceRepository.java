package com.meridian.policy.repo;

import com.meridian.policy.domain.UnderwritingSubmissionClearance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnderwritingSubmissionClearanceRepository extends JpaRepository<UnderwritingSubmissionClearance, Long> {
    List<UnderwritingSubmissionClearance> findByPolicyId(Long policyId);
}
