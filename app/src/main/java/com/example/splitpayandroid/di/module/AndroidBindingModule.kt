package com.example.splitpayandroid.di.module

import com.example.splitpayandroid.groups.GroupsActivity
import com.example.splitpayandroid.groups.GroupsFragment
import com.example.splitpayandroid.intro.*
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class AndroidBindingModule{

    @ContributesAndroidInjector
    abstract fun contributeIntro(): IntroActivity

    @ContributesAndroidInjector
    abstract fun contributeGroups(): GroupsActivity

    @ContributesAndroidInjector
    abstract fun contributeConfirm(): ConfirmMailActivity

    @ContributesAndroidInjector
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeEmailOnlyRegistrationFragment(): EmailOnlyRegistrationFragment

    @ContributesAndroidInjector
    abstract fun contributeGroupsFragment(): GroupsFragment

}
