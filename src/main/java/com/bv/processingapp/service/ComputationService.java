package com.bv.processingapp.service;

import com.bv.processingapp.api.model.ComputationResultResponse;

public interface ComputationService {
    ComputationResultResponse processText(final int numberOfParagraphs);
}
