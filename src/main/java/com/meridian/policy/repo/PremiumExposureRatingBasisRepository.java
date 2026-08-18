package com.meridian.policy.repo;

import com.meridian.policy.domain.PremiumExposureRatingBasis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PremiumExposureRatingBasisRepository extends JpaRepository<PremiumExposureRatingBasis, Long> {
    List<PremiumExposureRatingBasis> findByCoverageLineId(Long coverageLineId);
}
