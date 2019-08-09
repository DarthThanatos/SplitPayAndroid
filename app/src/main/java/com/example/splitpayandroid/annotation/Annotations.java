package com.example.splitpayandroid.annotation;

import dagger.MapKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

enum MessageType {
    TEXT_MESSAGE_TYPE,
    IMAGE_MESSAGE_TYPE,
    INFO_MESSAGE_TYPE;
}



@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey
@interface Annotations {
    MessageType value();
}