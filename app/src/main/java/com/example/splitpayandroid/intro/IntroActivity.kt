package com.example.splitpayandroid.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.dagger_snippet.ConstructorInj
import com.example.splitpayandroid.groups.GroupsActivity
import com.example.splitpayandroid.model.UsersList
import com.example.splitpayandroid.retrofit.UsersService
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
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

class IntroActivity : AppCompatActivity(), IntroView {

    @Inject lateinit var constructorInj: ConstructorInj

    @Inject lateinit var usersService: UsersService

    @Inject lateinit var vmFactory: VMFactory

    @Inject lateinit var presenter: IntroPresenter

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
        val vm = ViewModelProviders.of(this, vmFactory).get(VM::class.java)
        vm.usersList.observe(this, object: Observer<UsersList>{
            override fun onChanged(t: UsersList) {
                println("returned users list in vm:")
                println(t)
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
}
