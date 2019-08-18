package com.example.splitpayandroid.groups

import androidx.lifecycle.ViewModel


class GroupVM constructor(private val groupsRepository: GroupsRepository): ViewModel(), GroupRepositoryWork by groupsRepository