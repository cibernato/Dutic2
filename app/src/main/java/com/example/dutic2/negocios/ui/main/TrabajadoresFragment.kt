package com.example.dutic2.negocios.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.negocios.NegociosViewModel
import com.example.dutic2.R
import kotlinx.android.synthetic.main.trabajadores_fragment.*

/**
 * A placeholder fragment containing a simple view.
 */
class TrabajadoresFragment : Fragment() {

    private lateinit var negociosViewModel: NegociosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            negociosViewModel = ViewModelProviders.of(it).get(NegociosViewModel::class.java)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trabajadores_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trabajadores_recycler_view.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        negociosViewModel.getAdapterTrabajadores().observe(this, Observer {
            trabajadores_recycler_view.adapter = it
        })
    }
}