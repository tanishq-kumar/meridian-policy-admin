package com.meridian.policy.repo;

import com.meridian.policy.domain.PolicyCoverageLineDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PolicyCoverageLineDetailRepository extends JpaRepository<PolicyCoverageLineDetail, Long> {
    List<PolicyCoverageLineDetail> findByPolicyId(Long policyId);
}
