package com.example.splitpayandroid.groups

import android.content.SharedPreferences
import com.example.splitpayandroid.database.dao.GroupsDao
import com.example.splitpayandroid.database.entity.GroupEntity
import com.example.splitpayandroid.model.GroupDto
import com.example.splitpayandroid.model.User
import com.example.splitpayandroid.retrofit.ConnectivityManager
import com.example.splitpayandroid.retrofit.GroupsService
import com.example.splitpayandroid.retrofit.UsersService
import com.example.splitpayandroid.util.USER_ID
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class GroupsRepository @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val groupsService: GroupsService,
    private val usersService: UsersService,
    private val groupsDao: GroupsDao,
    private val sharedPreferences: SharedPreferences
){

    fun getSavedUserId(): Long {
        val userId = sharedPreferences.getLong(USER_ID, -1)
        return userId
    }

    fun getUser(id: Long): Observable<User>{
        return usersService
            .getUser(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun isNetworkConnected() = connectivityManager.isNetworkAvailable()

    fun getAllGroups(): Observable<List<GroupDto>> {
        return groupsService.getAllPaygroups().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getGroupsOfParticipating(): Observable<List<GroupDto>> {
        val userId = getSavedUserId()
        return groupsService.getPaygroupsOfParticipating(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getUserGroups(): Observable<List<GroupDto>> {
        val userId = getSavedUserId()
        val source =
            if(connectivityManager.isNetworkAvailable())
                getUserGroupsFromApi(userId).flatMap { logAndPass(it, "api-only") }
                    .startWith(getUserGroupsFromDb().flatMap { logAndPass(it, "db-only") })
                    .flatMap { logAndPass(it, "api-with-db") }
            else getUserGroupsFromDb().flatMap { logAndPass(it, "db-only") }
        return source.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun logAndPass(groups: List<GroupDto>, tag: String): Observable<List<GroupDto>>{
        groups.forEach { Timber.d("logging groups, tag: $tag, collection: $it") }
        return Observable.just(groups)
    }

    private fun getUserGroupsFromApi(userId: Long): Observable<List<GroupDto>> =
        groupsService
            .getUserPaygroups(userId)
            .apply{Timber.d("Got groups from api")}
            .flatMap { logAndPass(it, "api-before-saving")}
            .map { saveUserGroupsInDb(it) }

    private fun getUserGroupsFromDb(): Observable<List<GroupDto>> =
        groupsDao.getGroups().map{
            it.map{ GroupDto(it.groupId, it.displayName, it.isActive) }
        }.apply{Timber.d("Got Groups from db: $this")}.toObservable()

    private fun saveUserGroupsInDb(groups: List<GroupDto>): List<GroupDto>{
        groupsDao.deleteUserGroups()
        saveUserGroups(groups)
        Timber.d("saving groups in db: $groups")
        return groups
    }

    private fun saveUserGroups(groups: List<GroupDto>){
        groups.map{
            GroupEntity(it.groupid, it.displayname, it.isactive)
        }.apply{groupsDao.insertUserGroups(this) }
    }
}