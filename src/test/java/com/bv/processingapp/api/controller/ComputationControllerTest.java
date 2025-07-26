package com.bv.processingapp.api.controller;

import com.bv.processingapp.model.ComputationResult;
import com.bv.processingapp.service.ComputationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComputationControllerTest {

    @Mock
    private ComputationService computationService;

    @InjectMocks
    private ComputationController computationController;

    @Test
    void processText_shouldReturnResponseEntity() throws JsonProcessingException, ExecutionException, InterruptedException {
        final ComputationResult fakeComputationResult = ComputationResult.builder()
            .freqWord("check")
            .avgParagraphSize(100)
            .avgParagraphProcessingTime(100L)
            .totalProcessingTime(300L)
            .build();

        when(computationService.processText(1))
            .thenReturn(fakeComputationResult);

        ResponseEntity<ComputationResult> result = computationController.text(1);

        verify(computationService, times(1)).processText(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ComputationResult body = result.getBody();
        assertThat(body).isEqualTo(fakeComputationResult);
    }

}