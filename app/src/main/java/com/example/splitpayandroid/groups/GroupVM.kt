package com.example.splitpayandroid.groups

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.splitpayandroid.model.GroupDto
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class GroupVM constructor(private val groupsRepository: GroupsRepository): ViewModel(), GroupRepositoryWork by groupsRepository{

    private val composite: CompositeDisposable = CompositeDisposable()

    val groupsLiveData = MutableLiveData<List<GroupDto>>()

    fun loadUserGroups() {
        composite.add(groupsRepository
            .getUserGroups()
            .subscribe { groupsLiveData.value = it }
        )
    }

    override fun onCleared() {
        composite.clear()
        super.onCleared()
    }
}