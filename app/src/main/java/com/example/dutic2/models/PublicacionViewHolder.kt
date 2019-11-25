package com.example.dutic2.models

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R

class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    interface PublicacionClickListener {
        fun onPublicacionClicked(publicacion: Publicacion)
    }

    var titulo: TextView = itemView.findViewById(R.id.titulo_publicacion)
    var fecha: TextView = itemView.findViewById(R.id.fecha_publicacion)
    var contenido: TextView = itemView.findViewById(R.id.contenido_publicacion)

    fun bind(publicacion: Publicacion, mPublicacionClickListener: PublicacionClickListener) {
        titulo.text = publicacion.titulo
        fecha.text = publicacion.fecha
        contenido.text = publicacion.contenido
        if (publicacion.tipo == 2) {
            itemView.findViewById<ImageView>(R.id.tipo_publicacion_icon).setImageDrawable(
                ResourcesCompat.getDrawable(
                    itemView.context.resources,
                    R.drawable.ic_tarea_publicacion_icon, null
                )
            )
        }
        itemView.setOnClickListener {
            mPublicacionClickListener.onPublicacionClicked(publicacion)
        }

    }
}