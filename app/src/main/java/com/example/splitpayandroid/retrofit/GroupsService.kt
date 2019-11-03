package com.example.splitpayandroid.retrofit

import com.example.splitpayandroid.model.GroupDto
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface GroupsService{

    @GET("/paygroups/user/{userid}")
    fun getUserPaygroups(@Path("userid") userId: Long): Observable<List<GroupDto>>

    @GET("/paygroups/participating-user/{userid}")
    fun getPaygroupsOfParticipating(@Path("userid") userId: Long): Observable<List<GroupDto>>

    @GET("/paygroups/all")
    fun getAllPaygroups(): Observable<List<GroupDto>>

}