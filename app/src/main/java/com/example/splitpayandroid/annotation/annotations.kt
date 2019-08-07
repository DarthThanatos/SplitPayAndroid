package com.example.splitpayandroid.annotation


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
