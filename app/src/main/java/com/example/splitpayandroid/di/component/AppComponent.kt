package com.example.splitpayandroid.di.component

import com.example.splitpayandroid.App
import com.example.splitpayandroid.di.module.*
import com.example.splitpayandroid.di.module.RetrofitProvider
import com.example.splitpayandroid.di.scope.ApplicationScope
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector


@Component(modules = [
    AndroidInjectionModule::class,
    AndroidBindingModule::class,
    RetrofitProvider::class,
    DatabaseModule::class,
    ApplicationModule::class,
    ViewModelModule::class
])
@ApplicationScope
interface AppComponent: AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() // for binding App context
}
