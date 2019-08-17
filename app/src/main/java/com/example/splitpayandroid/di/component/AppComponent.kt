package com.example.splitpayandroid.di.component

import com.example.splitpayandroid.App
import com.example.splitpayandroid.di.module.ActivityModule
import com.example.splitpayandroid.di.module.AppContextModule
import com.example.splitpayandroid.di.module.AppModule
import com.example.splitpayandroid.di.module.IntroPresenterModule
import com.example.splitpayandroid.di.snippet.AbstractContributor
import com.example.splitpayandroid.di.snippet.Contributing
import com.example.splitpayandroid.retrofit.RetrofitProvider
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton


@Component(modules = [
    ActivityModule::class, RetrofitProvider::class, AppModule::class,
    AndroidInjectionModule::class, Contributing::class, AbstractContributor::class,
    IntroPresenterModule::class, AppContextModule::class
]
)
@Singleton
interface AppComponent: AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() // for binding App context
}
