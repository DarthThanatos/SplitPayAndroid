package com.example.splitpayandroid.retrofit

import com.example.splitpayandroid.model.User
import com.example.splitpayandroid.model.UsersList
import io.reactivex.Observable
import retrofit2.http.*

interface UsersService{

    @GET("/users/{userid}")
    fun getUser(@Path("userid") id: Long): Observable<User>

    @GET("/users/groups/{groupid}")
    fun getUsersInGroup(@Path("groupid") groupId: Long): Observable<UsersList>

    @POST("/users")
    fun createUser(@Body user: User): Observable<User>

    @DELETE("/users/{userId}")
    fun deleteUser(@Path("userId") id: Long): Observable<Void>

}