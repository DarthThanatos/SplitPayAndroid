package com.example.splitpayandroid.intro

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.splitpayandroid.di.scope.ApplicationScope
import com.example.splitpayandroid.fingerprint.FingerPrint
import com.example.splitpayandroid.model.UsersList
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ApplicationScope
class IntroViewModel @Inject constructor (private val introRepository: IntroRepository): ViewModel(), IntroRepositoryWork by introRepository{

    private val composite = CompositeDisposable()
    val authenticationStatus = MutableLiveData<String>()
    val biometricUnlocked = MutableLiveData<Boolean>()
    val savedCredentials = MutableLiveData<Pair<String, String>>()

    fun readSavedCredentials(){
        savedCredentials.value = introRepository.getSavedCredentials();
    }

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

    override fun onCleared() {
        composite.dispose()
        super.onCleared()
    }
}
