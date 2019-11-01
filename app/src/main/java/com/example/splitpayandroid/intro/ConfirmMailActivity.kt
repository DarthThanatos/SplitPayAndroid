package com.example.splitpayandroid.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import com.example.splitpayandroid.groups.GroupsActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import dagger.android.AndroidInjection

import kotlinx.android.synthetic.main.activity_confirm_mail.*
import kotlinx.android.synthetic.main.content_confirm_mail.*
import javax.inject.Inject

// Normally you login to your email you typed in IntroActivity and click so that it redirects you to this activity. You can also simulate redirecting by (instead of clicking the link sent to the mailbox) copying the link sent to you and pasting it after -d switch below:
// adb [-s <device_ip>] shell am start -W -a android.intent.action.View -d <the long link>
// In order for the link to work, you need to add regex ^https://splitpay-e9233.firebaseapp.com/.* to your dynamic link whitelist (no $ sign at the end of the regex!)

class ConfirmMailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var introVM: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_mail)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        initVM()
    }

    private fun initVM(){
        introVM = ViewModelProviders.of(this, viewModelFactory).get(IntroViewModel::class.java)
    }

    @Suppress("UNUSED_PARAMETER")
    fun verify(view: View){
        val email = verifyEmailInput.text.toString()
        Toast.makeText(this, "Veryifying mail: $email", Toast.LENGTH_LONG).show()
        introVM.confirmEmailOnly(email, intent, onComplete(), onFailure())
    }

    private fun onComplete() = OnCompleteListener<AuthResult>{
        val msg = if(!it.isSuccessful) "Email verification failed" else "verified mail of the user ${it.result?.user?.email}"
        Toast.makeText(this@ConfirmMailActivity, msg, Toast.LENGTH_LONG).show()
        if(it.isSuccessful){
            startActivity(Intent(this, GroupsActivity::class.java))
            finish()
        }
    }

    private fun onFailure() = OnFailureListener{
        Toast.makeText(this, "Error while verifying: ${it.message}", Toast.LENGTH_LONG).show()
    }
}

