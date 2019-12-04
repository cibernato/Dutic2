package com.example.dutic2.negocios.ui.main


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.negocios.NegociosViewModel
import com.example.dutic2.negocios.models.Permiso
import com.example.dutic2.negocios.models.PermisoAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_permisos.*


class PermisosFragment : Fragment(), PermisoAdapter.PermisoClickListener {

    lateinit var negociosViewModel: NegociosViewModel
    private val refPermisos =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/permisos")
    lateinit var adapter: PermisoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            negociosViewModel = ViewModelProviders.of(it).get(NegociosViewModel::class.java)
        }
        return inflater.inflate(R.layout.fragment_permisos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permisos_recycler_view.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        negociosViewModel.getPermisosActualizados().observe(this, Observer {
            adapter = PermisoAdapter(arrayListOf<Permiso>().apply { addAll(it) }, this)
            permisos_recycler_view.adapter = adapter
        })
        add_permiso_button.setOnClickListener {
            crearDialogPermiso(Permiso())
        }
    }

    private fun crearDialogPermiso(permiso: Permiso) {
        val i = AddDialogFragment.newInstance(permiso)
        i.setTargetFragment(this, 159)
        i.show(fragmentManager!!, "kappa")
    }


    override fun onPermisoClick(permiso: Permiso) {
        Log.e("PTMR", "Enviado $permiso")
        crearDialogPermiso(permiso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 159) {
            val recibido = data?.getParcelableExtra<Permiso>("valor")!!
            if (recibido.nombre.isNotEmpty()) pushToDatabase(recibido)
            else Log.e("REsultPermiso", " No se realizo nada ")
        }
    }

    private fun pushToDatabase(permiso: Permiso) {
        if (permiso.codigo.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .document("/negocios/tablas/permisos/${permiso.id}").set(
                    permiso, SetOptions.merge()
                )

        } else {
            val x = adapter.permisos.last()
            permiso.codigo = "${x.codigo.toInt() + 1}"
            permiso.estado = if (permiso.estado.isNotEmpty()) permiso.estado else "a"
            refPermisos.add(permiso).addOnCompleteListener {
                Log.e("PushtoDatabse", "Subio corectament con id ${it.result?.id}")
            }
        }
    }
}
