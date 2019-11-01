package com.example.splitpayandroid.intro

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import com.example.splitpayandroid.groups.GroupsActivity
import com.example.splitpayandroid.util.SHARE_LINK_SHORT
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject
import androidx.fragment.app.Fragment


interface IntroView


class IntroActivity : DaggerAppCompatActivity(), IntroView, CustomAuthenticationCallback.BiometricCallback {


    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var introViewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        setSupportActionBar(toolbar)
        initViewModel()
    }

    private fun initViewModel(){
        introViewModel = ViewModelProviders.of(this, viewModelFactory).get(IntroViewModel::class.java)
        introViewModel.biometricUnlocked.observe(this, Observer<Boolean> { unlocked ->
            if(unlocked){
                bioRegister()
            }
        })
        introViewModel.readSavedCredentials()
        introViewModel.savedCredentials.observe(this, Observer { credentials ->
            if(credentials.first != ""){
                showLoginScreen(credentials.first, credentials.second)
            }
            else{
                showRegisterScreen()
            }
        })
    }

    private fun showFragment(fragmentFactory: ()->Fragment){
        val newFragment = fragmentFactory()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.intro_container, newFragment)
        transaction.commit()
    }

    private fun showRegisterScreen(){
        showFragment {RegisterFragment.newInstance()}
    }

    private fun showLoginScreen(email: String, name: String){
        showFragment {LoginFragment.newInstance(email = email, name = name)}
    }

    private fun onEmailOnlyFinishedListener() =
        OnCompleteListener<Void> {
            val msg = if(!it.isSuccessful) "Email verification failed" else "Email verification: check your mailbox and follow the link in the noreply message. "
            Toast.makeText(this@IntroActivity, msg, Toast.LENGTH_LONG).show()
            if(it.isSuccessful){
                finish()
            }
        }

    private fun stdAuthOperation(
        authOperation: IntroViewModel.(String, String, OnCompleteListener<AuthResult>, OnFailureListener) -> Unit,
        onSuccess: OnCompleteListener<AuthResult>,
        onFailure: OnFailureListener
    ){
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        authOperation(introViewModel, email, password, onSuccess, onFailure)
    }

    private fun onRegisteredListener() =
        OnCompleteListener<AuthResult> { task ->
            val msg = if(!task.isSuccessful) "Registration failed" else "Authenticated user ${task.result?.user}"
            Toast.makeText(this@IntroActivity, msg, Toast.LENGTH_LONG).show()
            if(task.isSuccessful){
                introViewModel.updateUserName(nameInput.text.toString())
                introViewModel.logLogin("app")
                introViewModel.biometricUnlocked.value = true
            }
        }

    private fun onFailureListener(operation: String) = OnFailureListener {
        e ->
            val msg = "$operation failed, what went wrong: ${e.message}"
            Toast.makeText(this@IntroActivity, msg, Toast.LENGTH_LONG).show()
            e.printStackTrace()
    }

    private fun onLoginListener() =
        OnCompleteListener<AuthResult> {
            val msg = if(!it.isSuccessful) "Authentication failed" else "Authenticated user ${it.result?.user}"
            Toast.makeText(this@IntroActivity, msg, Toast.LENGTH_LONG).show()
            if(it.isSuccessful){
                goToGroupsActivity()
            }
        }

    private fun goToGroupsActivity(){
        val intent = Intent(this, GroupsActivity::class.java)
        startActivity(intent)
    }

    private fun bioRegister(){
        val fingerprintManagerCompat = FingerprintManagerCompat.from(this)
        if(!fingerprintManagerCompat.isHardwareDetected){
            Toast.makeText(this, "No biometric hardware detected", Toast.LENGTH_LONG).show()
        }
        compatRegister(fingerprintManagerCompat)
        showBioRegisterDialog()
    }

    private fun compatRegister(fingerprintManagerCompat: FingerprintManagerCompat){
        introViewModel.getFingerPrint(this) { fp ->
            fingerprintManagerCompat.authenticate(
                fp.cryptoObject,
                0,
                CancellationSignal(),
                CustomAuthenticationCallback(this),
                null
            )
        }
    }

    private fun showBioRegisterDialog(){
        val biometricDialogFragment = BiometricDialogFragment.newInstance()
        biometricDialogFragment.show(supportFragmentManager, "biometric_dialog")
    }

    override fun onAuthenticationHelp(msg: String) {
        introViewModel.authenticationStatus.value = msg
        Toast.makeText(this, "Hint: $msg", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(this, "Authentication has failed", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationError(msg: String) {
        introViewModel.authenticationStatus.value = msg
        Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
        introViewModel.authenticationStatus.value = "success"
        Toast.makeText(this, "Successfully authenticated", Toast.LENGTH_LONG).show()
    }

    private fun loginWithEmailPassword(){
        val password = passwordInput.text.toString()
        val email = emailInput.text.toString()
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(email)){
            Toast.makeText(this, "Both password and email values should be filled", Toast.LENGTH_LONG).show()
        }
        else{
            stdAuthOperation(IntroViewModel::login, onLoginListener(), onFailureListener(("Login")))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun shareClicked(view: View){

        val link = SHARE_LINK_SHORT

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, link)

        startActivity(Intent.createChooser(intent, "Share Link"))
    }

    @Suppress("UNUSED_PARAMETER")
    fun signOut(view: View){
        introViewModel.logout()
        Toast.makeText(this, "Logout successful", Toast.LENGTH_LONG).show()
    }


    @Suppress("UNUSED_PARAMETER")
    fun onRegister(view: View){
        val name = nameInput.text.toString()
        val password = passwordInput.text.toString()
        val email = emailInput.text.toString()
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(email) || TextUtils.isEmpty(name)){
            Toast.makeText(this, "Name, password and email values should be filled", Toast.LENGTH_LONG).show()
        }
        else {
            stdAuthOperation(IntroViewModel::create, onRegisteredListener(), onFailureListener("Registration"))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLogin(view: View){
        if(introViewModel.getLogged() != null){
            goToGroupsActivity()
        }
        else {
            loginWithEmailPassword()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun verifyEmail(view: View){
        val email = emailInput.text.toString()
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email values should be filled", Toast.LENGTH_LONG).show()
        }
        else {
            introViewModel.emailOnlyRegistration(
                emailInput.text.toString(),
                onEmailOnlyFinishedListener(),
                onFailureListener("Email verification")
            )
        }
    }

}
