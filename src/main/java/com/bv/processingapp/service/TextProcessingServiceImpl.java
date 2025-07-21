package com.bv.processingapp.service;

import com.bv.processingapp.api.model.ProcessingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextProcessingServiceImpl implements TextProcessingService {

    private final HipsumClient hipsumClient;

    @Override
    public ProcessingResponse processText(final int numberOfParagraphs) {
        LocalDateTime processingStartTime = LocalDateTime.now();

        final List<String> hipsumParagraphsList = hipsumClient.provideText();

        String paragraph = hipsumParagraphsList.stream().findFirst().get();

        String firstWord = Arrays.stream(paragraph.split(" ")).findFirst().get();
        log.info(paragraph);

        int paragraphSize = paragraph.length();

        LocalDateTime processingEndTime = LocalDateTime.now();

        long totalProcessingTime = Duration.between(processingStartTime, processingEndTime).toMillis();
        return ProcessingResponse.builder()
            .freqWord(firstWord)
            .avgParagraphSize(paragraphSize)
            .avgParagraphProcessingTime(totalProcessingTime)
            .totalProcessingTime(totalProcessingTime)
            .build();
    }
}
