package com.example.splitpayandroid.groups

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.splitpayandroid.di.scope.ApplicationScope
import com.example.splitpayandroid.model.GroupDto
import com.example.splitpayandroid.model.User
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


@ApplicationScope
class GroupViewModel @Inject constructor(private val groupsRepository: GroupsRepository): ViewModel() {

    private val composite: CompositeDisposable = CompositeDisposable()


    val userLiveData = MutableLiveData<User>()
    val progressLiveData = MutableLiveData<Boolean>()


    val allGroups = MutableLiveData<List<GroupDto>>()
    val userGroups = MutableLiveData<List<GroupDto>>()
    val participatingGroups = MutableLiveData<List<GroupDto>>()

    val toastMessagesLiveData = MutableLiveData<String>()

    fun loadSavedUser(){
        progressLiveData.value = true
        val userId = groupsRepository.getSavedUserId()
        if(userId == -1L){
            toastMessagesLiveData.value = "Error: Failed to load user"
            progressLiveData.value = false
            return
        }
        composite.add(
            groupsRepository.getUser(userId).subscribe {
                progressLiveData.value = false
                userLiveData.value = it
            }
        )
    }


    fun loadAllGroups() {
        composite.add(groupsRepository
            .getAllGroups()
            .subscribe {
                allGroups.value = it
            }
        )
    }


    fun loadGroupsOfParticipating() {
        composite.add(groupsRepository
            .getGroupsOfParticipating()
            .subscribe {
                participatingGroups.value = it
            }
        )
    }

    fun loadUserGroups() {
        composite.add(groupsRepository
            .getUserGroups()
            .subscribe {
                userGroups.value = it
            }
        )
    }

    override fun onCleared() {
        composite.clear()
        super.onCleared()
    }
}