package com.example.splitpayandroid

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import com.example.splitpayandroid.annotation.FirstMap
import com.example.splitpayandroid.annotation.SecondMap
import com.example.splitpayandroid.intro.IntroActivity
import com.example.splitpayandroid.retrofit.RetrofitProvider
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.*
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
class Contributing{

    @Provides @IntoMap @StringKey("one")  @FirstMap
    fun one() = 1

    @Provides @IntoMap @StringKey("two") @SecondMap
    fun two() = 2

    @Provides @IntoMap @StringKey("one")  @Named("firstMap")
    fun oneNamed() = 1

    @Provides @IntoMap @StringKey("two") @Named("secondMap")
    fun twoNamed() = 2

    @Provides @IntoMap @StringKey("three")
    fun three() = 3

//    @Provides @IntoMap @StringKey("first")
//    fun threeProv() = Provider<Int>({ 3 })

    @Provides @Named("yo") fun yo() = 3
}

@Module
abstract class AbstractContributor{

//    @Binds @Named("yo")
//    abstract fun three(res: Int = 3): Int

}

@Component(modules = [RetrofitProvider::class, AppModule::class, AndroidInjectionModule::class, Contributing::class, AbstractContributor::class])
@Singleton
interface AppComponent: AndroidInjector<App>

class App: DaggerApplication(){
    override fun applicationInjector() = DaggerAppComponent.create()
}

@SuppressLint("Registered")
class App_: Application(), HasActivityInjector{

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> =
        androidInjector

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent_.create().inject(this)
    }

}

@Module
abstract class AppModule{

    @Suppress("unused")
    @ContributesAndroidInjector
    abstract fun contribute(): IntroActivity
}


@Component(modules = [RetrofitProvider::class, AppModule::class, AndroidInjectionModule::class, Contributing::class, AbstractContributor::class])
@Singleton
interface AppComponent_: AndroidInjector<App_>
{
//    fun inject(introActivity: IntroActivity)
}