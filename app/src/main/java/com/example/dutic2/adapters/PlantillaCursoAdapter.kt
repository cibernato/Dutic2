package com.example.dutic2.adapters

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.models.Curso


class PlantillaCursoAdapter(var cursos: Array<Curso>, var mPlantillaClickListener: PlantillaClickListener) :
    RecyclerView.Adapter<PlantillaCursoAdapter.PlantillaCursoHolder>() {

    interface PlantillaClickListener{
        fun onPlantillaClickListener(curso: Curso)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantillaCursoHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.plantilla_curso_list_item, parent, false)
        val displaymetrics = DisplayMetrics()
        (parent.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
        val devicewidth: Int = displaymetrics.widthPixels / 2
        val deviceheight: Int = displaymetrics.heightPixels / 4
        view.layoutParams.width = devicewidth
        view.layoutParams.height = deviceheight

        return PlantillaCursoHolder(view)
    }

    override fun getItemCount(): Int {
        return cursos.size
    }

    override fun onBindViewHolder(holder: PlantillaCursoHolder, position: Int) {
        holder.bind(cursos[position])
    }

    inner class PlantillaCursoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val t: TextView = itemView.findViewById(R.id.plantilla_nombre_curso)
        fun bind(curso: Curso) {
            t.text = curso.nombre
            itemView.setOnClickListener {
                mPlantillaClickListener.onPlantillaClickListener(curso)
            }
        }
    }
}