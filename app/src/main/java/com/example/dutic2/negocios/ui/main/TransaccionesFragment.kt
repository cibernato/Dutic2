package com.example.dutic2.negocios.ui.main


import android.os.Bundle
import android.util.Log
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
import com.example.dutic2.negocios.models.TrabajadorAdapter
import com.example.dutic2.negocios.models.Transaccion
import com.example.dutic2.negocios.models.TransaccionAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_transacciones.*

/**
 * A simple [Fragment] subclass.
 */
class TransaccionesFragment : Fragment(), TransaccionAdapter.TransaccionListener {

    val ref_trabajadores =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/transacciones")
    private lateinit var negociosViewModel: NegociosViewModel
    lateinit var adapter: TransaccionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.let {
            negociosViewModel = ViewModelProviders.of(it).get(NegociosViewModel::class.java)
        }
        return inflater.inflate(R.layout.fragment_transacciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transacciones_recycler_view.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        negociosViewModel.getTransaccionesActualizados().observe(this, Observer {
            adapter = TransaccionAdapter(arrayListOf<Transaccion>().apply {
                addAll(it)
            }, this)
            transacciones_recycler_view.adapter = adapter
        })
    }

    override fun onClickTransaccion(model: Transaccion) {
        Log.e("ClickTransaccion","Funciona hasta aca ")
    }
}
