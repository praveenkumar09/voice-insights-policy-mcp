package com.aia.voiceinsights.policy;

import com.aia.voiceinsights.policy.client.PolicyApiClient;
import com.aia.voiceinsights.policy.client.PolicyApiException;
import com.aia.voiceinsights.policy.mcp.PolicyTools;
import com.aia.voiceinsights.policy.model.Nric;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/** Runs the tool through the real REST client against the dummy API served by the same app. */
// Fixed port: the REST client is built at startup, before a random port would be known.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"server.port=18085", "policy.api.base-url=http://localhost:18085/dummy-api"})
class PolicyToolsTest {

    @Autowired PolicyTools tools;
    @Autowired PolicyApiClient client;

    @Test void foundReturnsPoliciesAndMasksNric() {
        var r = tools.getExistingPolicies("s1234567d");
        assertThat(r.status()).isEqualTo("FOUND");
        assertThat(r.policies()).hasSize(3);
        assertThat(r.inForceCount()).isEqualTo(2);
        assertThat(r.nricMasked()).isEqualTo("S****567D").doesNotContain("1234");
    }

    @Test void validIdWithNoPoliciesIsNoneNotUnknown() {
        assertThat(tools.getExistingPolicies("T0123456A").status()).isEqualTo("NONE");
        assertThat(tools.getExistingPolicies("S9999999Z").status()).isEqualTo("NONE");
    }

    @Test void outageIsUnavailableNeverNone() {
        var r = tools.getExistingPolicies("G1111111X");
        assertThat(r.status()).isEqualTo("UNAVAILABLE");
        assertThat(r.policies()).isEmpty();
    }

    @Test void malformedIdIsRejectedBeforeAnyCall() {
        assertThat(tools.getExistingPolicies("12345").status()).isEqualTo("INVALID_ID");
        assertThat(Nric.isValid("S1234567")).isFalse();
    }

    @Test void clientThrowsOnOutage() {
        org.junit.jupiter.api.Assertions.assertThrows(PolicyApiException.class, () -> client.findPolicies("G1111111X"));
    }
}
