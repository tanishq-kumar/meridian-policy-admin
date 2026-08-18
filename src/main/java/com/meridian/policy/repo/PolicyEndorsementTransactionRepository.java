package com.meridian.policy.repo;

import com.meridian.policy.domain.PolicyEndorsementTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PolicyEndorsementTransactionRepository extends JpaRepository<PolicyEndorsementTransaction, Long> {
    List<PolicyEndorsementTransaction> findByPolicyIdOrderByEndorsementSequenceNumberAsc(Long policyId);
}
