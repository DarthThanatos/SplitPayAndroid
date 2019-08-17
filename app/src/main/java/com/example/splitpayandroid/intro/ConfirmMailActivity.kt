package com.example.splitpayandroid.intro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.VMFactory
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import dagger.android.AndroidInjection

import kotlinx.android.synthetic.main.activity_confirm_mail.*
import kotlinx.android.synthetic.main.content_confirm_mail.*
import javax.inject.Inject

class ConfirmMailActivity : AppCompatActivity() {

    @Inject
    lateinit var vmFactory: VMFactory

    private lateinit var introVM: IntroVM

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
        println("confirming")
        introVM = ViewModelProviders.of(this, vmFactory).get(IntroVM::class.java)
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
//            finish()
        }
    }

    private fun onFailure() = OnFailureListener{
        Toast.makeText(this, "Error while verifying: ${it.message}", Toast.LENGTH_LONG).show()
    }
}

