package com.bv.processingapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties({HipsumProperties.class})
@RequiredArgsConstructor
@Slf4j
@Configuration
public class RestClientConfig {

    private final HipsumProperties hipsumProperties;

    @Bean
    RestClient hipsumRestClient(){
        return RestClient.builder()
            .baseUrl(hipsumProperties.getFullUrl())
            .build();
    }
}
