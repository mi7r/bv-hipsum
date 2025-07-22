package com.bv.processingapp.service;

import com.bv.processingapp.model.ComputationResultResponse;

public interface ComputationService {
    ComputationResultResponse processText(final int numberOfParagraphs);
}
