package com.bv.processingapp.service.kafka;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProcessingResponsePublisher implements ProcessingResponsePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${external.services.kafka.topic}")
    private String kafkaTopic;

    @Override
    public void publishProcessingResponse(final String processingResponse) {
        if (StringUtils.isNotBlank(kafkaTopic)) {
            kafkaTemplate.send(kafkaTopic, processingResponse);
        } else {
            log.warn("No kafka topic configured - message not sent.");
        }
    }

    //todo: remove Listener from this repo
    @KafkaListener(id="processing_id",topics = "${external.services.kafka.topic}")
    public void listen(String processingResponse) {
        log.info("Received response from external service: {}", processingResponse);
    }

}
