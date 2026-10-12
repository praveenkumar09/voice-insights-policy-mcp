package com.aia.voiceinsights.policy.mcp;

import com.aia.voiceinsights.policy.client.PolicyApiClient;
import com.aia.voiceinsights.policy.model.Nric;
import com.aia.voiceinsights.policy.model.PolicyLookupResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class PolicyTools {

    private static final Logger log = LoggerFactory.getLogger(PolicyTools.class);

    private final PolicyApiClient api;

    public PolicyTools(PolicyApiClient api) {
        this.api = api;
    }

    @Tool(name = "get_existing_policies", description = """
            Returns the insurance policies a customer ALREADY holds, looked up by Singapore NRIC/FIN. Use it before suggesting any
            product, so a product the customer already owns is not suggested again. Result status: FOUND (policies listed),
            NONE (the policy system confirms the customer holds none), UNAVAILABLE (the system could not be reached: coverage is
            UNKNOWN, do not treat it as none), INVALID_ID (the NRIC is malformed). Each policy has a category
            (LIFE_TERM, LIFE_WHOLE, CRITICAL_ILLNESS, HOSPITALISATION, INVESTMENT_LINKED, ENDOWMENT, PERSONAL_ACCIDENT, ANNUITY)
            and a status; only IN_FORCE policies count as current cover.""")
    public PolicyLookupResult getExistingPolicies(
            @ToolParam(description = "The customer's NRIC or FIN, e.g. S1234567D") String nric) {
        if (!Nric.isValid(nric)) return PolicyLookupResult.invalid("The NRIC/FIN is not in a valid format.");
        String normalised = Nric.normalise(nric);
        String masked = Nric.mask(normalised);
        try {
            var result = PolicyLookupResult.found(masked, api.findPolicies(normalised), "policy-api");
            log.info("get_existing_policies {} -> {} ({} policies)", masked, result.status(), result.policies().size());
            return result;
        } catch (Exception e) {
            log.warn("get_existing_policies {} failed: {}", masked, e.getMessage());
            return PolicyLookupResult.unavailable(masked, "policy-api", "The policy system could not be reached; existing cover is unknown.");
        }
    }
}
