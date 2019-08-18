package com.example.splitpayandroid.retrofit

import javax.inject.Inject

interface ConnectivityManager {
    fun isNetworkAvailable(): Boolean
}

class ConnectivityManagerImpl @Inject constructor(
    private val connectivityManager: android.net.ConnectivityManager): ConnectivityManager {

    override fun isNetworkAvailable(): Boolean {
        connectivityManager.apply {
            val networkInfo = this.activeNetworkInfo
            return (networkInfo != null && networkInfo.isConnected)
        }
    }

}