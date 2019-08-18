package com.example.splitpayandroid

import com.example.splitpayandroid.di.component.DaggerAppComponent
import dagger.android.*
import timber.log.Timber


class App: DaggerApplication(){
    override fun applicationInjector() = DaggerAppComponent.builder().create(this)

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}


