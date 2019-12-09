package com.example.dutic2.negocios.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.negocios.NegociosViewModel
import com.example.dutic2.negocios.models.Trabajador
import com.example.dutic2.negocios.models.TrabajadorAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.trabajadores_fragment.*

/**
 * A placeholder fragment containing a simple view.
 */
class TrabajadoresFragment : Fragment(), TrabajadorAdapter.TrabajadorViewHolderListener {
    val ref_trabajadores =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/trabajadores")
    private lateinit var negociosViewModel: NegociosViewModel
    lateinit var adapter: TrabajadorAdapter
    var filtros = arrayListOf<String>()

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

        negociosViewModel.getTrabajadoresActualizados().observe(this, Observer {
            adapter = TrabajadorAdapter(arrayListOf<Trabajador>().apply {
                addAll(it)
            }, this)
            trabajadores_recycler_view.adapter = adapter
        })
        add_trabajador_button.setOnClickListener {
            crearDialogTrabajador(Trabajador())
        }
        trabajadores_busqueda.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        filtros_trabajadores.setOnClickListener {
            val f = FiltrosDialog.newInstance(filtros)
            f.setTargetFragment(this, 159)
            f.show(fragmentManager!!, "dialog")
        }
    }

    fun crearDialogTrabajador(trabajador: Trabajador) {
        val i = AddDialogFragment.newInstance(trabajador)
        i.setTargetFragment(this, 123)
        i.show(fragmentManager!!, "kappa")
    }

    override fun onClickTrabajador(trabajador: Trabajador) {
        Log.e("PTMR", "Enviado $trabajador")
        crearDialogTrabajador(trabajador)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 123) {
            val recibido = data?.getParcelableExtra<Trabajador>("valor")!!
            if (recibido.nombre.isNotEmpty()) pushToDatabase(recibido)
        }
        if (requestCode == 159) {
            adapter.filterBy(filtros)
        }
    }

    fun pushToDatabase(trabajador: Trabajador) {
        if (trabajador.codigo.isNotEmpty()) {
            Log.e("PushtoDatabse", "codigo actual ${trabajador.codigo}")
            FirebaseFirestore.getInstance()
                .document("/negocios/tablas/trabajadores/${trabajador.id}").set(
                    trabajador, SetOptions.merge()
                )

        } else {
            val x = adapter.trabajadores.last()
            Log.e("PushtoDatabse", "ultimo codiog ${x.codigo}")
            trabajador.codigo = "${x.codigo.toInt() + 1}"
            trabajador.estado = "a"
            ref_trabajadores.add(trabajador).addOnCompleteListener {
                Log.e("PushtoDatabse", "Subio corectament con id ${it.result?.id}")
            }
        }
    }


}