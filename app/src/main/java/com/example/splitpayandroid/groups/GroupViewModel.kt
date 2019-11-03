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

    val groupsList = ArrayList<GroupDto>()

    val userLiveData = MutableLiveData<User>()
    val groupsLoaded = MutableLiveData<Boolean>()
    val progressLiveData = MutableLiveData<Boolean>()

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
                groupsList.removeAll { true }
                groupsList.addAll(it)
                groupsLoaded.value = true
            }
        )
    }


    fun loadGroupsOfParticipating() {
        composite.add(groupsRepository
            .getGroupsOfParticipating()
            .subscribe {
                groupsList.removeAll { true }
                groupsList.addAll(it)
                groupsLoaded.value = true
            }
        )
    }

    fun loadUserGroups() {
        composite.add(groupsRepository
            .getUserGroups()
            .subscribe {
                groupsList.removeAll { true }
                groupsList.addAll(it)
                groupsLoaded.value = true
            }
        )
    }

    override fun onCleared() {
        composite.clear()
        super.onCleared()
    }
}