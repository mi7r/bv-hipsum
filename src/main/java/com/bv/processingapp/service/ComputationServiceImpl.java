package com.bv.processingapp.service;

import com.bv.processingapp.api.model.ComputationResultResponse;
import com.bv.processingapp.service.kafka.KafkaComputationResultPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComputationServiceImpl implements ComputationService {

    private final HipsumClient hipsumClient;
    private final KafkaComputationResultPublisher kafkaProcessingResponsePublisher;

    private static final String REGEX_EXPRESSION_FOR_SPLIT = "\\s+";
    private static final String REGEX_EXPRESSION_TO_REMOVE_PUNCTUATION = "[,.]";

    @Override
    public ComputationResultResponse processText(final int numberOfParagraphs) {
        final LocalDateTime totalProcessingStartTime = LocalDateTime.now();
        log.info("Received request with {} paragraphs to process.", numberOfParagraphs);

        final Map<String, Integer> totalWordFrequencyMap = new HashMap<>();
        final List<Integer> paragraphSizeList = new ArrayList<>();
        final List<Long> paragraphProcessingTimeInMillisList = new ArrayList<>();

        for (int i = 1; i <= numberOfParagraphs; i++) {
            final LocalDateTime paragraphProcessingStartTime = LocalDateTime.now();
            log.info("Processing paragraph no: {}", i);

            final List<String> hipsumParagraphsList = hipsumClient.provideText();
            final Map<String, Integer> paragraphWordFrequencyMap;

            final String paragraph = hipsumParagraphsList.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No paragraphs found in the response from Hipsum client."))
                .toLowerCase();

            paragraphSizeList.add(paragraph.length());

            paragraphWordFrequencyMap = splitParagraphToWords(paragraph);

            paragraphWordFrequencyMap.forEach(
                (word, occurrence) -> totalWordFrequencyMap.merge(word, occurrence, Integer::sum)
            );

            paragraphProcessingTimeInMillisList.add(Duration.between(
                paragraphProcessingStartTime,
                LocalDateTime.now()
            ).toMillis());
        }

        String mostFrequentWord = getMostFrequentWord(totalWordFrequencyMap);
        log.info("Most frequent word is: {}", mostFrequentWord);

        final ComputationResultResponse response = ComputationResultResponse.builder()
            .freqWord(mostFrequentWord)
            .avgParagraphSize(getAverageParagraphSize(paragraphSizeList))
            .avgParagraphProcessingTime(getAverageParagraphProcessingTime(paragraphProcessingTimeInMillisList))
            .totalProcessingTime(Duration.between(totalProcessingStartTime, LocalDateTime.now()).toMillis())
            .build();

        kafkaProcessingResponsePublisher.publishComputationResult(response);
        return response;
    }

    private static long getAverageParagraphProcessingTime(final List<Long> paragraphProcessingTimeInMillisList) {
        return paragraphProcessingTimeInMillisList.stream()
            .mapToLong(Long::longValue)
            .sum() / paragraphProcessingTimeInMillisList.size();
    }

    private static int getAverageParagraphSize(final List<Integer> paragraphSizeList) {
        return paragraphSizeList.stream().mapToInt(Integer::intValue).sum() / paragraphSizeList.size();
    }

    private String getMostFrequentWord(Map<String, Integer> wordFrequencyMap) {
        return wordFrequencyMap.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElseThrow(() -> new IllegalStateException("No most frequent word found in the map."));
    }

    private Map<String, Integer> splitParagraphToWords(String paragraph) {
        return Arrays.stream(paragraph
                .replaceAll(REGEX_EXPRESSION_TO_REMOVE_PUNCTUATION, "")
                .split(REGEX_EXPRESSION_FOR_SPLIT))
            .collect(Collectors.toMap(String::trim, word -> 1, Integer::sum));
    }

}
