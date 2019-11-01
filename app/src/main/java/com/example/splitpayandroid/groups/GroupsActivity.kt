package com.example.splitpayandroid.groups

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import dagger.android.AndroidInjection

import kotlinx.android.synthetic.main.activity_groups.*
import timber.log.Timber
import javax.inject.Inject

class GroupsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: GroupViewModel

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
    }

    private fun initVM(){
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GroupViewModel::class.java)
        viewModel.tryGetDynamicLinkIfCorrectIntent(intent, onDynamicLinkSuccess(), onDynamiLinkFailure())
        viewModel.logYolo()
        viewModel.loadUserGroups()
        viewModel.groupsLiveData.observe(this, Observer {
            Timber.d("Downloaded groups: $it")
        })
    }

    private fun informWhoLogged(){
        Toast.makeText(this, "${viewModel.getLogged()?.email ?: "Unknown"} has just logged in, name: ${viewModel.getLogged()?.displayName ?: "Unknown"}", Toast.LENGTH_LONG).show()
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

    @Suppress("UNUSED_PARAMETER")
    fun checkNetwork(view: View){
        val msg = if (viewModel.isNetworkConnected()) "Connected" else "Connection lost"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun loadGroups(view: View){
        viewModel.loadUserGroups()
    }

}
