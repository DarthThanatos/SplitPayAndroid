package com.example.splitpayandroid.di.module

import android.content.Context
import androidx.room.Room
import com.example.splitpayandroid.database.Database
import com.example.splitpayandroid.database.dao.GroupsDao
import com.example.splitpayandroid.di.scope.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
abstract class DatabaseModule{

    @Module
    companion object {
        @JvmStatic
//        @ApplicationScope
        @Provides
        internal fun provideDatabase(context: Context): Database =
                Room.databaseBuilder(context, Database::class.java, "splitpay-db")
                    .fallbackToDestructiveMigration()
                    .build()

        @JvmStatic
//        @ApplicationScope
        @Provides
        internal fun provideGroupsDao(database: Database): GroupsDao =
            database.groupsDao()
    }


}