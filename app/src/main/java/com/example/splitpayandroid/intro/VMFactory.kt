package com.example.splitpayandroid.intro

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitpayandroid.model.UsersList
import com.example.splitpayandroid.retrofit.UsersService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


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

class VMFactory(private val usersService: UsersService): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(VM::class.java)){
            return VM(usersService) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}