package com.aia.voiceinsights.policy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PolicyMcpApplication {
    public static void main(String[] args) {
        SpringApplication.run(PolicyMcpApplication.class, args);
    }
}
