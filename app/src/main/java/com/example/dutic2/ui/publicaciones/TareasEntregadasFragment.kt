package com.example.dutic2.ui.publicaciones


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.models.Curso
import com.example.dutic2.models.Publicacion
import com.example.dutic2.models.PublicacionViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_tareas_entregadas.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class TareasEntregadasFragment : Fragment(), PublicacionViewHolder.PublicacionClickListener {

    var curso: Curso? = null
    lateinit var novedadesViewModel: NovedadesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tareas_entregadas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        novedadesViewModel = ViewModelProviders.of(this).get(NovedadesViewModel::class.java)
        if (curso == null) {
            curso = novedadesViewModel.cursoModel
        }
        try {
            val ref =
                FirebaseFirestore.getInstance().collection("${curso!!.idGeneral}/publicaciones")
                    .whereEqualTo(
                        "tipo", 2
                    )
            val option = FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(ref, SnapshotParser<Publicacion?> { snapshot: DocumentSnapshot ->
                    val retornar = snapshot.toObject(Publicacion::class.java)
                    retornar?.uid = snapshot.id
                    return@SnapshotParser retornar!!

                }).setLifecycleOwner(viewLifecycleOwner).build()
            novedadesViewModel.apply {
                this.option = option
                this.mPublicacionClickListener = this@TareasEntregadasFragment
                this.cursoModel = curso!!
                this.isTarea = true
            }
            tareas_entregadas_list.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            var fra: FirestoreRecyclerAdapter<Publicacion, PublicacionViewHolder>?
            novedadesViewModel.getFRA().observe(this, Observer {
                fra = it
                tareas_entregadas_list.adapter = fra
                fra?.startListening()
            })
        } catch (e: Exception) {
            Log.e("Error TareasEnrhgadas", e.localizedMessage!!)

        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
        }
    }

    companion object {
        fun newInstance(curso: Curso) = TareasEntregadasFragment().apply {
            this.curso = curso
        }
    }

    override fun onPublicacionClicked(publicacion: Publicacion) {
        findNavController().navigate(
            R.id.nav_detallePublicacion,
            bundleOf("publicacion" to publicacion)
        )
    }

}
