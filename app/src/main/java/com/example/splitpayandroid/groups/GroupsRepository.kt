package com.example.splitpayandroid.groups

import android.content.Intent
import com.example.splitpayandroid.analytics.Analytics
import com.example.splitpayandroid.database.dao.GroupsDao
import com.example.splitpayandroid.database.entity.GroupEntity
import com.example.splitpayandroid.model.GroupDto
import com.example.splitpayandroid.retrofit.ConnectivityManager
import com.example.splitpayandroid.retrofit.GroupsService
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

//interface specifying the contract for work that requires tedious delegation from VM perspective
interface GroupRepositoryWork{
    fun getLogged(): FirebaseUser?
    fun isNetworkConnected(): Boolean
    fun tryGetDynamicLinkIfCorrectIntent(
        intent: Intent,
        onSuccess: OnSuccessListener<PendingDynamicLinkData>,
        onFailure: OnFailureListener
    )
    fun logYolo()
}

class GroupsRepository @Inject constructor(
    private val analytics: Analytics,
    private val connectivityManager: ConnectivityManager,
    private val groupsService: GroupsService,
    private val groupsDao: GroupsDao
): GroupRepositoryWork{

    private val dynamicLinks = FirebaseDynamicLinks.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun getLogged() = auth.currentUser

    override fun tryGetDynamicLinkIfCorrectIntent(
        intent: Intent,
        onSuccess: OnSuccessListener<PendingDynamicLinkData>,
        onFailure: OnFailureListener
    ) {
        if(!Intent.ACTION_VIEW.equals(intent.getAction())){
            return
        }
        dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    override fun logYolo(){
        analytics.logYolo()
    }

    override fun isNetworkConnected() = connectivityManager.isNetworkAvailable()

    fun getUserGroups(): Observable<List<GroupDto>> {
        val source =
            if(connectivityManager.isNetworkAvailable())
                getUserGroupsFromApi().flatMap { logAndPass(it, "api-only") }
                    .startWith(getUserGroupsFromDb().flatMap { logAndPass(it, "db-only") })
                    .startWith(Observable.just(
                        listOf(GroupDto(999, "start", false))
                    ))
                    .flatMap { logAndPass(it, "api-with-db") }
            else getUserGroupsFromDb().flatMap { logAndPass(it, "db-only") }
        return source.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun logAndPass(groups: List<GroupDto>, tag: String): Observable<List<GroupDto>>{
        groups.forEach { Timber.d("logging groups, tag: $tag, collection: $it") }
        return Observable.just(groups)
    }

    private fun getUserGroupsFromApi(): Observable<List<GroupDto>> =
        groupsService
            .getUserPaygroups(20005L)
            .apply{Timber.d("Got groups from api")}
            .flatMap { logAndPass(it, "api-before-saving")}
//            .flatMap { saveUserGroupsInDb(it) }

    private fun test(){
        val g: Observable<List<GroupEntity>> = groupsDao.getGroups()
        val a: Observable<List<GroupDto>> = g.flatMap { Observable.just(it.map{GroupDto(it.groupId, it.displayName, it.isActive)}) }
        val b: Observable<List<GroupDto>> = g.map {it.map{GroupDto(it.groupId, it.displayName, it.isActive)}}
        val c: Observable<List<GroupDto>> = g.map{saveUserGroupsInDb_(it.map{GroupDto(it.groupId, it.displayName, it.isActive)})}
    }

    private fun saveUserGroupsInDb_(groups: List<GroupDto>): List<GroupDto> {
        groupsDao.deleteUserGroups()
        saveUserGroups(groups)
        return groups
    }

    private fun getUserGroupsFromDb(): Observable<List<GroupDto>> {
        return groupsDao.getGroups().map{
            it.map{ GroupDto(it.groupId, it.displayName, it.isActive) }
        }.apply{Timber.d("Got groups from db: $this")}
    }



    private fun saveUserGroupsInDb(groups: List<GroupDto>): Observable<List<GroupDto>> {
        groupsDao.deleteUserGroups()
        saveUserGroups(groups)
        Timber.d("saving groups in db: $groups")
        return Observable.just(groups)
    }

    private fun saveUserGroups(groups: List<GroupDto>){
        groups.map{
            GroupEntity(it.groupid, it.displayname, it.isactive)
        }.apply{groupsDao.insertUserGroups(this)}
    }

}