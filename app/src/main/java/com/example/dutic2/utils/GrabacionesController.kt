package com.example.dutic2.utils

import android.net.Uri
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.example.dutic2.models.GrabacionModel_
import java.util.*
import kotlin.collections.ArrayList

class GrabacionesController(
    var paths: ArrayList<Uri>,
    var duraciones: ArrayList<String>,
    var mOpcionesClickListener: OpcionesClickListener,
    var mPlayPauseClickListener: PlayPauseClickListener

) : EpoxyController() {
    interface OpcionesClickListener {
        fun onOpcionesClick(
            model: GrabacionModel_,
            position: Int
        )
    }

    interface PlayPauseClickListener {
        fun onPlayPauseClick(
            model: GrabacionModel_,
            position: Int,
            clickedView: View
        )
    }

    override fun buildModels() {
        if (paths.isNotEmpty() && duraciones.isNotEmpty()) {
            for (i in 0 until paths.size) {
                GrabacionModel_().apply {
                    path = paths[i]
                    duration = duraciones[i]
                }.id("${UUID.randomUUID()}")
                    .opcionesListemer { model, _, _, position ->
                        mOpcionesClickListener.onOpcionesClick(model,position)
                    }
                    .playPauseListemer { model, _, clickedView, position ->
                        mPlayPauseClickListener.onPlayPauseClick(model,position,clickedView)
                    }
                    .addTo(this)
            }
        }
    }
}