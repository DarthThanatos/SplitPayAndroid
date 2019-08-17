package com.example.splitpayandroid.di.snippet

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import com.example.splitpayandroid.annotation.FirstMap
import com.example.splitpayandroid.annotation.MessageType
import com.example.splitpayandroid.annotation.ProviderKey
import com.example.splitpayandroid.annotation.SecondMap
import com.example.splitpayandroid.di.module.ActivityModule
import com.example.splitpayandroid.di.module.AppModule
import com.example.splitpayandroid.di.module.IntroPresenterModule
import com.example.splitpayandroid.retrofit.RetrofitProvider
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton


@Module
class Contributing{

    @Provides
    @IntoMap
    @StringKey("one")  @FirstMap
    fun one() = 1

    @Provides
    @IntoMap
    @StringKey("two") @SecondMap
    fun two() = 2

    @Provides
    @IntoMap
    @StringKey("one")  @Named("firstMap")
    fun oneNamed() = 1

    @Provides
    @IntoMap
    @StringKey("two") @Named("secondMap")
    fun twoNamed() = 2

    @Provides
    @IntoMap
    @StringKey("three")
    fun three(): Int = 3

    @Provides
    @Named("yo") fun yo() = 3
}

interface Clazz

data class ClazzImpl @Inject constructor(val yolo: Int): Clazz

@Suppress("unused")
@Module
abstract class AbstractContributor{

    @Module
    companion object{

        @Provides
        @JvmStatic
        fun provideInt() = 5

        // when uncommented, dagger prioritises 4 provided by the function below over 3 provided in the function above
        // since the factory method that returns 4 is qualified

//        @Provides
//        @JvmStatic
//        @Named("yo1")
//        fun provideFour() = 4
    }

    @Binds
    @Named("yo1")
    abstract fun three(res: Int): Int //return type can be Number only when qualified factory method is available in the module


    @Binds
    @IntoMap
    @StringKey("yoloy")
    abstract fun bindClazzIntoMap(clazz: ClazzImpl): Clazz

    @Binds
    @IntoMap
    @StringKey("yoloy1")
    @FirstMap
    abstract fun bindClazz1IntoMap(clazz: ClazzImpl): Clazz

    @Binds
    abstract fun bindClazz(clazz: ClazzImpl): Clazz

    @Binds
    @IntoMap
    @StringKey("four")
    abstract fun fourMap(i: Int): Int

    @Binds
    @IntoMap
    @ProviderKey(MessageType.IMAGE_MESSAGE_TYPE)
    abstract fun mapCustomKey(i: Int): Int
}



@Suppress("unused")
@Module
abstract class App_ContextModule{

    @Binds
    abstract fun bindContext(app: App_): Context
}


@Component(modules = [
    ActivityModule::class, RetrofitProvider::class, AppModule::class,
    AndroidInjectionModule::class, Contributing::class, AbstractContributor::class,
    IntroPresenterModule::class,  App_ContextModule::class
])
@Singleton
interface AppComponent_: AndroidInjector<App_>
{
//    fun inject(introActivity: IntroActivity)
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App_>()
}

@SuppressLint("Registered")
class App_: Application(), HasActivityInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> =
        androidInjector

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent_.builder().create(this).inject(this)
    }

}


class ConstructorInj @Inject constructor(
    private val mapOnePrim: Map<String, Int>,
    private val mapOnePrimProv: Map<String, @JvmSuppressWildcards Provider<Int>>,
    private val mapTwoPrim: Map<String,  @JvmSuppressWildcards Clazz>,
    private val mapTwoPrimProv: Map<String,  @JvmSuppressWildcards Provider<Clazz>>,
    @FirstMap private val firstMapTwoPrim: Map<String,  @JvmSuppressWildcards Clazz>,
    @FirstMap private val firstMapTwoPrimProv: Map<String,  @JvmSuppressWildcards Provider<Clazz>>,
    private val clazzProv: Provider<ClazzImpl>,
    @Named("yo1") private val yo: Int,
    @Named("yo1") private val provInt: Provider<Int>
){
    // if you inject a primitive with a qualifier
    // @set:[Inject Named("logoIcon")] var logoIcon: Int = 0
    @field:[Inject FirstMap] // because of qualifying annotation
    lateinit var mapOne: Map<String, Int>

    @field:[Inject Named("secondMap")] // because of qualifying annotation
    lateinit var mapTwo: Map<String, Int>

    @Inject
    lateinit var clazzMap: Map<String, @JvmSuppressWildcards Clazz>

    @field:[Inject ProviderKey(MessageType.IMAGE_MESSAGE_TYPE)]
    lateinit var customKeyIntMap: Map<MessageType, @JvmSuppressWildcards Provider<Int>>

    fun snippet(){
        println("IntroVM Fac, $yo, $provInt, ${clazzProv.get()}")
        println("mapOne: $mapOne")
        println("mapTwo: $mapTwo")
        println("mapOnePrim: $mapOnePrim")
        println("mapOnePrimProv: $mapOnePrimProv")
        println("mapTwoPrim: $mapTwoPrim")
        println("mapToPrimProv: $mapTwoPrimProv")
        println("firstMapTwoPrim: $firstMapTwoPrim")
        println("firstMapTwoPrimProv: $firstMapTwoPrimProv")
        println("clazzMapField: $clazzMap")
        println("customIntMap: $customKeyIntMap")
    }
}