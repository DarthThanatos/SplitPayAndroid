package com.example.splitpayandroid.groups

import android.content.Intent
import com.example.splitpayandroid.analytics.Analytics
import com.example.splitpayandroid.retrofit.ConnectivityManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import javax.inject.Inject

//interface specifying the contract for work that requires tedious delegation from VM perspective
interface GroupRepositoryWork{
    fun getLogged(): FirebaseUser?
    fun isNetworkConnected(): Boolean
    fun tryGetDynamicLinkIfCorrectIntent(intent: Intent, onSuccess: OnSuccessListener<PendingDynamicLinkData>, onFailure: OnFailureListener)
    fun logYolo()
}

class GroupsRepository @Inject constructor(private val analytics: Analytics, private val connectivityManager: ConnectivityManager): GroupRepositoryWork{

    private val dynamicLinks = FirebaseDynamicLinks.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun getLogged() = auth.currentUser

    override fun tryGetDynamicLinkIfCorrectIntent(intent: Intent, onSuccess: OnSuccessListener<PendingDynamicLinkData>, onFailure: OnFailureListener) {
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
}