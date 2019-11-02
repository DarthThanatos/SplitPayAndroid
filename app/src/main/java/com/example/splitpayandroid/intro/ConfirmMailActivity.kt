package com.example.splitpayandroid.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import dagger.android.AndroidInjection

import kotlinx.android.synthetic.main.activity_confirm_mail.*
import javax.inject.Inject

// Normally you login to your email you typed in IntroActivity and click so that it redirects you to this activity. You can also simulate redirecting by (instead of clicking the link sent to the mailbox) copying the link sent to you and pasting it after -d switch below:
// adb [-s <device_ip>] shell am start -W -a android.intent.action.View -d <the long link>
// In order for the link to work, you need to add regex ^https://splitpay-e9233.firebaseapp.com/.* to your dynamic link whitelist (no $ sign at the end of the regex!)

class ConfirmMailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_mail)
        initVM()
    }

    private fun initVM(){
        createAndAssignViewModel()
        subscribeOnToastMessages()
        subscribeShowIntroActivity()
        subscribeProgressBar()
    }

    private fun createAndAssignViewModel(){
        viewModel = ViewModelProviders.of(
            this, viewModelFactory
        ).get(IntroViewModel::class.java)
    }

    private fun subscribeOnToastMessages(){
        viewModel.toastMessagesLiveData.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })
    }

    private fun subscribeShowIntroActivity(){
        viewModel.showIntroScreen.observe(this, Observer { shouldShow ->
            if(shouldShow){
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        })
    }

    private fun subscribeProgressBar(){
        viewModel.showProgressBar.observe(this, Observer {isVisible ->
            confirmEmailProgressBar.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
        })
    }


    @Suppress("UNUSED_PARAMETER")
    fun verify(view: View){
        val email = confirmEmailInput.text.toString()
        val name = confirmEmailNameInput.text.toString()
        viewModel.finishVerifyingEmail(email, name, intent)
    }

}

