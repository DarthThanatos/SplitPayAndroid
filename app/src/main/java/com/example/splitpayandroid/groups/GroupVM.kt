package com.example.splitpayandroid.groups

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.PendingDynamicLinkData

class GroupVM constructor(private val groupsRepository: GroupsRepository): ViewModel(){

    fun tryGetDynamicLinkIfCorrectIntent(intent: Intent, onSuccess: OnSuccessListener<PendingDynamicLinkData>, onFailure: OnFailureListener){
        groupsRepository.tryGetDynamicLinkIfCorrectIntent(intent, onSuccess, onFailure)
    }

    fun getLogged() = groupsRepository.getLogged()

    fun logYolo(){
        groupsRepository.logYolo()
    }

}