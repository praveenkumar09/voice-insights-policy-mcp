package com.aia.voiceinsights.policy.dummy;

import com.aia.voiceinsights.policy.model.Nric;
import com.aia.voiceinsights.policy.model.Policy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Stand-in for the real policy system. Same contract the real API must honour: POST /policies/search {"nric": "..."} ->
 * array of policies. Remove by setting policy.api.dummy.enabled=false and pointing policy.api.base-url at the real service.
 *
 * Fixture NRICs (format-valid, not real people):
 *   S1234567D  Term life + hospitalisation (in force), an old lapsed endowment
 *   S7654321C  Critical illness + investment-linked (AIA Pro Achiever Invest)
 *   T0123456A  Valid ID, holds nothing                          -> NONE
 *   G1111111X  Triggers a 500 from this dummy                   -> tool reports UNAVAILABLE
 * Any other format-valid NRIC -> no policies.
 */
@RestController
@RequestMapping("/dummy-api")
@ConditionalOnProperty(name = "policy.api.dummy.enabled", havingValue = "true", matchIfMissing = true)
public class DummyPolicyController {

    private static final Map<String, List<Policy>> FIXTURES = Map.of(
            "S1234567D", List.of(
                    new Policy("P-100234", "AIA-SGT", "AIA Secure Guard Term", "LIFE_TERM", "IN_FORCE",
                            new BigDecimal("500000"), new BigDecimal("640.00"), LocalDate.of(2021, 3, 1), "AIA"),
                    new Policy("P-100871", "AIA-HSP", "AIA HealthShield Gold Max", "HOSPITALISATION", "IN_FORCE",
                            null, new BigDecimal("1180.00"), LocalDate.of(2019, 7, 15), "AIA"),
                    new Policy("P-090112", null, "Endowment Plan 20", "ENDOWMENT", "LAPSED",
                            new BigDecimal("50000"), new BigDecimal("2400.00"), LocalDate.of(2012, 1, 10), "Another insurer")),
            "S7654321C", List.of(
                    new Policy("P-200456", "AIA-CI", "AIA Max Care Critical Illness", "CRITICAL_ILLNESS", "IN_FORCE",
                            new BigDecimal("150000"), new BigDecimal("1520.00"), LocalDate.of(2022, 9, 1), "AIA"),
                    new Policy("P-200990", "AIA-PAI", "AIA Pro Achiever Invest", "INVESTMENT_LINKED", "IN_FORCE",
                            new BigDecimal("100000"), new BigDecimal("6000.00"), LocalDate.of(2023, 2, 20), "AIA")),
            "T0123456A", List.of());

    @PostMapping("/policies/search")
    public ResponseEntity<?> search(@RequestBody Map<String, String> body) {
        String nric = Nric.normalise(body.get("nric"));
        if (!Nric.isValid(nric)) return ResponseEntity.badRequest().body(Map.of("error", "Invalid NRIC format"));
        if ("G1111111X".equals(nric)) return ResponseEntity.internalServerError().body(Map.of("error", "simulated policy system outage"));
        return ResponseEntity.ok(FIXTURES.getOrDefault(nric, List.of()));
    }
}
