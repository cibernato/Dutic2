package com.example.dutic2.models

import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.dutic2.R


@EpoxyModelClass(layout = R.layout.nota_de_voz_list_item)
abstract class GrabacionModel : EpoxyModelWithHolder<GrabacionModel.GrabacionHolder>() {

    @EpoxyAttribute
    lateinit var playPauseListemer: View.OnClickListener
    @EpoxyAttribute
    lateinit var opcionesListemer: View.OnClickListener
    @EpoxyAttribute
    lateinit var path: Uri
    @EpoxyAttribute
    lateinit var duration: String



    override fun bind(holder: GrabacionHolder) {

        holder.apply {
            titulo.text = path.lastPathSegment
            duracion.text = duration
        }
    }

    inner class GrabacionHolder : EpoxyHolder() {
        lateinit var titulo: TextView
        lateinit var opciones: ImageButton
        lateinit var duracion: TextView
        lateinit var playIcon : ImageButton
        override fun bindView(itemView: View) {
            titulo = itemView.findViewById(R.id.nombre_nota_de_voz)
            duracion = itemView.findViewById(R.id.duracion_nota_de_voz)
            playIcon = itemView.findViewById(R.id.play_pause_icon)
            playIcon.setOnClickListener {
                playPauseListemer.onClick(it)
            }
            opciones = itemView.findViewById(R.id.opciones_nota_de_voz)
            opciones.setOnClickListener {
                opcionesListemer.onClick(it)
            }

        }

    }
}