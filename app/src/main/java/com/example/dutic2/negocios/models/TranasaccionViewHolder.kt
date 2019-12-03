package com.example.dutic2.negocios.models

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R


class TranasaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var codigo: TextView = itemView.findViewById<TextView>(R.id.transaccion_codigo)
    var estado: TextView = itemView.findViewById<TextView>(R.id.transaccion_estado)
    var permiso: TextView = itemView.findViewById<TextView>(R.id.transaccion_permiso)
    var horas: TextView = itemView.findViewById<TextView>(R.id.transaccion_horas)
    var trabajador: TextView = itemView.findViewById<TextView>(R.id.transaccion_trabajador)
    fun bind(model: Transaccion) {
        codigo.text = model.codigo
        permiso.text = model.nombrePermiso
        horas.text = model.horas
        trabajador.text = model.nombreTrabajador
        when (model.estado) {
            "a" -> {
                estado.text = "Activo"
                itemView.background = ColorDrawable(itemView.context.resources.getColor(R.color.md_green_200))
            }
            "i" -> {
                estado.text = "Inactivo"
                itemView.background = ColorDrawable(itemView.context.resources.getColor(R.color.md_yellow_200))
            }
            else -> {
                estado.text = "Eliminado"
                itemView.background = ColorDrawable(itemView.context.resources.getColor(R.color.md_red_200))
            }
        }

    }
}