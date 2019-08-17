package com.example.splitpayandroid.di.module

import android.content.Context
import com.example.splitpayandroid.App
import dagger.Binds
import dagger.Module


@Suppress("unused")
@Module
abstract class AppContextModule{

    @Binds
    abstract fun bindContext(app: App): Context
}
