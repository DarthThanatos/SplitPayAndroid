package com.example.splitpayandroid.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : DaggerFragment(){

    @Inject lateinit var factory: ViewModelFactory
    private lateinit var viewModel: IntroViewModel

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString(ARG_NAME)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initOpenRegisterButton()
        initLoginButton()
        initEmailInput()
    }

    private fun initEmailInput(){
        loginEmailInput.setText(email)
    }

    private fun initViewModel(){
        activity?.apply {
            viewModel = ViewModelProviders.of(this, factory).get(IntroViewModel::class.java)
        }
    }

    private fun initOpenRegisterButton(){
        moveToRegisterButton.setOnClickListener {
            viewModel.moveToRegister()
        }
    }

    private fun initLoginButton(){
        loginBtn.setOnClickListener {
            val email = loginEmailInput.text.toString()
            val password = loginPasswordInput.text.toString()
            viewModel.onLogin(email, password)
        }
    }

    companion object {

        private const val ARG_NAME = "NAME"

        @JvmStatic
        fun newInstance(email: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, email)
                }
            }
    }
}
