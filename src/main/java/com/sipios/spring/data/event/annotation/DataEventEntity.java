package com.sipios.spring.data.event.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface DataEventEntity {

    String creationTopic() default "";
    String deletionTopic() default "";
    String updateTopic() default "";
}