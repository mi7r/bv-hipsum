package com.bv.processingapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.services.hipsum")
@Getter
@Setter
public class HipsumProperties {
    private String baseUrl;
    private Integer parasParameterValue;
    private String fullUrl;
}
