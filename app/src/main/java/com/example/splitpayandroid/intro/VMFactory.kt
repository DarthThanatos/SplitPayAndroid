package com.example.splitpayandroid.intro

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitpayandroid.model.UsersList
import com.example.splitpayandroid.retrofit.UsersService
import dagger.Binds
import dagger.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider


class VM(private val usersService: UsersService): ViewModel(){

    private val composite = CompositeDisposable()
    val usersList = MutableLiveData<UsersList>()


    private fun observer() =
        object: DisposableObserver<UsersList>() {
            override fun onComplete() {

            }

            override fun onNext(t: UsersList) {
                usersList.value = t
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

        }

    fun loadGroups(){
        composite.add(
            usersService.getUsersInGroup(3L)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer())
        )
    }

    override fun onCleared() {
        composite.dispose()
        super.onCleared()
    }
}

@Module
abstract class VieModelModule{

    @Binds
    abstract fun bindVMFactory(vmFactory: VMFactory): ViewModelProvider.Factory
}

class  VMFactory @Inject constructor(private val usersService: UsersService,
                                     private val mapOne: Map<String, Int>, @Named("yo1") val yo: Int): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        println("VM Fac, $yo")
        mapOne.forEach{println(it)}
        if(modelClass.isAssignableFrom(VM::class.java)){
            return VM(usersService) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}