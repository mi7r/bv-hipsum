package com.bv.processingapp.service.kafka;

public interface ComputationResultPublisher {
    void publishComputationResult(String computationResult);
}
