package com.example.splitpayandroid.intro

import android.content.Intent
import android.text.TextUtils
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.splitpayandroid.di.scope.ApplicationScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ApplicationScope
class IntroViewModel @Inject constructor (private val introRepository: IntroRepository):
    ViewModel(), CustomAuthenticationCallback.BiometricCallback {

    private val composite = CompositeDisposable()

    val toastMessagesLiveData = MutableLiveData<String>()
    val savedEmail = MutableLiveData<String?>()
    val showBiometricDialog = MutableLiveData<Boolean>().apply { value = false }
    val showRegisterDialog = MutableLiveData<Boolean>().apply { value = false }
    val showEmailOnlyRegisterDialog = MutableLiveData<Boolean>().apply { value = false }
    val showProgressBar = MutableLiveData<Boolean>().apply { value = false }

    val biometricAuthenticationStatus = MutableLiveData<String>()

    val showGroupsScreen = MutableLiveData<Boolean>().apply { value = false }
    val showIntroScreen = MutableLiveData<Boolean>().apply { value = false }

    fun onCheckLoggedIn(){
        val loggedIn = introRepository.loggedIn()
        if(loggedIn){
            showGroupsScreen.value = true
        }
    }

    fun readSavedEmail(){
        val email = introRepository.getSavedEmail();
        if(email != ""){
            savedEmail.value = email
        }
        else{
            showRegisterDialog.value = true
        }
    }

    fun initBiometricAuthentication(){
        if(!introRepository.hasFingerprintHardware()){
            toastMessagesLiveData.value = "No biometric hardware detected, proceeding to standard login"
            return
        }
        introRepository.authenticateBiometrically(this)
    }

    override fun onAuthenticationHelp(msg: String) {
        biometricAuthenticationStatus.value = msg
        toastMessagesLiveData.value = "Biometric authentication hint: $msg"
    }

    override fun onAuthenticationFailed() {
        toastMessagesLiveData.value = "Biometric authentication has failed"
    }

    override fun onAuthenticationError(msg: String) {
        biometricAuthenticationStatus.value = msg
        toastMessagesLiveData.value = "Error: $msg"
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
        biometricAuthenticationStatus.value = "success"
        toastMessagesLiveData.value = "Successful biometric authentication"
    }

    fun logout() {
        showProgressBar.value = true
        introRepository.logout()
        showProgressBar.value = false
        toastMessagesLiveData.value = "Logout successful"
    }

    fun onRegister(name: String, password: String, email: String) {
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(email) || TextUtils.isEmpty(name)){
            toastMessagesLiveData.value = "Name, password and email values should be filled"
        }
        else {
            showProgressBar.value = true
            introRepository.createUserHaving(
                email,
                password,
                firebaseRegistrationListener(name, email),
                onFirebaseAuthenticationFailureListener("Registration")
            )
        }
    }

    fun onLogin(email: String, password: String){
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(email)){
            toastMessagesLiveData.value = "Both password and email values should be filled"
        }
        else {
            showProgressBar.value = true
            introRepository.login(
                email,
                password,
                firebaseLoginListener(),
                onFirebaseAuthenticationFailureListener(("Login"))
            )
        }
    }

    fun onVerifyEmail(email: String){
        if(TextUtils.isEmpty(email)){
            toastMessagesLiveData.value = "Email value should be filled"
        }
        else {
            showProgressBar.value = true
            introRepository.verifyEmail(
                email,
                firebaseVerifyEmailListener(),
                onFirebaseAuthenticationFailureListener("Email verification")
            )
        }
    }

    fun finishVerifyingEmail(email: String, name: String, intent: Intent){
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email)){
            toastMessagesLiveData.value = "Both name and email values should be filled"
        }
        else{
            showProgressBar.value = true
            introRepository.confirmEmail(
                email,
                intent,
                firebaseFinishVerifyEmailListener(email, name),
                onFirebaseAuthenticationFailureListener("Confirming email")
            )
        }
    }

    private fun firebaseLoginListener() =
        OnCompleteListener<AuthResult> {
            toastMessagesLiveData.value =
                if(!it.isSuccessful) "Login failed"
                else "The user ${it.result?.user} has signed in"
            if(it.isSuccessful){
                showProgressBar.value = false
                showGroupsScreen.value = true
            }
        }

    private fun firebaseRegistrationListener(name: String, email: String) =
        OnCompleteListener<AuthResult> { task ->
            toastMessagesLiveData.value =
                if(!task.isSuccessful) "Registration failed"
                else "Authenticated user ${task.result?.user}"
            if(task.isSuccessful){
                introRepository.updateUserName(name)
                introRepository.logLogin("app")
                introRepository.saveEmail(email)
                showProgressBar.value = false
                showGroupsScreen.value = true
            }
        }

    private fun firebaseVerifyEmailListener() =
        OnCompleteListener<Void> {
            toastMessagesLiveData.value =
                if(!it.isSuccessful) "Email verification failed"
                else "Email verification: check your mailbox and follow the link in the noreply message."
            showProgressBar.value = false
        }

    private fun firebaseFinishVerifyEmailListener(email: String, name: String) = OnCompleteListener<AuthResult>{
        toastMessagesLiveData.value =
            if(!it.isSuccessful) "Email verification failed"
            else "Verified mail of the user ${it.result?.user?.email}"
        if(it.isSuccessful){
            introRepository.saveEmail(email)
            introRepository.updateUserName(name)
            showProgressBar.value = false
            showIntroScreen.value = true
        }
    }

    fun moveToRegister() {
        showRegisterDialog.value = true
    }

    fun moveToRegisterEmailOnly() {
        showEmailOnlyRegisterDialog.value = true
    }

    private fun onFirebaseAuthenticationFailureListener(operation: String) = OnFailureListener { e ->
        toastMessagesLiveData.value = "$operation failed, what went wrong: ${e.message}"
        showProgressBar.value = false
        e.printStackTrace()
    }

    override fun onCleared() {
        composite.dispose()
        super.onCleared()
    }


}
