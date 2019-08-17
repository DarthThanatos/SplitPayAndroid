package com.example.splitpayandroid.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.VMFactory
import com.example.splitpayandroid.di.snippet.ConstructorInj
import com.example.splitpayandroid.groups.GroupsActivity
import com.example.splitpayandroid.model.UsersList
import com.example.splitpayandroid.retrofit.UsersService
import com.example.splitpayandroid.util.SHARE_LINK_SHORT
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject

interface IntroView

class SnackListener(private val activity: Activity): View.OnClickListener {

    override fun onClick(v: View?) {
        goToGroups()
    }

    private fun goToGroups(){
        val intent = Intent(activity, GroupsActivity::class.java)
        activity.startActivity(intent)
    }
}

class IntroActivity : DaggerAppCompatActivity(), IntroView, CustomAuthenticationCallback.BiometricCallback {

    @Inject lateinit var constructorInj: ConstructorInj

    @Inject lateinit var usersService: UsersService

    @Inject lateinit var vmFactory: VMFactory

    @Inject lateinit var presenter: IntroPresenter

    private lateinit var introVm: IntroVM

    override fun onCreate(savedInstanceState: Bundle?) {
        injectSelf()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", SnackListener(this)).show()
        }
        initPresenter(usersService)
        initVM()
    }

    private fun injectSelf(){
        AndroidInjection.inject(this)
        constructorInj.snippet()
    }

    private fun initPresenter(usersService: UsersService){
        presenter.attachView(this)
        presenter.fetchUsers(usersService)
    }

    private fun initVM(){
        introVm = ViewModelProviders.of(this, vmFactory).get(IntroVM::class.java)
        introVm.usersList.observe(this, Observer<UsersList> { t ->
            println("returned users list in introVm:")
            println(t)
        })
        introVm.biometricUnlocked.observe(this, Observer<Boolean> { unlocked ->
            if(unlocked){
                bioRegister()
            }
        })
        introVm.loadGroups()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRegister(view: View){
        stdAuthOperation(IntroVM::create, onRegisteredListener(), onFailureListener("Registration"))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLogin(view: View){
        if(introVm.getLogged() != null){
            goToGroupsActivity()
        }
        else {
            stdAuthOperation(IntroVM::login, onLoginListener(), onFailureListener(("Login")))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun verifyEmail(view: View){
        introVm.emailOnlyRegistration(emailInput.text.toString(), onEmailOnlyFinishedListener(), onFailureListener("Email verification"))
    }


    private fun onEmailOnlyFinishedListener() =
        OnCompleteListener<AuthResult> {
            val msg = if(!it.isSuccessful) "Email verification failed" else "Email verification: authenticated user ${it.result?.user?.email}"
            Toast.makeText(this@IntroActivity, msg, Toast.LENGTH_LONG).show()
            if(it.isSuccessful){
                goToGroupsActivity()
            }
        }

    private fun stdAuthOperation(
        authOperation: IntroVM.(String, String, OnCompleteListener<AuthResult>, OnFailureListener) -> Unit,
        onSuccess: OnCompleteListener<AuthResult>,
        onFailure: OnFailureListener
    ){
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        authOperation(introVm, email, password, onSuccess, onFailure)
    }

    private fun onRegisteredListener() =
        OnCompleteListener<AuthResult> { task ->
            val msg = if(!task.isSuccessful) "Registration failed" else "Authenticated user ${task.result?.user}"
            Toast.makeText(this@IntroActivity, msg, Toast.LENGTH_LONG).show()
            if(task.isSuccessful){
                introVm.logLogin("app")
                introVm.biometricUnlocked.value = true
            }
        }

    private fun onFailureListener(operation: String) = OnFailureListener {
        e -> Toast.makeText(this@IntroActivity, "$operation failed, what went wrong: ${e.message}", Toast.LENGTH_LONG).show()
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
        introVm.getFingerPrint(this) { fp ->
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
        introVm.authenticationStatus.value = msg
        Toast.makeText(this, "Hint: $msg", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(this, "Authentication has failed", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationError(msg: String) {
        introVm.authenticationStatus.value = msg
        Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
        introVm.authenticationStatus.value = "success"
        Toast.makeText(this, "Successfully authenticated", Toast.LENGTH_LONG).show()

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
        introVm.signout()
    }
}
