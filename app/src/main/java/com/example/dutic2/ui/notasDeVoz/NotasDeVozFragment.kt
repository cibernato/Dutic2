package com.example.dutic2.ui.notasDeVoz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R

class NotasDeVozFragment : Fragment() {

    private lateinit var notasDeVozViewModel: NotasDeVozViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notasDeVozViewModel =
            ViewModelProviders.of(this).get(NotasDeVozViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notas_de_voz, container, false)
        val textView: TextView = root.findViewById(R.id.text_send)
        notasDeVozViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}