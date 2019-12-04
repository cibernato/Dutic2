package com.example.dutic2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R

class AdjuntosAdapter(var adjuntos: List<String>) :
    RecyclerView.Adapter<AdjuntosAdapter.AdjuntosHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdjuntosHolder {
        return AdjuntosHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adjunto_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return adjuntos.size
    }

    override fun onBindViewHolder(holder: AdjuntosHolder, position: Int) {
        holder.onBind(adjuntos[position])
    }

    inner class AdjuntosHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val t: TextView = itemView.findViewById(R.id.nombre_adjunto_list_item)
        fun onBind(s: String) {
            t.text = s

        }
    }

}