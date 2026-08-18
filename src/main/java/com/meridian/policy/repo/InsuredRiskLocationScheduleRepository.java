package com.meridian.policy.repo;

import com.meridian.policy.domain.InsuredRiskLocationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InsuredRiskLocationScheduleRepository extends JpaRepository<InsuredRiskLocationSchedule, Long> {
    List<InsuredRiskLocationSchedule> findByPolicyId(Long policyId);
}
