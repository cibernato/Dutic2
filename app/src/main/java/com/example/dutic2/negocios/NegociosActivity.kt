package com.example.dutic2.negocios

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R
import com.example.dutic2.negocios.ui.main.NegociosFragment

class NegociosActivity : AppCompatActivity() {
    lateinit var negociosViewModel: NegociosViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_negocios)
        supportFragmentManager.beginTransaction().replace(R.id.negocios_content, NegociosFragment())
            .commit()
        negociosViewModel = ViewModelProviders.of(this).get(NegociosViewModel::class.java)
        negociosViewModel.getAdapterPermiso().observe(this, Observer {
            it.startListening()
        })
        negociosViewModel.getAdapterTrabajadores().observe(this, Observer {
            it.startListening()
        })
        negociosViewModel.getAdapterTransacciones().observe(this, Observer {
            it.startListening()
        })
    }

}