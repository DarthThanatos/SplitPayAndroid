package com.example.splitpayandroid.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

//  to view events in the firebase console immidiately, run the following line to enter debug mode
//  adb -s 192.168.0.101:5555 shell setprop debug.firebase.analytics.app com.example.splitpayandroid
class Analytics @Inject constructor(context: Context){

    private val anal = FirebaseAnalytics.getInstance(context)

    fun logLogin(method: String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, method)
        anal.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

}