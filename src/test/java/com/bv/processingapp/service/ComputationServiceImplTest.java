package com.bv.processingapp.service;

import com.bv.processingapp.model.ComputationResult;
import com.bv.processingapp.model.ParagraphAnalysisResult;
import com.bv.processingapp.service.kafka.KafkaComputationResultPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComputationServiceImplTest {

    @Mock
    private KafkaComputationResultPublisher kafkaComputationResultPublisherMock;

    @Mock
    private ObjectMapper objectMapperMock = new ObjectMapper();

    @Mock
    private ParagraphAnalysisService paragraphAnalysisServiceMock;

    @InjectMocks
    private ComputationServiceImpl computationService;

    private static final List<String> MOCKED_PARAGRAPH = List.of("check.  chEck word, word, CHECK,");
    private static final List<String> MOCKED_PARAGRAPH_2 = List.of("mic. check, but word, word WORD,  word,");
    private static final List<String> MOCKED_PARAGRAPH_3 = List.of("check. CHECK,  cheCK, word, WORD. wOrD,  CHEck");

    private static final ParagraphAnalysisResult MOCKED_PARAGRAPH_ANALYSIS_RESULT = ParagraphAnalysisResult.builder()
        .paragraphWordsMap(Map.of(
            "check", 3,
            "word", 2
        ))
        .paragraphSize(MOCKED_PARAGRAPH.size())
        .paragraphProcessingTime(100L)
        .build();

    private static final ParagraphAnalysisResult MOCKED_PARAGRAPH_ANALYSIS_RESULT_2 = ParagraphAnalysisResult.builder()
        .paragraphWordsMap(Map.of(
            "check", 1,
            "word", 4,
            "mic", 1,
            "but", 1
        ))
        .paragraphSize(MOCKED_PARAGRAPH_2.size())
        .paragraphProcessingTime(100L)
        .build();

    private final static ParagraphAnalysisResult MOCKED_PARAGRAPH_ANALYSIS_RESULT_3 = ParagraphAnalysisResult.builder()
        .paragraphWordsMap(Map.of(
            "check", 3,
            "word", 2
        ))
        .paragraphSize(MOCKED_PARAGRAPH_3.size())
        .paragraphProcessingTime(100L)
        .build();

    @Test
    void processText_forSingleParagraph_shouldReturnMostFrequentWord() throws JsonProcessingException {
        when(paragraphAnalysisServiceMock.analyzeParagraph()).thenReturn(MOCKED_PARAGRAPH_ANALYSIS_RESULT);

        ComputationResult result = computationService.processText(1);
        assertEquals("check", result.freqWord());
        assertNotNull(result.avgParagraphProcessingTime());
        assertNotNull(result.avgParagraphSize());
        assertNotNull(result.totalProcessingTime());
    }

    @Test
    void processText_forMultipleParagraphs_shouldProperlySumUpWordOccurrence() throws JsonProcessingException {
        when(paragraphAnalysisServiceMock.analyzeParagraph()).thenReturn(MOCKED_PARAGRAPH_ANALYSIS_RESULT).thenReturn(MOCKED_PARAGRAPH_ANALYSIS_RESULT_2);

        ComputationResult result = computationService.processText(2);
        assertEquals("word", result.freqWord());
        assertNotNull(result.avgParagraphProcessingTime());
        assertNotNull(result.avgParagraphSize());
        assertNotNull(result.totalProcessingTime());
    }

    @Test
    void processText_shouldRemoveDoubleSpacesAndPunctuation() throws JsonProcessingException {
        when(paragraphAnalysisServiceMock.analyzeParagraph()).thenReturn(MOCKED_PARAGRAPH_ANALYSIS_RESULT_3);

        ComputationResult result = computationService.processText(1);
        assertEquals("check", result.freqWord());
        assertNotNull(result.avgParagraphProcessingTime());
        assertNotNull(result.avgParagraphSize());
        assertNotNull(result.totalProcessingTime());
    }

    @Test
    void processText_forSingleParagraph_shouldSendMessageToKafkaOnce() throws JsonProcessingException {
        when(paragraphAnalysisServiceMock.analyzeParagraph()).thenReturn(MOCKED_PARAGRAPH_ANALYSIS_RESULT);

        ComputationResult result = computationService.processText(1);

        verify(kafkaComputationResultPublisherMock, times(1))
            .publishComputationResult(objectMapperMock.writeValueAsString(result));
    }

    @Test
    void processText_forMultipleParagraphs_shouldSendMessageToKafkaOnce() throws JsonProcessingException {
        when(paragraphAnalysisServiceMock.analyzeParagraph()).thenReturn(MOCKED_PARAGRAPH_ANALYSIS_RESULT).thenReturn(MOCKED_PARAGRAPH_ANALYSIS_RESULT_2);

        ComputationResult result = computationService.processText(2);

        verify(kafkaComputationResultPublisherMock, times(1))
            .publishComputationResult(objectMapperMock.writeValueAsString(result));
    }
}