package com.meridian.policy.repo;

import com.meridian.policy.domain.ProducerAgencyContract;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProducerAgencyContractRepository extends JpaRepository<ProducerAgencyContract, Long> {
    Optional<ProducerAgencyContract> findByAgencyCode(String agencyCode);
}
