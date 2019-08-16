package com.example.splitpayandroid.intro

import androidx.core.hardware.fingerprint.FingerprintManagerCompat

class CustomAuthenticationCallback(private val biometricCallback: BiometricCallback): FingerprintManagerCompat.AuthenticationCallback(){
    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
        biometricCallback.onAuthenticationError(errString.toString())
    }

    override fun onAuthenticationFailed() {
        biometricCallback.onAuthenticationFailed()
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
        biometricCallback.onAuthenticationHelp(helpString.toString())
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
        biometricCallback.onAuthenticationSucceeded(result)
    }

    interface BiometricCallback {
        fun onAuthenticationHelp(msg: String)
        fun onAuthenticationFailed()
        fun onAuthenticationError(msg: String)
        fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult)
    }
}