package com.sipios.spring.data.event.listener;

import com.sipios.spring.data.event.annotation.DataEventEntity;
import com.sipios.spring.data.event.broadcaster.DataEventBroadcaster;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
public class DataEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private final DataEventBroadcaster dataEventBroadcaster;

    public DataEventListener(DataEventBroadcaster dataEventBroadcaster) {
        this.dataEventBroadcaster = dataEventBroadcaster;
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
}
