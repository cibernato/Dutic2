package com.example.dutic2.models

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R

class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var nom: TextView = itemView.findViewById(R.id.curso_nombre_list_item)
    var prof: TextView = itemView.findViewById(R.id.curso_profesor_list_item)
    var pendientes: TextView = itemView.findViewById(R.id.curso_list_item_pendientes)

    interface CursoClickListener {
        fun onCrsoClicked(curso: Curso)
    }

    fun bind(curso: Curso, string: String, mCursoClickListener: CursoClickListener) {
        nom.text = curso.nombre
        prof.text = curso.profesor
        when {
            curso.tareasPendientes == "0" -> pendientes.text = "No tiene tareas pendientes"
            curso.tareasPendientes == "1" -> pendientes.text = "Tiene 1 tarea pendiente"
            else -> pendientes.text = string.format(curso.tareasPendientes?.toInt())
        }
        itemView.setOnClickListener {
            mCursoClickListener.onCrsoClicked(curso)
        }
    }
}
