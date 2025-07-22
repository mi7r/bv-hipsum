package com.bv.processingapp.service;

import com.bv.processingapp.model.ComputationResult;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ComputationService {
    ComputationResult processText(final int numberOfParagraphs) throws JsonProcessingException;
}
