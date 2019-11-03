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
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_all_groups.*
import javax.inject.Inject


class GroupsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: GroupViewModel

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
            0 -> viewModel.loadAllGroups()
            1 -> viewModel.loadUserGroups()
            2 -> viewModel.loadGroupsOfParticipating()
        }
        viewModel.groupsLoaded.observe(this, Observer {
            handleUserGroups()
        })
    }

    private fun handleUserGroups(){
        adapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView(){
        adapter = GroupsAdapter(viewModel.groupsList)
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
