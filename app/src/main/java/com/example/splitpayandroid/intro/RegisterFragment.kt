package com.example.splitpayandroid.intro

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import dagger.android.support.DaggerFragment
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
        activity?.apply {
            viewModel = ViewModelProviders.of(this, factory).get(IntroViewModel::class.java)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            RegisterFragment()
    }
}
