package com.example.splitpayandroid.groups

import android.content.Intent
import com.example.splitpayandroid.analytics.Analytics
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import javax.inject.Inject

class GroupsRepository @Inject constructor(private val analytics: Analytics){

    private val dynamicLinks = FirebaseDynamicLinks.getInstance()

    private val auth = FirebaseAuth.getInstance()

    fun getLogged() = auth.currentUser

    fun tryGetDynamicLinkIfCorrectIntent(intent: Intent, onSuccess: OnSuccessListener<PendingDynamicLinkData>, onFailure: OnFailureListener) {
        if(!Intent.ACTION_VIEW.equals(intent.getAction())){
            return
        }
        dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun logYolo(){
        analytics.logYolo()
    }

}