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
import com.example.dutic2.negocios.models.Transaccion
import com.example.dutic2.negocios.models.TransaccionAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_transacciones.*

/**
 * A simple [Fragment] subclass.
 */
class TransaccionesFragment : Fragment(), TransaccionAdapter.TransaccionListener {

    val refTransacciones =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/transacciones")
    private lateinit var negociosViewModel: NegociosViewModel
    lateinit var adapter: TransaccionAdapter
    var filtros = arrayListOf<String>()

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
            LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            ) as RecyclerView.LayoutManager?
        negociosViewModel.getTransaccionesActualizados().observe(this, Observer {
            adapter = TransaccionAdapter(arrayListOf<Transaccion>().apply {
                addAll(it)
            }, this)
            transacciones_recycler_view.adapter = adapter
        })
        transacciones_busqueda.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        add_transaccion_button.setOnClickListener {
            crearDialog(Transaccion())
        }
        filtros_transacciones.setOnClickListener {
            val f = FiltrosDialog.newInstance(filtros)
            f.setTargetFragment(this, 159)
            f.show(fragmentManager!!, "dialog")
        }
    }

    override fun onClickTransaccion(model: Transaccion) {
        crearDialog(model)
    }

    fun crearDialog(transaccion: Transaccion) {
        val d = TransaccionDialog.newInstance(transaccion)
        d.setTargetFragment(this, 147)
        d.show(fragmentManager!!, "rip")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 147) {
            val recibido = data?.getParcelableExtra<Transaccion>("valor")
            pushToDatabase(recibido!!)
        }
        if (requestCode == 159) {
            adapter.filterBy(filtros)
        }
    }

    private fun pushToDatabase(transaccion: Transaccion) {
        Log.e("Push", "enviara $transaccion")
        if (transaccion.codigo.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .document("/negocios/tablas/transacciones/${transaccion.id}").set(
                    transaccion, SetOptions.merge()
                ).addOnCompleteListener {
                    Log.e("PushtoDatabse", "Actualizado correctamente")

                }

        } else {
            val x = adapter.transacciones.last()
            transaccion.codigo = "${x.codigo.toInt() + 1}"
            transaccion.estado = if (transaccion.estado.isNotEmpty()) transaccion.estado else "a"
            refTransacciones.add(transaccion).addOnCompleteListener {
                Log.e("PushtoDatabse", "Subio corectament con id ${it.result?.id}")
            }
        }
    }
}
