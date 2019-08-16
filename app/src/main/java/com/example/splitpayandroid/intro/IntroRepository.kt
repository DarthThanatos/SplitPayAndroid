package com.example.splitpayandroid.intro

import com.example.splitpayandroid.analytics.Analytics
import com.example.splitpayandroid.retrofit.UsersService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class IntroRepository @Inject constructor (private val usersService: UsersService, private val analytics: Analytics){

    private val auth = FirebaseAuth.getInstance()

    fun logLogin(method: String){
        analytics.logLogin(method)
    }

    fun create(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener (onComplete).addOnFailureListener(onFailure)
    }

    fun getUserGroups() =
        usersService.getUsersInGroup(3L)

}