package com.example.splitpayandroid.intro

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitpayandroid.fingerprint.FingerPrint
import com.example.splitpayandroid.model.UsersList
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import dagger.Binds
import dagger.Module
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class VM(private val introRepository: IntroRepository): ViewModel(){

    private val composite = CompositeDisposable()
    val usersList = MutableLiveData<UsersList>()
    val authenticationStatus = MutableLiveData<String>()
    val biometricUnlocked = MutableLiveData<Boolean>()

    fun create(email: String, password: String, onComplete: OnCompleteListener<AuthResult>, onFailure: OnFailureListener){
        introRepository.create(email, password, onComplete, onFailure)
    }

    fun logLogin(method: String){
        introRepository.logLogin(method)
    }

    private fun singleFingerPrint(context: Context) = Single.create(object: SingleOnSubscribe<FingerPrint>{
        override fun subscribe(emitter: SingleEmitter<FingerPrint>) {
            emitter.onSuccess(FingerPrint(context))
        }
    })

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

@Suppress("unused")
@Module
abstract class ViewModelModule{

    @Binds
    abstract fun bindVMFactory(vmFactory: VMFactory): ViewModelProvider.Factory
}

class VMFactory @Inject constructor(private val introRepository: IntroRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(VM::class.java)){
            return VM(introRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}