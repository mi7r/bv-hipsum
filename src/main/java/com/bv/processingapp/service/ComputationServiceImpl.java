package com.bv.processingapp.service;

import com.bv.processingapp.exception.ParagraphProcessingException;
import com.bv.processingapp.model.ComputationResult;
import com.bv.processingapp.model.ParagraphAnalysisResult;
import com.bv.processingapp.service.kafka.KafkaComputationResultPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComputationServiceImpl implements ComputationService {

    private final ParagraphAnalysisService paragraphAnalysisService;
    private final KafkaComputationResultPublisher kafkaProcessingResponsePublisher;
    private final ObjectMapper objectMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(30);

    @Override
    public ComputationResult processText(final int numberOfParagraphs) throws JsonProcessingException {
        final LocalDateTime requestProcessingStartTime = LocalDateTime.now();

        final AtomicInteger summedSizeOfAllParagraphs = new AtomicInteger(0);
        final AtomicLong summedProcessingTimeOfAllParagraphsInMilliseconds = new AtomicLong(0);
        final Map<String, Integer> requestWordsOccurenceMap = new ConcurrentHashMap<>();

        final List<Callable<ParagraphAnalysisResult>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfParagraphs; i++) {
            log.info("Starting to process paragraph: {}", i);
            tasks.add(paragraphAnalysisService::analyzeParagraph);
        }

        try {
            final List<Future<ParagraphAnalysisResult>> paragraphAnalysisResults = executorService.invokeAll(tasks);

            for (Future<ParagraphAnalysisResult> result : paragraphAnalysisResults) {
                log.info("Starting to process paragraph with size: {}", result.get().paragraphSize());
                ParagraphAnalysisResult paragraphAnalysisResult = result.get();
                summedSizeOfAllParagraphs.addAndGet(paragraphAnalysisResult.paragraphSize());
                summedProcessingTimeOfAllParagraphsInMilliseconds.addAndGet(paragraphAnalysisResult.paragraphProcessingTime());
                mergeWordOccurrence(requestWordsOccurenceMap, paragraphAnalysisResult.paragraphWordsMap());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new ParagraphProcessingException(e);
        }

        final ComputationResult response = ComputationResult.builder()
            .freqWord(getMostFrequentWord(requestWordsOccurenceMap))
            .avgParagraphSize(summedSizeOfAllParagraphs.get() / numberOfParagraphs)
            .avgParagraphProcessingTime(summedProcessingTimeOfAllParagraphsInMilliseconds.get() / numberOfParagraphs)
            .totalProcessingTime(Duration.between(requestProcessingStartTime, LocalDateTime.now()).toMillis())
            .build();

        kafkaProcessingResponsePublisher.publishComputationResult(objectMapper.writeValueAsString(response));
        return response;
    }

    private static String getMostFrequentWord(Map<String, Integer> wordsFrequencyMap) {
        return Collections.max(wordsFrequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private static void mergeWordOccurrence(
        Map<String, Integer> requestWordsOccurrenceMap,
        Map<String, Integer> paragraphWordsOccurrenceMap
    ) {
        paragraphWordsOccurrenceMap.forEach(
            (word, occurrence) -> requestWordsOccurrenceMap.merge(word, occurrence, Integer::sum)
        );
    }

}
