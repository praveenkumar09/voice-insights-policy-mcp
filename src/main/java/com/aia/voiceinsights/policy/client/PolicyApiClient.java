package com.aia.voiceinsights.policy.client;

import com.aia.voiceinsights.policy.model.Policy;

import java.util.List;

/** The one seam to the policy system. The dummy API and the real one sit behind the same HTTP contract, so only the base URL changes. */
public interface PolicyApiClient {
    /** @throws PolicyApiException if the policy system cannot be reached or answers with an error */
    List<Policy> findPolicies(String nric);
}
