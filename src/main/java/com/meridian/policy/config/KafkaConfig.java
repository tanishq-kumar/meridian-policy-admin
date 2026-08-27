package com.meridian.policy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    @Bean
    @ConditionalOnProperty(name = "meridian.events.enabled", havingValue = "true")
    public KafkaTemplate<String,String> kafkaTemplate(ProducerFactory<String,String> pf) {
        return new KafkaTemplate<>(pf);
    }
    @Bean
    public ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }
}
