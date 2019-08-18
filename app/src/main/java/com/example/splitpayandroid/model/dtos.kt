package com.example.splitpayandroid.model


data class User(
    val userid: Long,
    val email: String  = "",
    val displayname: String = "",
    val isoffline: Boolean = true,
    val avatarUrl: String = ""
)

typealias UsersList = List<User>

data class GroupDto(val groupid: Int,
                    val displayname: String,
                    val isactive: Boolean)