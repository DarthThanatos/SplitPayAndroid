package com.example.splitpayandroid.di.module

import com.example.splitpayandroid.intro.IntroPresenter
import com.example.splitpayandroid.intro.IntroPresenterImpl
import dagger.Binds
import dagger.Module


@Suppress("unused")
@Module
interface IntroPresenterModule{
    @Binds
    fun bindIntroPresenter(introPresenterImpl: IntroPresenterImpl): IntroPresenter
}