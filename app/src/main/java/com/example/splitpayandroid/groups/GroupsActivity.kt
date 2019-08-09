package com.example.splitpayandroid.groups

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.splitpayandroid.R
import com.example.splitpayandroid.dagger_snippet.ConstructorInj
import dagger.android.AndroidInjection

import kotlinx.android.synthetic.main.activity_groups.*
import javax.inject.Inject

class GroupsActivity : AppCompatActivity() {

    @Inject
    lateinit var constructorInj: ConstructorInj

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        constructorInj.snippet()
    }

}
