package com.example.dutic2.negocios.ui.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.dutic2.R
import com.example.dutic2.negocios.NegociosViewModel
import kotlinx.android.synthetic.main.fragment_permisos.*


class PermisosFragment : Fragment() {
    lateinit var negociosViewModel: NegociosViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.let {
            negociosViewModel = ViewModelProviders.of(it).get(NegociosViewModel::class.java)
        }
        return inflater.inflate(R.layout.fragment_permisos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permisos_recycler_view.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        negociosViewModel.getAdapterPermiso().observe(this, Observer {
            permisos_recycler_view.adapter = it

        })
    }
    override fun onStop() {
        super.onStop()
    }
}
