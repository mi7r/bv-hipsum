package com.bv.processingapp.service;

import com.bv.processingapp.api.model.ProcessingResponse;

public interface TextProcessingService {
    ProcessingResponse processText(final int numberOfParagraphs);
}
