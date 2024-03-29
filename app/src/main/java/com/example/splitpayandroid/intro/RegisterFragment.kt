package com.example.splitpayandroid.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject

class RegisterFragment : DaggerFragment() {

    @Inject lateinit var factory: ViewModelFactory
    private lateinit var viewModel: IntroViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initEmailOnlyRegisterButton()
        initSubmitRegisterButton()
    }

    private fun initViewModel(){
        activity?.apply {
            viewModel = ViewModelProviders.of(this, factory).get(IntroViewModel::class.java)
        }
    }

    private fun initSubmitRegisterButton(){
        submitRegisterButton.setOnClickListener {
            val name = registerNameInput.text.toString()
            val email = registerEmailInput.text.toString()
            val password = registerPasswordInput.text.toString()
            viewModel.onRegister(name, password, email)
        }
    }

    private fun initEmailOnlyRegisterButton(){
        moveToEmailOnlyRegisterButton.setOnClickListener {
            viewModel.moveToRegisterEmailOnly()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            RegisterFragment()
    }
}
