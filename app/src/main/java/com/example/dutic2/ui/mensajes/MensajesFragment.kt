package com.example.dutic2.ui.mensajes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R

class MensajesFragment : Fragment() {

    private lateinit var mensajesViewModel: MensajesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mensajesViewModel =
            ViewModelProviders.of(this).get(MensajesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_mensajes, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        mensajesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}