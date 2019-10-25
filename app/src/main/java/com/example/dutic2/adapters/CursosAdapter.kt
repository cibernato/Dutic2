package com.example.dutic2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.models.Curso

class CursosAdapter(val context: Context) : RecyclerView.Adapter<CursosAdapter.CursoHolder>() {
    private var cursos: List<Curso> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoHolder {
        return CursoHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.curso_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return cursos.size
    }

    override fun onBindViewHolder(holder: CursoHolder, position: Int) {
        holder.onBind(cursos[position])

    }

    fun setCursos(cursos: List<Curso>) {
        this.cursos = cursos
        notifyDataSetChanged()
    }

    inner class CursoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre : TextView = itemView.findViewById(R.id.curso_nombre_list_item)
        val profesor : TextView = itemView.findViewById(R.id.curso_profesor_list_item)
        fun onBind(curso :Curso){
            nombre.text = curso.nombre
            profesor.text = curso.profesor
        }
    }
}