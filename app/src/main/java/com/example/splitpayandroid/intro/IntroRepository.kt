package com.example.splitpayandroid.intro

import android.content.Intent
import android.content.SharedPreferences
import com.example.splitpayandroid.analytics.Analytics
import com.example.splitpayandroid.model.User
import com.example.splitpayandroid.model.UsersList
import com.example.splitpayandroid.retrofit.UsersService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

interface IntroRepositoryWork{
    fun getLogged(): FirebaseUser?
    fun logLogin(method: String)
    fun logout()
    fun confirmEmailOnly(
        email: String,
        intent: Intent,
        onComplete: OnCompleteListener<AuthResult>,
        onFailure: OnFailureListener
    )

    fun create(
        email: String,
        password: String,
        onComplete: OnCompleteListener<AuthResult>,
        onFailure: OnFailureListener
    )

    fun login(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener)
    fun emailOnlyRegistration(email: String, onComplete: OnCompleteListener<Void>, onFailure: OnFailureListener)

    fun updateUserName(name: String)
    fun saveCredentials(name: String, email: String)
}

class IntroRepository @Inject constructor (private val usersService: UsersService, private val analytics: Analytics, private val sharedPreferences: SharedPreferences): IntroRepositoryWork {

    override fun saveCredentials(name: String, email: String) {
        sharedPreferences.edit().putString(EMAIL, email).putString(NAME, name).apply()
    }

    private val auth = FirebaseAuth.getInstance()

    fun getSavedCredentials() :Pair<String, String> {
        val email = sharedPreferences.getString(EMAIL, "")!!
        val name = sharedPreferences.getString(NAME, "")!!
        return Pair(first = email, second = name)
    }

    override fun getLogged() = auth.currentUser

    override fun updateUserName(name: String) {
        val user = getLogged()
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()
        user?.updateProfile(request)
    }

    override fun logLogin(method: String){
        analytics.logLogin(method)
    }

    override fun logout(){
        auth.signOut()
    }

    override fun confirmEmailOnly(email: String, intent: Intent, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        val emailLink = intent.data?.toString() ?: ""
        Timber.d(emailLink)
        if(auth.isSignInWithEmailLink(emailLink)){
            auth.signInWithEmailLink(email, emailLink).addOnCompleteListener(onComplete).addOnFailureListener(onFailure)
        }
    }

    override fun create(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener (onComplete).addOnFailureListener(onFailure)
    }

    override fun login(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener (onComplete).addOnFailureListener(onFailure)
    }

    override fun emailOnlyRegistration(email: String, onComplete: OnCompleteListener<Void>, onFailure: OnFailureListener){
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

    companion object {
        private const val EMAIL = "email"
        private const val NAME = "name"

    }
}