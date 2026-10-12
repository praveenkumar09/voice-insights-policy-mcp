package com.aia.voiceinsights.policy.model;

import java.time.Instant;
import java.util.List;

/**
 * What the tool returns. {@code status} is never inferred: FOUND (one or more policies), NONE (the system answered: no policies),
 * UNAVAILABLE (the system could not be asked: the caller must treat coverage as UNKNOWN, never as "none").
 * The NRIC is returned masked only.
 */
public record PolicyLookupResult(
        String status,
        String nricMasked,
        List<Policy> policies,
        int inForceCount,
        String source,
        Instant fetchedAt,
        String message
) {
    public static PolicyLookupResult found(String masked, List<Policy> policies, String source) {
        long inForce = policies.stream().filter(p -> "IN_FORCE".equals(p.status())).count();
        return new PolicyLookupResult(policies.isEmpty() ? "NONE" : "FOUND", masked, policies, (int) inForce, source, Instant.now(), null);
    }

    public static PolicyLookupResult unavailable(String masked, String source, String message) {
        return new PolicyLookupResult("UNAVAILABLE", masked, List.of(), 0, source, Instant.now(), message);
    }

    public static PolicyLookupResult invalid(String message) {
        return new PolicyLookupResult("INVALID_ID", null, List.of(), 0, "validation", Instant.now(), message);
    }
}
