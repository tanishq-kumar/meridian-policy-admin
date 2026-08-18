package com.meridian.policy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEndorsementRequest(
        @NotBlank String endorsementTypeCode,
        @NotNull LocalDate endorsementEffectiveDate,
        @NotNull LocalDate endorsementProcessedDate,
        @NotNull BigDecimal premiumDeltaAmount,
        String initiatingSourceCode
) {}
