package com.example.splitpayandroid.intro

import android.content.Intent
import com.example.splitpayandroid.analytics.Analytics
import com.example.splitpayandroid.retrofit.UsersService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import javax.inject.Inject

class IntroRepository @Inject constructor (private val usersService: UsersService, private val analytics: Analytics){

    private val auth = FirebaseAuth.getInstance()

    fun getLogged() = auth.currentUser

    fun logLogin(method: String){
        analytics.logLogin(method)
    }

    fun logout(){
        auth.signOut()
    }

    fun confirmEmailOnly(email: String, intent: Intent, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        val emailLink = intent.data?.toString() ?: ""
        Timber.d(emailLink)
        if(auth.isSignInWithEmailLink(emailLink)){
            auth.signInWithEmailLink(email, emailLink).addOnCompleteListener(onComplete).addOnFailureListener(onFailure)
        }
    }

    fun create(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener (onComplete).addOnFailureListener(onFailure)
    }

    fun login(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener (onComplete).addOnFailureListener(onFailure)
    }

    fun emailOnlyRegistration(email: String, onComplete: OnCompleteListener<Void>, onFailure: OnFailureListener){
        val actionCodeSettings = ActionCodeSettings.newBuilder()

            // URL you want to redirect back to. The domain (www.example.com) for this
            // URL must be whitelisted in the Firebase Console.

            // Due to some bug, for now you cannot use any other link than that of a default authorised domain with prepended https scheme
            // (same goes for a "data" tag in intent-filter element of AndroidManifest.xml) and some following suffixes (like /emailSignInLink in the example below);
            // no path prefix allowed (I think... )

            // More info regarding the bug at: https://github.com/firebase/quickstart-android/issues/488
                
            .setUrl("https://splitpay-e9233.firebaseapp.com/emailSignInLink")
            .setHandleCodeInApp(true) // This must be true
            .setAndroidPackageName(
                "com.example.splitpayandroid",
                false, /* installIfNotAvailable */
                null /* minimumVersion, null == default -> current version */)
            .build()
        auth.sendSignInLinkToEmail(email, actionCodeSettings).addOnCompleteListener(onComplete).addOnFailureListener(onFailure)
    }

    fun getUserGroups() =
        usersService.getUsersInGroup(3L)

}