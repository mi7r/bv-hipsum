package com.bv.processingapp.service.kafka;

import com.bv.processingapp.api.model.ComputationResultResponse;
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
public class KafkaComputationResultPublisher implements ComputationResultPublisher {

    private final KafkaTemplate<String, ComputationResultResponse> kafkaTemplate;

    @Value("${external.services.kafka.topic}")
    private String kafkaTopic;

    @Override
    public void publishComputationResult(final ComputationResultResponse computationResult) {
        if (StringUtils.isNotBlank(kafkaTopic)) {
            kafkaTemplate.send(kafkaTopic, computationResult);
        } else {
            log.warn("No kafka topic configured - message not sent.");
        }
    }

    //todo: remove Listener from this repo
    @KafkaListener(id="processing_id",topics = "${external.services.kafka.topic}")
    public void listen(String processingResponse) {
        log.info("Received message from KAFKA topic: {}", processingResponse);
    }

}
