package com.example.dutic2.negocios.models

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R

class TrabajadorAdapter(
    var trabajadores: ArrayList<Trabajador>,
    var mTrabajadorViewHolderListener: TrabajadorViewHolderListener
) :
    RecyclerView.Adapter<TrabajadorAdapter.TrabajadorHolder>() {

    interface TrabajadorViewHolderListener {
        fun onClickTrabajador(trabajador: Trabajador)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrabajadorHolder {
        return TrabajadorHolder(LayoutInflater.from(parent.context).inflate(R.layout.permiso_list_item,parent,false))
    }

    override fun getItemCount(): Int {
        return trabajadores.size
    }

    override fun onBindViewHolder(holder: TrabajadorHolder, position: Int) {
        holder.bind(trabajadores[position])
    }
    inner class TrabajadorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nombre: TextView = itemView.findViewById<TextView>(R.id.permiso_nombre)
        var estado: TextView = itemView.findViewById<TextView>(R.id.permiso_estado)
        var codigo: TextView = itemView.findViewById<TextView>(R.id.permiso_codigo)
        fun bind(trabajador: Trabajador) {
            nombre.text = trabajador.nombre
            codigo.text = trabajador.codigo
            when (trabajador.estado) {
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
                mTrabajadorViewHolderListener.onClickTrabajador(trabajador)
            }


        }
    }
}