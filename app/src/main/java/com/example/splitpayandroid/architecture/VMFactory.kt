package com.example.splitpayandroid.architecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitpayandroid.groups.GroupVM
import com.example.splitpayandroid.groups.GroupsRepository
import com.example.splitpayandroid.intro.IntroRepository
import com.example.splitpayandroid.intro.IntroVM
import javax.inject.Inject

class VMFactory @Inject constructor(
    private val introRepository: IntroRepository,
    private val groupsRepository: GroupsRepository
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(IntroVM::class.java)){
            return IntroVM(introRepository) as T
        }
        if(modelClass.isAssignableFrom(GroupVM::class.java)){
            return GroupVM(groupsRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}