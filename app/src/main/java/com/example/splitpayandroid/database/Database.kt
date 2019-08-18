package com.example.splitpayandroid.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.splitpayandroid.database.dao.GroupsDao
import com.example.splitpayandroid.database.entity.GroupEntity

@Database(entities = [GroupEntity::class], version = 1)
abstract class Database: RoomDatabase(){
    abstract fun groupsDao(): GroupsDao
}