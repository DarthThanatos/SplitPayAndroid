package com.example.splitpayandroid

import com.example.splitpayandroid.di.component.DaggerAppComponent
import dagger.android.*


class App: DaggerApplication(){
    override fun applicationInjector() = DaggerAppComponent.builder().create(this)
}


