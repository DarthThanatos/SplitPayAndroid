package com.example.splitpayandroid.intro

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.splitpayandroid.R
import com.example.splitpayandroid.architecture.ViewModelFactory
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_biometric_dialog.*
import kotlinx.android.synthetic.main.fragment_biometric_dialog.view.*
import javax.inject.Inject

class BiometricDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        initViewModel()
        return AlertDialog.Builder(activity)
            .setView(newView())
            .create()
    }

    private fun initViewModel(){
        activity?.apply {
            val viewModel = ViewModelProviders.of(this, viewModelFactory).get(IntroViewModel::class.java)
            viewModel.biometricAuthenticationStatus.observe(this, object: Observer<String>{
                override fun onChanged(status: String) {
                    if(status == "success"){
                        dismiss()
                        return
                    }
                    itemStatus.setText(status)
                }
            })
        }
    }

    @SuppressLint("InflateParams")
    private fun newView(): View{
        val inflater = requireActivity().layoutInflater
        val v = inflater.inflate(R.layout.fragment_biometric_dialog, null)
        v.btn_cancel.setOnClickListener { dismiss() }
        return v
    }

    companion object {

        @JvmStatic
        fun newInstance() = BiometricDialogFragment()
    }
}
