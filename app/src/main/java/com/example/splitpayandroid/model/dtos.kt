package com.example.splitpayandroid.model


data class User(
    val userid: Long? = null,
    val email: String?  = null,
    val displayname: String? = null,
    val isoffline: Boolean? = null,
    val avatarUrl: String? = null
)

typealias UsersList = List<User>

data class GroupDto(val groupid: Int,
                    val displayname: String,
                    val isactive: Boolean)