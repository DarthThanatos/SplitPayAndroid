package com.example.splitpayandroid

import android.content.Context
import com.example.splitpayandroid.dagger_snippet.AbstractContributor
import com.example.splitpayandroid.dagger_snippet.Contributing
import com.example.splitpayandroid.groups.GroupsActivity
import com.example.splitpayandroid.intro.IntroActivity
import com.example.splitpayandroid.intro.IntroPresenterModule
import com.example.splitpayandroid.intro.ViewModelModule
import com.example.splitpayandroid.intro.BiometricDialogFragment
import com.example.splitpayandroid.retrofit.RetrofitProvider
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.android.*
import javax.inject.Singleton


@Component(modules = [
        ActivityModule::class, RetrofitProvider::class, AppModule::class,
        AndroidInjectionModule::class, Contributing::class, AbstractContributor::class,
        IntroPresenterModule::class, ViewModelModule::class, AppContextModule::class
    ]
)
@Singleton
interface AppComponent: AndroidInjector<App>{

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() // for binding App context
}

class App: DaggerApplication(){
    override fun applicationInjector() = DaggerAppComponent.builder().create(this)
}


@Suppress("unused")
@Module
abstract class ActivityModule{
    @ContributesAndroidInjector
    abstract fun contributeBiometricFragment(): BiometricDialogFragment
}

@Suppress("unused")
@Module
abstract class AppContextModule{

    @Binds
    abstract fun bindContext(app: App): Context
}

@Suppress("unused")
@Module
abstract class AppModule{

    @ContributesAndroidInjector
    abstract fun contributeIntro(): IntroActivity

    @ContributesAndroidInjector
    abstract fun contributeGroups(): GroupsActivity


}

