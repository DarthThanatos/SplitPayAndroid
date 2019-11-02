package com.example.splitpayandroid.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_email_only_registration.*
import javax.inject.Inject

class EmailOnlyRegistrationFragment : DaggerFragment() {

    @Inject
    lateinit var factory: ViewModelFactory
    private lateinit var viewModel: IntroViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email_only_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initEmailOnlySubmitButton()
        initMoveToRegisterButton()
    }

    private fun initViewModel(){
        activity?.apply {
            viewModel = ViewModelProviders.of(this, factory).get(IntroViewModel::class.java)
        }
    }

    private fun initEmailOnlySubmitButton(){
        submitEmailOnlyRegisterButton.setOnClickListener {
            val email = registerEmailOnlyInput.text.toString()
            viewModel.onVerifyEmail(email)
        }
    }

    private fun initMoveToRegisterButton(){
        moveToRegisterButton.setOnClickListener {
            viewModel.moveToRegister()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            EmailOnlyRegistrationFragment()
    }
}
