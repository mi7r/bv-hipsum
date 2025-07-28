package com.bv.processingapp.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record ParagraphAnalysisResult(
    Map<String,Integer> paragraphWordsMap,
    Integer paragraphSize,
    Long paragraphProcessingTime
) {
}
