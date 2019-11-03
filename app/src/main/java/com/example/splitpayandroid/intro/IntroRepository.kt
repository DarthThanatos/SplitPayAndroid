package com.example.splitpayandroid.intro

import android.content.Intent
import android.content.SharedPreferences
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import com.example.splitpayandroid.analytics.Analytics
import com.example.splitpayandroid.fingerprint.FingerPrint
import com.example.splitpayandroid.model.User
import com.example.splitpayandroid.retrofit.UsersService
import com.example.splitpayandroid.util.EMAIL
import com.example.splitpayandroid.util.USER_ID
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


class IntroRepository @Inject constructor (
    private val fingerprintManager: FingerprintManagerCompat,
    private val fingerPrint: FingerPrint,
    private val analytics: Analytics,
    private val usersService: UsersService,
    private val sharedPreferences: SharedPreferences
) {

    fun loggedIn() = auth.currentUser != null

    fun saveEmail(email: String) {
        println("Saving user email: $email")
        sharedPreferences.edit().putString(EMAIL, email).apply()
    }

    fun saveUserId(id: Long){
        println("Saving user id: $id")
        sharedPreferences.edit().putLong(USER_ID, id).apply()
    }

    fun hasFingerprintHardware() = fingerprintManager.isHardwareDetected

    fun authenticateBiometrically(callback: CustomAuthenticationCallback.BiometricCallback){
        fingerprintManager.authenticate(
            fingerPrint.cryptoObject,
            0,
            CancellationSignal(),
            CustomAuthenticationCallback(callback),
            null
        )
    }

    private val auth = FirebaseAuth.getInstance()

    fun getSavedEmail(): String {
        println("current user: ${auth.currentUser?.displayName ?: "None"}")
        val email = sharedPreferences.getString(EMAIL, "")!!
        return email
    }

    fun createApiUser(email: String, name: String) =
        usersService.createUser(User(email = email, displayname = name))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun updateFirebaseUserName(name: String) {
        val user = auth.currentUser
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()
        user?.updateProfile(request)
    }

    fun logLogin(method: String){
        analytics.logLogin(method)
    }

    fun logout(){
        auth.signOut()
    }

    fun confirmEmail(email: String, intent: Intent, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        val emailLink = intent.data?.toString() ?: ""
        Timber.d(emailLink)
        if(auth.isSignInWithEmailLink(emailLink)){
            auth.signInWithEmailLink(email, emailLink).addOnCompleteListener(onComplete).addOnFailureListener(onFailure)
        }
    }

    fun createUserHaving(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener (onComplete).addOnFailureListener(onFailure)
    }

    fun deleteUser(userId: Long): Observable<Void> =
        usersService.deleteUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun login(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener (onComplete).addOnFailureListener(onFailure)
    }

    fun verifyEmail(email: String, onComplete: OnCompleteListener<Void>, onFailure: OnFailureListener){
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

}