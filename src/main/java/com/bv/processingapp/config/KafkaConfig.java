package com.bv.processingapp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${external.services.kafka.topic}")
    private String kafkaTopic;

    @Bean
    public NewTopic wordProcessedTopic(){
        return TopicBuilder
            .name(kafkaTopic)
            .partitions(4)
            .replicas(1)
            .build();
    }
}
