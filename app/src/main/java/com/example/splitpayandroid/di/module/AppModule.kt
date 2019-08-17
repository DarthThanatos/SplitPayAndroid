package com.example.splitpayandroid.di.module

import com.example.splitpayandroid.groups.GroupsActivity
import com.example.splitpayandroid.intro.ConfirmMailActivity
import com.example.splitpayandroid.intro.IntroActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class AppModule{

    @ContributesAndroidInjector
    abstract fun contributeIntro(): IntroActivity

    @ContributesAndroidInjector
    abstract fun contributeGroups(): GroupsActivity

    @ContributesAndroidInjector
    abstract fun contributeConfirm(): ConfirmMailActivity


}
