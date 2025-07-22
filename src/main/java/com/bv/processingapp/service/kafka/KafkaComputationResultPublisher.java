package com.bv.processingapp.service.kafka;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaComputationResultPublisher implements ComputationResultPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${external.services.kafka.topic}")
    private String kafkaTopic;

    @Override
    public void publishComputationResult(final String computationResult) {
        if (StringUtils.isNotBlank(kafkaTopic)) {
            kafkaTemplate.send(kafkaTopic, computationResult);
            log.info("Message: {} sent to Kafka topic: {}", computationResult, kafkaTopic);
        } else {
            log.warn("No kafka topic configured - message not sent.");
        }
    }
}
