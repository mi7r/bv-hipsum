package com.bv.processingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HipsumClientImpl implements HipsumClient {

    private final RestClient hipsumRestClient;

    @Override
    public List<String> provideText() {
        return hipsumRestClient
            .get()
            .retrieve()
            .body(new ParameterizedTypeReference<List<String>>() {});
    }
}
