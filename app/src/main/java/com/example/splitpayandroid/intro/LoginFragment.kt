package com.example.splitpayandroid.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.splitpayandroid.R
import dagger.android.support.DaggerFragment

class LoginFragment : DaggerFragment() {

    private lateinit var email: String
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString(ARG_NAME)!!
            name = it.getString(ARG_EMAIL)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {

        private const val ARG_NAME = "NAME"
        private const val ARG_EMAIL = "EMAIL"

        @JvmStatic
        fun newInstance(email: String, name: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, email)
                    putString(ARG_EMAIL, name)
                }
            }
    }
}
