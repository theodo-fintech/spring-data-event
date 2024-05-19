package com.sipios.spring.data.event.listener;

import com.sipios.spring.data.event.annotation.DataEventEntity;
import com.sipios.spring.data.event.broadcaster.DataEventBroadcaster;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataEventListenerTest {
    @Mock
    private DataEventBroadcaster dataEventBroadcaster;

    @InjectMocks
    private DataEventListener listener;

    @Nested
    class AnnotatedEntityTests {

        @Test
        public void testOnPostInsert() {
            PostInsertEvent event = mock(PostInsertEvent.class);
            TestEntity entity = new TestEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);

            listener.onPostInsert(event);

            verify(dataEventBroadcaster).broadcastEntityCreated(entity, "testEntity.created");
        }

        @Test
        public void testOnPostUpdate() {
            PostUpdateEvent event = mock(PostUpdateEvent.class);
            TestEntity entity = new TestEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);

            listener.onPostUpdate(event);

            verify(dataEventBroadcaster).broadcastEntityUpdated(entity, "testEntity.updated");
        }

        @Test
        public void testOnPostDelete() {
            PostDeleteEvent event = mock(PostDeleteEvent.class);
            TestEntity entity = new TestEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);

            listener.onPostDelete(event);

            verify(dataEventBroadcaster).broadcastEntityDeleted(entity, "testEntity.deleted");
        }

        @Test
        public void testOnPostInsertJsonProcessingException() throws Exception {
            PostInsertEvent event = mock(PostInsertEvent.class);
            TestEntity entity = new TestEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);
            doThrow(new RuntimeException("JSON processing error")).when(dataEventBroadcaster).broadcastEntityCreated(any(), any());

            assertThrows(RuntimeException.class, () -> listener.onPostInsert(event));
        }

        @Test
        public void testOnPostUpdateJsonProcessingException() throws Exception {
            PostUpdateEvent event = mock(PostUpdateEvent.class);
            TestEntity entity = new TestEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);
            doThrow(new RuntimeException("JSON processing error")).when(dataEventBroadcaster).broadcastEntityUpdated(any(), any());

            assertThrows(RuntimeException.class, () -> listener.onPostUpdate(event));
        }

        @Test
        public void testOnPostDeleteJsonProcessingException() throws Exception {
            PostDeleteEvent event = mock(PostDeleteEvent.class);
            TestEntity entity = new TestEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);
            doThrow(new RuntimeException("JSON processing error")).when(dataEventBroadcaster).broadcastEntityDeleted(any(), any());

            assertThrows(RuntimeException.class, () -> listener.onPostDelete(event));
        }

        @DataEventEntity(creationTopic = "testEntity.created", updateTopic = "testEntity.updated", deletionTopic = "testEntity.deleted")
        @Getter
        @Setter
        @AllArgsConstructor
        private static class TestEntity {
            private int id;
            private String name;
            private boolean active;
        }
    }

    @Nested
    class NonAnnotatedEntityTests {

        @Test
        public void testOnPostInsert() {
            PostInsertEvent event = mock(PostInsertEvent.class);
            NonAnnotatedEntity entity = new NonAnnotatedEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);

            listener.onPostInsert(event);

            verify(dataEventBroadcaster, never()).broadcastEntityCreated(any(), any());
        }

        @Test
        public void testOnPostUpdate() {
            PostUpdateEvent event = mock(PostUpdateEvent.class);
            NonAnnotatedEntity entity = new NonAnnotatedEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);

            listener.onPostUpdate(event);

            verify(dataEventBroadcaster, never()).broadcastEntityUpdated(any(), any());
        }

        @Test
        public void testOnPostDelete() {
            PostDeleteEvent event = mock(PostDeleteEvent.class);
            NonAnnotatedEntity entity = new NonAnnotatedEntity(1, "Test Name", true);
            when(event.getEntity()).thenReturn(entity);

            listener.onPostDelete(event);

            verify(dataEventBroadcaster, never()).broadcastEntityDeleted(any(), any());
        }

        @Getter
        @Setter
        @AllArgsConstructor
        private static class NonAnnotatedEntity {
            private int id;
            private String name;
            private boolean active;
        }
    }
}
