package com.aia.voiceinsights.policy.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One policy the customer holds. {@code category} is a closed vocabulary shared with the recommendation agents so they can
 * compare cover by kind, not just by product name: LIFE_TERM, LIFE_WHOLE, CRITICAL_ILLNESS, HOSPITALISATION, INVESTMENT_LINKED,
 * ENDOWMENT, PERSONAL_ACCIDENT, ANNUITY. {@code productCode} matches the product catalogue the agents search, when it is an AIA product.
 */
public record Policy(
        String policyNumber,
        String productCode,
        String productName,
        String category,
        String status,          // IN_FORCE | LAPSED | SURRENDERED | MATURED | CLAIMED
        BigDecimal sumAssured,
        BigDecimal annualPremium,
        LocalDate startDate,
        String insurer          // "AIA" or another insurer, when the system knows
) {}
