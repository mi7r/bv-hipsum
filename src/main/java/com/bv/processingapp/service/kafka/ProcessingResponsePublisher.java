package com.bv.processingapp.service.kafka;

public interface ProcessingResponsePublisher {
    void publishProcessingResponse(String processingResponse);
}
