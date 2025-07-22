package com.bv.processingapp.service.kafka;

import com.bv.processingapp.api.model.ComputationResultResponse;

public interface ComputationResultPublisher {
    void publishComputationResult(ComputationResultResponse computationResult);
}
