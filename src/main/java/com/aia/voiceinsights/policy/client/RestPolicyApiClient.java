package com.aia.voiceinsights.policy.client;

import com.aia.voiceinsights.policy.model.Policy;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Calls {@code POST {base-url}/policies/search} with the NRIC in the BODY (never the URL, which ends up in access logs).
 * Response: a JSON array of {@link Policy}; an empty array means the customer holds none.
 */
@Component
public class RestPolicyApiClient implements PolicyApiClient {

    private final RestClient http;

    public RestPolicyApiClient(PolicyApiProperties props) {
        var settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(props.timeoutMs()))
                .withReadTimeout(Duration.ofMillis(props.timeoutMs()));
        this.http = RestClient.builder()
                .baseUrl(props.baseUrl())
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    @Override
    public List<Policy> findPolicies(String nric) {
        try {
            List<Policy> body = http.post().uri("/policies/search")
                    .body(Map.of("nric", nric))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return body == null ? List.of() : body;
        } catch (Exception e) {
            throw new PolicyApiException("Policy system call failed: " + e.getClass().getSimpleName(), e);
        }
    }
}
