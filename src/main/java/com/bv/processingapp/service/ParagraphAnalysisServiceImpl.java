package com.bv.processingapp.service;

import com.bv.processingapp.model.ParagraphAnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParagraphAnalysisServiceImpl implements ParagraphAnalysisService {

    private final HipsumClient hipsumClient;

    private static final String REGEX_EXPRESSION_FOR_SPLIT = "[^a-zA-Z]+";
    private static final String REGEX_EXPRESSION_TO_REMOVE_PUNCTUATION = "[,.]";

    @Override
    public ParagraphAnalysisResult analyzeParagraph() {
        final LocalDateTime paragraphProcessingStartTime = LocalDateTime.now();

        String singleParagraph = hipsumClient.provideText().getFirst();

        return ParagraphAnalysisResult.builder()
            .paragraphWordsMap(splitParagraphInToWordsMap(singleParagraph))
            .paragraphSize(singleParagraph.length())
            .paragraphProcessingTime(Duration.between(paragraphProcessingStartTime, LocalDateTime.now()).toMillis())
            .build();
    }


    private static Map<String, Integer> splitParagraphInToWordsMap(String paragraph) {
        return Arrays.stream(paragraph
                .replaceAll(REGEX_EXPRESSION_TO_REMOVE_PUNCTUATION, "")
                .split(REGEX_EXPRESSION_FOR_SPLIT))
            .filter(word -> word.length() > 1 && word.chars().allMatch(Character::isLetter))
            .collect(Collectors.toMap(String::trim, word -> 1, Integer::sum));
    }
}
