package com.example.splitpayandroid

import com.example.splitpayandroid.dagger_snippet.AbstractContributor
import com.example.splitpayandroid.dagger_snippet.Contributing
import com.example.splitpayandroid.groups.GroupsActivity
import com.example.splitpayandroid.intro.IntroActivity
import com.example.splitpayandroid.intro.IntroPresenterModule
import com.example.splitpayandroid.retrofit.RetrofitProvider
import dagger.Component
import dagger.Module
import dagger.android.*
import javax.inject.Singleton


@Component(modules = [RetrofitProvider::class, AppModule::class, AndroidInjectionModule::class, Contributing::class, AbstractContributor::class, IntroPresenterModule::class])
@Singleton
interface AppComponent: AndroidInjector<App>

class App: DaggerApplication(){
    override fun applicationInjector() = DaggerAppComponent.create()
}


@Suppress("unused")
@Module
abstract class AppModule{

    @ContributesAndroidInjector
    abstract fun contributeIntro(): IntroActivity

    @ContributesAndroidInjector
    abstract fun contributeGroups(): GroupsActivity


}

