package com.example.dutic2.negocios.models

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R

class TransaccionAdapter(
    var transacciones: ArrayList<Transaccion>,
    var mTransaccionListener: TransaccionListener
) :
    RecyclerView.Adapter<TransaccionAdapter.TransaccionHolder>(), Filterable {

    var transaccionesTodos = arrayListOf<Transaccion>().apply { addAll(transacciones) }
    var transaccionesFiltradas = arrayListOf<Transaccion>().apply { addAll(transacciones) }

    interface TransaccionListener {
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

    override fun getFilter(): Filter {
        return transaccionFilter
    }

    fun filterBy(filtros: java.util.ArrayList<String>) {
        transacciones.clear()
        transaccionesFiltradas.clear()
        if (filtros.size == 0) {
            transacciones.addAll(transaccionesTodos)
        } else {
            transaccionesTodos.forEach {
                if (it.estado in filtros) {
                    transacciones.add(it)
                    transaccionesFiltradas.add(it)
                }
            }
        }
        notifyDataSetChanged()
    }

    var transaccionFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = arrayListOf<Transaccion>()
            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(transaccionesFiltradas)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()
                transaccionesFiltradas.forEach {
                    if (it.nombrePermiso.toLowerCase().contains(filterPattern) || it.nombreTrabajador.toLowerCase().contains(
                            filterPattern
                        )
                    ) {
                        filteredList.add(it)
                    }
                }
            }
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            transacciones.clear()
            transacciones.addAll(results?.values as ArrayList<Transaccion>)
            notifyDataSetChanged()
        }
    }
}