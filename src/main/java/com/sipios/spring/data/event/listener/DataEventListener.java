package com.sipios.spring.data.event.listener;

import com.sipios.spring.data.event.annotation.DataEventEntity;
import com.sipios.spring.data.event.broadcaster.DataEventBroadcaster;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
public class DataEventListener implements PostCommitInsertEventListener, PostCommitUpdateEventListener, PostCommitDeleteEventListener {

    private final DataEventBroadcaster dataEventBroadcaster;
    private final EntityManagerFactory entityManagerFactory;

    @PostConstruct
    private void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_COMMIT_UPDATE).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_COMMIT_DELETE).appendListener(this);
    }

    public DataEventListener(DataEventBroadcaster dataEventBroadcaster, EntityManagerFactory entityManagerFactory) {
        this.dataEventBroadcaster = dataEventBroadcaster;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if(event.getEntity().getClass().isAnnotationPresent(DataEventEntity.class)) {
            DataEventEntity annotation = event.getEntity().getClass().getAnnotation(DataEventEntity.class);
            dataEventBroadcaster.broadcastEntityDeleted(event.getEntity(), annotation.deletionTopic());
        }
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if(event.getEntity().getClass().isAnnotationPresent(DataEventEntity.class)) {
            DataEventEntity annotation = event.getEntity().getClass().getAnnotation(DataEventEntity.class);
            dataEventBroadcaster.broadcastEntityCreated(event.getEntity(), annotation.creationTopic());
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if(event.getEntity().getClass().isAnnotationPresent(DataEventEntity.class)) {
            DataEventEntity annotation = event.getEntity().getClass().getAnnotation(DataEventEntity.class);
            dataEventBroadcaster.broadcastEntityUpdated(event.getEntity(), annotation.updateTopic());
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return true;
    }

    @Override
    public void onPostInsertCommitFailed(PostInsertEvent event) {
    }

    @Override
    public void onPostDeleteCommitFailed(PostDeleteEvent event) {

    }

    @Override
    public void onPostUpdateCommitFailed(PostUpdateEvent event) {
    }
}
