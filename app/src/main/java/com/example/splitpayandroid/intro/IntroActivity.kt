package com.example.splitpayandroid.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import com.example.splitpayandroid.groups.GroupsActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject
import androidx.fragment.app.Fragment
import com.example.splitpayandroid.util.SHARE_LINK_SHORT
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.content_main.registerNameInput
import kotlinx.android.synthetic.main.content_main.registerPasswordInput
import kotlinx.android.synthetic.main.fragment_register.*


class IntroActivity : DaggerAppCompatActivity() {


    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        setSupportActionBar(toolbar)
        initViewModel()
    }

    private fun initViewModel(){
        createAndAssignViewModel()
        subscribeShowRegisterDialog()
        subscribeSavedEmail()
        subscribeOnToastMessages()
        subscribeShowBiometricDialog()
        subscribeShowGroupsActivity()
        subscribeShowEmailOnlyRegistration()
        subscribeProgressBar()
        viewModel.onCheckLoggedIn()
    }

    private fun createAndAssignViewModel(){
        viewModel = ViewModelProviders.of(
            this, viewModelFactory
        ).get(IntroViewModel::class.java)
    }

    private fun subscribeShowRegisterDialog(){
        viewModel.showRegisterDialog.observe(this, Observer { shouldShow ->
            if(shouldShow){
                showFragment {
                    RegisterFragment.newInstance()
                }
            }
        })
    }

    private fun subscribeProgressBar(){
        viewModel.showProgressBar.observe(this, Observer {isVisible ->
            introProgressBar.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
        })
    }

    private fun subscribeSavedEmail(){
        viewModel.readSavedEmail()
        viewModel.savedEmail.observe(this, Observer { email ->
            email?.apply {
                showFragment {
                    LoginFragment.newInstance(email = email)
                }
            }
        })
    }

    private fun subscribeOnToastMessages(){
        viewModel.toastMessagesLiveData.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })
    }

    private fun subscribeShowBiometricDialog(){
        viewModel.showBiometricDialog.observe(this, Observer { shouldShow ->
            if(shouldShow){
                val biometricDialogFragment = BiometricDialogFragment.newInstance()
                biometricDialogFragment.show(supportFragmentManager, "biometric_dialog")
            }
        })
    }

    private fun subscribeShowGroupsActivity(){
        viewModel.showGroupsScreen.observe(this, Observer { shouldShow ->
            if(shouldShow){
                startActivity(Intent(this, GroupsActivity::class.java))
                finish()
            }
        })
    }

    private fun subscribeShowEmailOnlyRegistration() {
        viewModel.showEmailOnlyRegisterDialog.observe(this, Observer {shouldShow ->
            if(shouldShow){
                showFragment { EmailOnlyRegistrationFragment.newInstance() }
            }
        })
    }

    @Suppress("UNUSED_PARAMETER")
    fun shareClicked(view: View){
        startActivity(Intent.createChooser(shareIntent, "Share Link"))
    }

    private val shareIntent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, SHARE_LINK_SHORT)
        }

    @Suppress("UNUSED_PARAMETER")
    fun signOut(view: View){
        viewModel.logout()
    }


    @Suppress("UNUSED_PARAMETER")
    fun onRegister(view: View){
        val name = registerNameInput.text.toString()
        val password = registerPasswordInput.text.toString()
        val email = registerEmailInput1.text.toString()
        viewModel.onRegister(name, password, email)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLogin(view: View){
        val password = registerPasswordInput.text.toString()
        val email = registerEmailInput1.text.toString()
        viewModel.onLogin(email, password)
    }

    @Suppress("UNUSED_PARAMETER")
    fun verifyEmail(view: View){
        val email = registerEmailInput.text.toString()
        viewModel.onVerifyEmail(email)
    }

    private fun showFragment(fragmentFactory: ()->Fragment){
        val newFragment = fragmentFactory()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.intro_container, newFragment)
        transaction.commit()
    }

}
