package com.example.splitpayandroid.di.module

import android.content.Context
import android.content.SharedPreferences
import com.example.splitpayandroid.App
import com.example.splitpayandroid.BuildConfig
import com.example.splitpayandroid.di.scope.ApplicationScope
import com.example.splitpayandroid.intro.BiometricDialogFragment
import com.example.splitpayandroid.retrofit.ConnectivityManager
import com.example.splitpayandroid.retrofit.ConnectivityManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class ApplicationModule{

    @Binds
    @ApplicationScope
    abstract fun bindContext(app: App): Context

    @ContributesAndroidInjector
    abstract fun contributeBiometricFragment(): BiometricDialogFragment

    @Binds
    @ApplicationScope
    abstract fun bindConnectivityManager(connectivityManagerImpl: ConnectivityManagerImpl): ConnectivityManager

    @Module
    companion object {
        @JvmStatic
        @Provides
        @ApplicationScope
        fun provideConnectivityManager(context: Context): android.net.ConnectivityManager {
            return context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        }

        @JvmStatic
        @Provides
        @ApplicationScope
        fun provideSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        }
    }
}
