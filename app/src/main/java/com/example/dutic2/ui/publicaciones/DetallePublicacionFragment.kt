package com.example.dutic2.ui.publicaciones


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.dutic2.R
import com.example.dutic2.adapters.AdjuntosAdapter
import com.example.dutic2.models.Publicacion
import kotlinx.android.synthetic.main.fragment_detalle_publicacion.*

/**
 * A simple [Fragment] subclass.
 */
class DetallePublicacionFragment : Fragment() {

    var publicacion: Publicacion? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        publicacion = arguments?.getSerializable("publicacion") as Publicacion
        return inflater.inflate(R.layout.fragment_detalle_publicacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (publicacion?.tipo == 2) {
            imgview1.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_tarea_publicacion_icon, null
                )
            )
        }
        titulo_detalle.text = publicacion?.titulo
        fecha_detalle.text = publicacion?.fecha
        contenido_detalle.text = publicacion?.contenido
        val adapter = AdjuntosAdapter(publicacion?.adjuntos!!)
        adjuntos_detalle.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adjuntos_detalle.adapter = adapter


    }
}
