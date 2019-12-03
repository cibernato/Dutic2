package com.example.dutic2.negocios.models

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R

class TransaccionAdapter(
    var transacciones: ArrayList<Transaccion>,
    var mTransaccionListener: TransaccionListener
) :
    RecyclerView.Adapter<TransaccionAdapter.TransaccionHolder>() {

    interface TransaccionListener{
        fun onClickTransaccion(model: Transaccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionHolder {
        return TransaccionHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.transaccion_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return transacciones.size
    }

    override fun onBindViewHolder(holder: TransaccionHolder, position: Int) {
        holder.bind(transacciones[position])
    }

    inner class TransaccionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var codigo: TextView = itemView.findViewById<TextView>(R.id.transaccion_codigo)
        var estado: TextView = itemView.findViewById<TextView>(R.id.transaccion_estado)
        var permiso: TextView = itemView.findViewById<TextView>(R.id.transaccion_permiso)
        var horas: TextView = itemView.findViewById<TextView>(R.id.transaccion_horas)
        var trabajador: TextView = itemView.findViewById<TextView>(R.id.transaccion_trabajador)
        fun bind(model: Transaccion) {
            codigo.text = model.codigo
            permiso.text = model.nombrePermiso
            horas.text = horas.text.toString().format(model.horas)
            trabajador.text = model.nombreTrabajador
            when (model.estado) {
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
                mTransaccionListener.onClickTransaccion(model)
            }
        }

    }
}