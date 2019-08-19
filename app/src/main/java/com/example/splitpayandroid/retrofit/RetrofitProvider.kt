package com.example.splitpayandroid.retrofit

import com.example.splitpayandroid.BuildConfig
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RetrofitProvider{

    private val BASE_URL = BuildConfig.API_URL
    private var usersService: UsersService
    private var groupsService: GroupsService

    init{
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
        usersService = retrofit.create(UsersService::class.java)
        groupsService = retrofit.create(GroupsService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService() = usersService

    @Provides
    @Singleton
    fun provideGroupService() = groupsService

}