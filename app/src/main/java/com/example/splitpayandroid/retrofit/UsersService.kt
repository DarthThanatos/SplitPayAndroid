package com.example.splitpayandroid.retrofit

import com.example.splitpayandroid.model.User
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface UsersService{

    @GET("/users/{userid}")
    fun getUser(@Path("userid") id: Long): Observable<User>
}