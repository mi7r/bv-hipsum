package com.bv.processingapp.service;

import com.bv.processingapp.config.HipsumProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@EnableConfigurationProperties({HipsumProperties.class})
@RequiredArgsConstructor
@Service
public class HipsumClientImpl implements HipsumClient {

    private final RestClient hipsumRestClient;
    private final HipsumProperties hipsumProperties;

    @Override
    public List<String> provideDummyText(final int numberOfParagraphs) {

        final List<String> listOfParagraphs;

        listOfParagraphs = hipsumRestClient
            .get()
            .retrieve()
            .body(new ParameterizedTypeReference<List<String>>() {});

        return listOfParagraphs;
    }
}
