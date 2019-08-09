package com.example.splitpayandroid.intro

import com.example.splitpayandroid.model.User
import com.example.splitpayandroid.retrofit.UsersService
import dagger.Binds
import dagger.Module
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject


interface IntroPresenter{
    fun fetchUsers(usersService: UsersService)
    fun attachView(view: IntroView)
    fun detachView()
}

@Suppress("unused")
@Module
interface IntroPresenterModule{
    @Binds
    fun bindIntroPresenter(introPresenterImpl: IntroPresenterImpl): IntroPresenter
}

class IntroPresenterImpl @Inject constructor(): IntroPresenter{

    private var view: IntroView? = null
    private var disposables: CompositeDisposable? = null

    override fun attachView(view: IntroView){
        this.view = view
    }

    private fun dispose(){
        val shouldDispose = !(disposables?.isDisposed() ?: true)
        if(shouldDispose){
           disposables?.dispose()
       }
    }

    override fun detachView(){
        view = null
        dispose()
    }


    private fun observer() = object: Observer<User>{
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables?.add(d)
        }

        override fun onNext(t: User) {
            println(t)
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

    }

    override fun fetchUsers(usersService: UsersService) {
        usersService
            .getUser(20005L)
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {  }
            .subscribe(observer())
    }

}