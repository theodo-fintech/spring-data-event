package com.sipios.spring.data.event.listener;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

@Component
public class DataEventListenerRegistration {

    private final EntityManagerFactory entityManagerFactory;
    private final DataEventListener dataEventListener;

    public DataEventListenerRegistration(EntityManagerFactory entityManagerFactory, DataEventListener dataEventListener) {
        this.entityManagerFactory = entityManagerFactory;
        this.dataEventListener = dataEventListener;
    }

    @PostConstruct
    private void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(dataEventListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(dataEventListener);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(dataEventListener);
    }
}
