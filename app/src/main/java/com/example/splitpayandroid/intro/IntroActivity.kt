package com.example.splitpayandroid.intro

import android.app.Activity
import android.content.Context
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
import com.example.splitpayandroid.dagger_snippet.ConstructorInj
import com.example.splitpayandroid.groups.GroupsActivity
import com.example.splitpayandroid.model.UsersList
import com.example.splitpayandroid.retrofit.UsersService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception
import javax.inject.Inject


interface IntroView{

}

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

    private lateinit var vm: VM

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
        vm = ViewModelProviders.of(this, vmFactory).get(VM::class.java)
        vm.usersList.observe(this, Observer<UsersList> { t ->
            println("returned users list in vm:")
            println(t)
        })
        vm.biometricUnlocked.observe(this, Observer<Boolean> {unlocked ->
            if(unlocked){
                bioAuth()
            }
        })
        vm.loadGroups()
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
    fun onAuthenticate(view: View){
        stdAuth()
    }

    private fun stdAuth(){
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        vm.create(email, password, onAuthenticatedListener(), onFailureListener())
    }

    private fun onAuthenticatedListener() = object: OnCompleteListener<AuthResult>{
        override fun onComplete(task: Task<AuthResult>) {
            val msg = if(!task.isSuccessful) "Authentication failed" else "Authenticated user ${task.result?.user}"
            Toast.makeText(this@IntroActivity, msg, Toast.LENGTH_LONG).show()
            if(task.isSuccessful){
                vm.logLogin("app")
                vm.biometricUnlocked.value = true
            }
        }
    }

    private fun onFailureListener() = object: OnFailureListener{
        override fun onFailure(e: Exception) {
            Toast.makeText(this@IntroActivity, "Authentication failed, what went wrong: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun bioAuth(){
        val fingerprintManagerCompat = FingerprintManagerCompat.from(this)
        if(!fingerprintManagerCompat.isHardwareDetected){
            Toast.makeText(this, "No biometric hardware detected", Toast.LENGTH_LONG).show()
//            return
        }
        compatAuth(fingerprintManagerCompat)
        showAuthDialog()
    }

    private fun compatAuth(fingerprintManagerCompat: FingerprintManagerCompat){
        vm.getFingerPrint(this) { fp ->
            fingerprintManagerCompat.authenticate(
                fp.cryptoObject,
                0,
                CancellationSignal(),
                CustomAuthenticationCallback(this),
                null
            )
        }
    }

    private fun showAuthDialog(){
        val biometricDialogFragment = BiometricDialogFragment.newInstance()
        biometricDialogFragment.show(supportFragmentManager, "biometric_dialog")
    }

    override fun onAuthenticationHelp(msg: String) {
        vm.authenticationStatus.value = msg
        Toast.makeText(this, "Hint: $msg", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(this, "Authentication has failed", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationError(msg: String) {
        vm.authenticationStatus.value = msg
        Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
        vm.authenticationStatus.value = "success"
        Toast.makeText(this, "Successfully authenticated", Toast.LENGTH_LONG).show()

    }
}
