package com.example.dutic2.ui.promedio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R

class PromediosFragment : Fragment() {

    private lateinit var promediosViewModel: PromediosViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        promediosViewModel =
            ViewModelProviders.of(this).get(PromediosViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_promedio, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        promediosViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}