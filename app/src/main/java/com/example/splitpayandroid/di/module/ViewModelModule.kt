package com.example.splitpayandroid.di.module

import androidx.lifecycle.ViewModel
import com.example.splitpayandroid.architecture.ViewModelKey
import com.example.splitpayandroid.groups.GroupViewModel
import com.example.splitpayandroid.intro.IntroViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
interface ViewModelModule{

    @Binds
    @IntoMap
    @ViewModelKey(IntroViewModel::class)
    fun bindIntroViewModel(introViewModel: IntroViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(GroupViewModel::class)
    fun bindGroupViewModel(groupViewModel: GroupViewModel): ViewModel

}