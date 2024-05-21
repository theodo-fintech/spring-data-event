package com.sipios.spring.data.event.annotation;

import com.sipios.spring.data.event.broadcaster.DataEventBroadcaster;
import com.sipios.spring.data.event.listener.DataEventListener;
import com.sipios.spring.data.event.listener.DataEventListenerRegistration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
@Import({DataEventBroadcaster.class, DataEventListener.class, DataEventListenerRegistration.class})
public @interface EnableDataEvent {
}
