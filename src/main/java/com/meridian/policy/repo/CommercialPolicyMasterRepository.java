package com.meridian.policy.repo;

import com.meridian.policy.domain.CommercialPolicyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommercialPolicyMasterRepository extends JpaRepository<CommercialPolicyMaster, Long> {
    Optional<CommercialPolicyMaster> findByPolicyNumber(String policyNumber);
    boolean existsByPolicyNumber(String policyNumber);
}
