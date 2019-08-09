package com.example.splitpayandroid.annotation

import dagger.MapKey


@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD
)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@javax.inject.Qualifier
annotation class FirstMap


@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD
)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@javax.inject.Qualifier
annotation class SecondMap

enum class MessageType {
    TEXT_MESSAGE_TYPE,
    IMAGE_MESSAGE_TYPE,
    INFO_MESSAGE_TYPE
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ProviderKey(val messageType: MessageType)