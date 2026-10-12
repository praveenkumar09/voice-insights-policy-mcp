package com.aia.voiceinsights.policy.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "policy.api")
public record PolicyApiProperties(String baseUrl, int timeoutMs, Dummy dummy) {
    public record Dummy(boolean enabled) {}
}
