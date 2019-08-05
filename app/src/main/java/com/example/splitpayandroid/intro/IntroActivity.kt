package com.example.splitpayandroid.intro

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.model.UsersList
import com.example.splitpayandroid.retrofit.RetrofitProvider
import com.example.splitpayandroid.retrofit.UsersService

import kotlinx.android.synthetic.main.activity_main.*

interface IntroView{

}

class SnackListener: View.OnClickListener{
    override fun onClick(v: View?) {
        println("clicked snackbar")
    }
}

class IntroActivity : AppCompatActivity(), IntroView {

    private var presenter: IntroPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", SnackListener()).show()
        }
        val usersService = RetrofitProvider().usersService
        initPresenter(usersService)
        initVM(usersService)
    }

    private fun initPresenter(usersService: UsersService){
        presenter = IntroPresenterImpl()
        presenter?.attachView(this)
        presenter?.fetchUsers(usersService)
    }

    private fun initVM(usersService: UsersService){
        val vmFactory = VMFactory(usersService)
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
        presenter?.detachView()
        presenter = null
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
