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

//@Target(AnnotationTarget.FUNCTION)
//@Retention(AnnotationRetention.RUNTIME)
//@MapKey
//annotation class ProviderKey