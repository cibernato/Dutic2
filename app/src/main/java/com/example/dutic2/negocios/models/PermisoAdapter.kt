package com.example.dutic2.negocios.models

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R

class PermisoAdapter(

    var permisos: ArrayList<Permiso>,
    var mPermisoClickListener: PermisoClickListener
) :
    RecyclerView.Adapter<PermisoAdapter.PermisoHolder>() {

    interface PermisoClickListener {
        fun onPermisoClick(permiso: Permiso)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PermisoHolder {
        return PermisoHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.permiso_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return permisos.size
    }

    override fun onBindViewHolder(holder: PermisoHolder, position: Int) {
        holder.bind(permisos[position])
    }

    inner class PermisoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nombre: TextView = itemView.findViewById<TextView>(R.id.permiso_nombre)
        var estado: TextView = itemView.findViewById<TextView>(R.id.permiso_estado)
        var codigo: TextView = itemView.findViewById<TextView>(R.id.permiso_codigo)
        fun bind(permiso: Permiso) {
            nombre.text = permiso.nombre
            codigo.text = permiso.codigo
            when (permiso.estado) {
                "a" -> {
                    estado.text = "Activo"
                    itemView.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.md_green_200))
                }
                "i" -> {
                    estado.text = "Inactivo"
                    itemView.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.md_yellow_200))
                }
                else -> {
                    estado.text = "Eliminado"
                    itemView.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.md_red_200))
                }
            }
            itemView.setOnClickListener {
                mPermisoClickListener.onPermisoClick(permiso)
            }
        }

    }
}