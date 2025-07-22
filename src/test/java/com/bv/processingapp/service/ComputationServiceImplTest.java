package com.bv.processingapp.service;

import com.bv.processingapp.model.ComputationResult;
import com.bv.processingapp.service.kafka.KafkaComputationResultPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComputationServiceImplTest {

    @Mock
    private HipsumClient hipsumClient;

    @Mock
    private KafkaComputationResultPublisher kafkaComputationResultPublisher;

    @Mock
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ComputationServiceImpl computationServiceImpl;

    private static final List<String> MOCKED_PARAGRAPH = List.of("check.  chEck word, word, CHECK,");
    private static final List<String> MOCKED_PARAGRAPH_2 = List.of("mic. check, but word, word WORD,  word,");
    private static final List<String> MOCKED_PARAGRAPH_3 = List.of("check. CHECK,  cheCK, word, WORD. wOrD,  CHEck");

    @Test
    void processText_forSingleParagraph_shouldReturnMostFrequentWord() throws JsonProcessingException {
        when(hipsumClient.provideText()).thenReturn(MOCKED_PARAGRAPH);

        ComputationResult result = computationServiceImpl.processText(1);
        assertEquals("check", result.freqWord());
        assertNotNull(result.avgParagraphProcessingTime());
        assertNotNull(result.avgParagraphSize());
        assertNotNull(result.totalProcessingTime());
    }

    @Test
    void processText_forMultipleParagraphs_shouldProperlySumUpWordOccurrence() throws JsonProcessingException {
        when(hipsumClient.provideText()).thenReturn(MOCKED_PARAGRAPH).thenReturn(MOCKED_PARAGRAPH_2);

        ComputationResult result = computationServiceImpl.processText(2);
        assertEquals("word", result.freqWord());
        assertNotNull(result.avgParagraphProcessingTime());
        assertNotNull(result.avgParagraphSize());
        assertNotNull(result.totalProcessingTime());
    }

    @Test
    void processText_shouldRemoveDoubleSpacesAndPunctuation() throws JsonProcessingException {
        when(hipsumClient.provideText()).thenReturn(MOCKED_PARAGRAPH_3);

        ComputationResult result = computationServiceImpl.processText(1);
        assertEquals("check", result.freqWord());
        assertNotNull(result.avgParagraphProcessingTime());
        assertNotNull(result.avgParagraphSize());
        assertNotNull(result.totalProcessingTime());
    }

    @Test
    void processText_forSingleParagraph_shouldSendMessageToKafkaOnce() throws JsonProcessingException {
        when(hipsumClient.provideText()).thenReturn(MOCKED_PARAGRAPH);

        ComputationResult result = computationServiceImpl.processText(1);

        verify(kafkaComputationResultPublisher, times(1))
            .publishComputationResult(objectMapper.writeValueAsString(result));
    }

    @Test
    void processText_forMultipleParagraphs_shouldSendMessageToKafkaOnce() throws JsonProcessingException {
        when(hipsumClient.provideText()).thenReturn(MOCKED_PARAGRAPH).thenReturn(MOCKED_PARAGRAPH_2);

        ComputationResult result = computationServiceImpl.processText(2);

        verify(kafkaComputationResultPublisher, times(1))
            .publishComputationResult(objectMapper.writeValueAsString(result));
    }
}