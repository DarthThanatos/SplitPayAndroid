package com.example.splitpayandroid.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import com.example.splitpayandroid.model.GroupDto
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_all_groups.*
import javax.inject.Inject


class GroupsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: GroupViewModel

    private val groupsList = ArrayList<GroupDto>()
    private lateinit var adapter: GroupsAdapter

    private var position: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setupRecyclerView()
    }

    private fun initViewModel(){
        createAndAssignViewModel()
        subscribeUserGroups()
    }

    private fun subscribeUserGroups(){
        when(position){
            0 -> {
                viewModel.loadAllGroups()
                viewModel.allGroups.observe(this, Observer {
                    handleUserGroups(it)
                })
            }
            1 -> {
                viewModel.loadUserGroups()
                viewModel.userGroups.observe(this, Observer {
                    handleUserGroups(it)
                })
            }
            2 -> {
                viewModel.loadGroupsOfParticipating()
                viewModel.participatingGroups.observe(this, Observer {
                    handleUserGroups(it)
                })
            }
        }
    }

    private fun handleUserGroups(groups: List<GroupDto>){
        groupsList.removeAll { true }
        groupsList.addAll(groups)
        adapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView(){
        adapter = GroupsAdapter(groupsList)
        groupsFramgentRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@GroupsFragment.adapter

        }
    }

    private fun createAndAssignViewModel(){
        activity?.apply {
            viewModel = ViewModelProviders.of(
                this, viewModelFactory
            ).get(GroupViewModel::class.java)
        }

    }

    companion object {

        private const val POSITION = "position"

        @JvmStatic
        fun newInstance(position: Int) = GroupsFragment().apply {
            arguments = Bundle().apply {
                putInt(POSITION, position)
            }
        }
    }
}
