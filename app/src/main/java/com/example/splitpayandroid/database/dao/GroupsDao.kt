package com.example.splitpayandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.splitpayandroid.database.entity.GroupEntity
import io.reactivex.Observable

@Dao
interface GroupsDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserGroups(paygroups: List<GroupEntity>)

    @Query("select * from paygroups")
    fun getGroups(): Observable<List<GroupEntity>>

    @Query("delete from paygroups")
    fun deleteUserGroups()

}