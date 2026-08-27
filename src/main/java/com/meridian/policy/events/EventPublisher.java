package com.meridian.policy.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final boolean enabled;

    public EventPublisher(@Value("${meridian.events.enabled:false}") boolean enabled,
                          org.springframework.beans.factory.ObjectProvider<KafkaTemplate<String,String>> kafkaProvider,
                          ObjectMapper objectMapper) {
        this.enabled = enabled;
        this.kafkaTemplate = kafkaProvider.getIfAvailable();
        this.objectMapper = objectMapper;
    }

    public void publish(String topic, String eventType, Map<String,String> payload) {
        try {
            Map<String,Object> envelope = new java.util.LinkedHashMap<>();
            envelope.put("event_type", eventType);
            envelope.putAll(payload);
            String json = objectMapper.writeValueAsString(envelope);
            if (enabled && kafkaTemplate != null) {
                kafkaTemplate.send(topic, json);
                log.info("Published {} to {}: {}", eventType, topic, json);
            } else {
                log.info("Event {} to {} (kafka disabled): {}", eventType, topic, json);
            }
        } catch (Exception e) {
            log.warn("Failed to publish {}: {}", eventType, e.getMessage());
        }
    }
}
