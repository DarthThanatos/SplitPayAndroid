package com.example.splitpayandroid.intro

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.splitpayandroid.fingerprint.FingerPrint
import com.example.splitpayandroid.model.UsersList
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class IntroVM(private val introRepository: IntroRepository): ViewModel(), IntroRepositoryWork by introRepository{

    private val composite = CompositeDisposable()
    val usersList = MutableLiveData<UsersList>()
    val authenticationStatus = MutableLiveData<String>()
    val biometricUnlocked = MutableLiveData<Boolean>()

    private fun singleFingerPrint(context: Context) =
        Single.create(SingleOnSubscribe<FingerPrint> { emitter -> emitter.onSuccess(FingerPrint(context)) })

    fun getFingerPrint(context: Context, onSuccess: (fp: FingerPrint) -> Unit) {
        composite.add(
            singleFingerPrint(context)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess)
        )
    }

    private fun observer() =
        object: DisposableObserver<UsersList>() {
            override fun onComplete() { }

            override fun onNext(t: UsersList) {
                usersList.value = t
            }
            override fun onError(e: Throwable) {
                e.printStackTrace()
            }
        }

    fun loadGroups(){
        composite.add(
            introRepository.getUserGroups()
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
