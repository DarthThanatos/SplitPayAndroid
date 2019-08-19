package com.example.splitpayandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.splitpayandroid.database.entity.GroupEntity
import io.reactivex.Single

@Dao
interface GroupsDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserGroups(paygroups: List<GroupEntity>)

    @Query("select * from paygroups")
    fun getGroups(): Single<List<GroupEntity>>

    @Query("delete from paygroups")
    fun deleteUserGroups()

}