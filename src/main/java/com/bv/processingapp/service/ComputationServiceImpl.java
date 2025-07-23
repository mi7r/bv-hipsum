package com.bv.processingapp.service;

import com.bv.processingapp.model.ComputationResult;
import com.bv.processingapp.service.kafka.KafkaComputationResultPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComputationServiceImpl implements ComputationService {

    private final HipsumClient hipsumClient;
    private final KafkaComputationResultPublisher kafkaProcessingResponsePublisher;
    private final ObjectMapper objectMapper;

    private static final String REGEX_EXPRESSION_FOR_SPLIT = "[^a-zA-Z]+";
    private static final String REGEX_EXPRESSION_TO_REMOVE_PUNCTUATION = "[,.]";

    @Override
    public ComputationResult processText(final int numberOfParagraphs) throws JsonProcessingException {
        final LocalDateTime requestProcessingStartTime = LocalDateTime.now();
        log.info("Received request with {} paragraphs to process.", numberOfParagraphs);

        int summedSizeOfAllParagraphs = 0;
        long summedProcessingTimeOfAllParagraphsInMiliseconds = 0L;
        final Map<String, Integer> requestWordsOccurrenceMap = new HashMap<>();

        for (int i = 1; i <= numberOfParagraphs; i++) {
            final LocalDateTime paragraphProcessingStartTime = LocalDateTime.now();
            log.info("Processing paragraph no: {}", i);

            final String singleParagraph = hipsumClient.provideText().getFirst().toLowerCase();

            mergeWordOccurrence(requestWordsOccurrenceMap, splitParagraphInToWords(singleParagraph));

            summedSizeOfAllParagraphs += singleParagraph.length();
            summedProcessingTimeOfAllParagraphsInMiliseconds += Duration.between(
                paragraphProcessingStartTime,
                LocalDateTime.now()
            ).toMillis();
        }

        String mostFrequentWord = getMostFrequentWord(requestWordsOccurrenceMap);
        log.info("Most frequent word is: {}", mostFrequentWord);

        final ComputationResult response = ComputationResult.builder()
            .freqWord(mostFrequentWord)
            .avgParagraphSize(summedSizeOfAllParagraphs / numberOfParagraphs)
            .avgParagraphProcessingTime(summedProcessingTimeOfAllParagraphsInMiliseconds / numberOfParagraphs)
            .totalProcessingTime(Duration.between(requestProcessingStartTime, LocalDateTime.now()).toMillis())
            .build();

        kafkaProcessingResponsePublisher.publishComputationResult(objectMapper.writeValueAsString(response));
        return response;
    }

    private static void mergeWordOccurrence(
        Map<String, Integer> requestWordsOccurrenceMap,
        Map<String, Integer> paragraphWordsOccurrenceMap
    ) {
        paragraphWordsOccurrenceMap.forEach(
            (word, occurrence) -> requestWordsOccurrenceMap.merge(word, occurrence, Integer::sum)
        );
    }

    private static String getMostFrequentWord(Map<String, Integer> wordsFrequencyMap) {
        return Collections.max(wordsFrequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private static Map<String, Integer> splitParagraphInToWords(String paragraph) {
        return Arrays.stream(paragraph
                .replaceAll(REGEX_EXPRESSION_TO_REMOVE_PUNCTUATION, "")
                .split(REGEX_EXPRESSION_FOR_SPLIT))
            .filter(word -> word.length() > 1 && word.chars().allMatch(Character::isLetter))
            .collect(Collectors.toMap(String::trim, word -> 1, Integer::sum));
    }
}
