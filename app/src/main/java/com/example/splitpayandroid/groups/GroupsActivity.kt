package com.example.splitpayandroid.groups

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.VMFactory
import com.example.splitpayandroid.di.snippet.ConstructorInj
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import dagger.android.AndroidInjection

import kotlinx.android.synthetic.main.activity_groups.*
import javax.inject.Inject

class GroupsActivity : AppCompatActivity() {

    @Inject
    lateinit var constructorInj: ConstructorInj

    @Inject
    lateinit var vmFactory: VMFactory

    private lateinit var vm: GroupVM

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        initVM()
        informWhoLogged()
        constructorInj.snippet()
    }

    private fun initVM(){
        vm = ViewModelProviders.of(this, vmFactory).get(GroupVM::class.java)
        vm.tryGetDynamicLinkIfCorrectIntent(intent, onDynamicLinkSuccess(), onDynamiLinkFailure())
        vm.logYolo()
    }

    private fun informWhoLogged(){
        Toast.makeText(this, "${vm.getLogged()?.email ?: "Unknown"} has just logged in", Toast.LENGTH_LONG).show()
    }

    private fun onDynamicLinkSuccess() = OnSuccessListener<PendingDynamicLinkData> {
        if(it != null){
            val deepLink = it.link
            Toast.makeText(this, "Opened deep Link: $deepLink", Toast.LENGTH_LONG).show()
        }
    }

    private fun onDynamiLinkFailure() = OnFailureListener{
        Toast.makeText(this, "Error getting opening dynamic link: ${it.message}", Toast.LENGTH_LONG).show()
    }

}
