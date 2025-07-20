package com.bv.processingapp.api.model;

import lombok.Builder;

@Builder
public record ProcessingResponse(
    String freqWord, Integer avgParagraphSize, Long avgParagraphProcessingTime, Long totalProcessingTime
) {
}
