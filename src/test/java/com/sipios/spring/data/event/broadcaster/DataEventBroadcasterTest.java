package com.sipios.spring.data.event.broadcaster;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.CallbackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

public class DataEventBroadcasterTest {
    private DataEventBroadcaster broadcaster;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        kafkaTemplate = mock(KafkaTemplate.class);
        objectMapper = new ObjectMapper();
        broadcaster = new DataEventBroadcaster(kafkaTemplate, objectMapper);
    }

    @Test
    void testBroadcastEntityCreated() throws Exception {
        TestEntity entity = new TestEntity(1, "Test Name", true);
        String expectedJson = objectMapper.writeValueAsString(entity);

        broadcaster.broadcastEntityCreated(entity, "testEntity.created");

        verify(kafkaTemplate).send(eq("testEntity.created"), eq(expectedJson));
    }

    @Test
    void testBroadcastEntityUpdated() throws Exception {
        TestEntity entity = new TestEntity(1, "Test Name", false);
        String expectedJson = objectMapper.writeValueAsString(entity);

        broadcaster.broadcastEntityUpdated(entity, "testEntity.updated");

        verify(kafkaTemplate).send(eq("testEntity.updated"), eq(expectedJson));
    }

    @Test
    void testBroadcastEntityDeleted() throws Exception {
        TestEntity entity = new TestEntity(1, "Test Name", true);
        String expectedJson = objectMapper.writeValueAsString(entity);

        broadcaster.broadcastEntityDeleted(entity, "testEntity.deleted");

        verify(kafkaTemplate).send(eq("testEntity.deleted"), eq(expectedJson));
    }

    @Test
    void testBroadcastEntityCreatedJsonProcessingException() throws Exception {
        objectMapper = mock(ObjectMapper.class);
        broadcaster = new DataEventBroadcaster(kafkaTemplate, objectMapper);
        TestEntity entity = new TestEntity(1, "Test Name", true);

        when(objectMapper.writeValueAsString(entity)).thenThrow(new JsonProcessingException("JSON processing error") {});

        assertThrows(CallbackException.class, () -> broadcaster.broadcastEntityCreated(entity, "testEntity.created"));
    }

    @Test
    void testBroadcastEntityUpdatedJsonProcessingException() throws Exception {
        objectMapper = mock(ObjectMapper.class);
        broadcaster = new DataEventBroadcaster(kafkaTemplate, objectMapper);
        TestEntity entity = new TestEntity(1, "Test Name", false);

        when(objectMapper.writeValueAsString(entity)).thenThrow(new JsonProcessingException("JSON processing error") {});

        assertThrows(CallbackException.class, () -> broadcaster.broadcastEntityUpdated(entity, "testEntity.updated"));
    }

    @Test
    void testBroadcastEntityDeletedJsonProcessingException() throws Exception {
        objectMapper = mock(ObjectMapper.class);
        broadcaster = new DataEventBroadcaster(kafkaTemplate, objectMapper);
        TestEntity entity = new TestEntity(1, "Test Name", true);

        when(objectMapper.writeValueAsString(entity)).thenThrow(new JsonProcessingException("JSON processing error") {});

        assertThrows(CallbackException.class, () -> broadcaster.broadcastEntityDeleted(entity, "testEntity.deleted"));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TestEntity {
        private int id;
        private String name;
        private boolean active;
    }
}
