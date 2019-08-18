package com.example.splitpayandroid.di.module

import android.content.Context
import com.example.splitpayandroid.retrofit.ConnectivityManager
import com.example.splitpayandroid.retrofit.ConnectivityManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
abstract class ConnectivityModule{

    @Binds
    abstract fun bindConnectivityManager(connectivityManagerImpl: ConnectivityManagerImpl): ConnectivityManager

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideConnectivityManager(context: Context): android.net.ConnectivityManager {
            return context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        }
    }
}