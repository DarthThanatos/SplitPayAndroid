package com.example.splitpayandroid.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paygroups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val groupId: Int,
    val displayName: String,
    val isActive: Boolean
)