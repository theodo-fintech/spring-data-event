package com.sipios.spring.data.event.broadcaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.CallbackException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataEventBroadcaster {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DataEventBroadcaster(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private void broadcast(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    private String getTopic(String eventType, Object entity) {
        return entity.getClass().getSimpleName().toLowerCase() + "." + eventType;
    }

    private String getMessage(Object entity) throws CallbackException {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException error) {
            throw new CallbackException(error);
        }
    }

    public void broadcastEntityCreated(Object entity, String topicLabel) throws CallbackException {
        String topic = "".equals(topicLabel)?getTopic("created", entity): topicLabel;
        String message = getMessage(entity);
        broadcast(topic, message);
    }

    public void broadcastEntityUpdated(Object entity, String topicLabel) throws CallbackException {
        String topic = "".equals(topicLabel)?getTopic("updated", entity): topicLabel;
        String message = getMessage(entity);
        broadcast(topic, message);
    }

    public void broadcastEntityDeleted(Object entity, String topicLabel) throws CallbackException {
        String topic = "".equals(topicLabel)?getTopic("deleted", entity): topicLabel;
        String message = getMessage(entity);
        broadcast(topic, message);
    }
}