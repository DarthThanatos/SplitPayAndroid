package com.example.splitpayandroid.di.module

import com.example.splitpayandroid.intro.BiometricDialogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class ActivityModule{
    @ContributesAndroidInjector
    abstract fun contributeBiometricFragment(): BiometricDialogFragment
}