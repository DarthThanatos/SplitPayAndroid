package com.example.splitpayandroid.groups

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import com.example.splitpayandroid.model.User
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerAppCompatActivity

import kotlinx.android.synthetic.main.content_groups.*
import javax.inject.Inject

class GroupsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        initViewModel()
    }

    private fun initViewModel(){
        createAndAssignViewModel()
        subscribeOnToastMessages()
        subscribeProgressBar()
        subscribeApiUser()
    }

    private fun createAndAssignViewModel() {
        viewModel = ViewModelProviders.of(
            this, viewModelFactory
        ).get(GroupViewModel::class.java)
    }

    private fun subscribeOnToastMessages(){
        viewModel.toastMessagesLiveData.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })
    }

    private fun subscribeApiUser(){
        viewModel.loadSavedUser()
        viewModel.userLiveData.observe(this, Observer {
            bindUserProfile(it)
            setupTabLayout()
            setupViewPager()
//            showFragment { GroupsFragment.newInstance() }
        })
    }

    private fun subscribeProgressBar(){
        viewModel.progressLiveData.observe(this, Observer {shouldShow ->
            groupsProgressBar.visibility = if(shouldShow) View.VISIBLE else View.INVISIBLE
        })
    }

    private fun setupTabLayout(){
        groupsTabLayout.addTab(groupsTabLayout.newTab().setText("All"))
        groupsTabLayout.addTab(groupsTabLayout.newTab().setText("My created"))
        groupsTabLayout.addTab(groupsTabLayout.newTab().setText("My participating"))
        groupsTabLayout.tabGravity = TabLayout.GRAVITY_FILL;
    }

    private fun setupViewPager(){
        groupsPager.adapter = GroupsPageAdapter(supportFragmentManager)
        groupsPager.currentItem = 0

        groupsPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(groupsTabLayout) //inform tab layout that page was slided
        )

        groupsTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                println("Tab selected: ${tab.position}")
                groupsPager.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun bindUserProfile(user: User){
        groupsAppBarTitle.text = resources.getString(R.string.logged_in_info, user.displayname)
        Glide.with(this).load(user.avatarUrl).into(groupsProfileButton);
    }

//    private fun showFragment(fragmentFactory: () -> Fragment){
//        val newFragment = fragmentFactory()
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.groupsFragmentContainer, newFragment)
//        transaction.commit()
//    }

}
