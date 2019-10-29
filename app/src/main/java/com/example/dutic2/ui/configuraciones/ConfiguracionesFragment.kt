package com.example.dutic2.ui.configuraciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R

class ConfiguracionesFragment : Fragment() {

    private lateinit var configuracionesViewModel: ConfiguracionesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configuracionesViewModel =
            ViewModelProviders.of(this).get(ConfiguracionesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_configuraciones, container, false)
        val textView: TextView = root.findViewById(R.id.text_share)
        configuracionesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}