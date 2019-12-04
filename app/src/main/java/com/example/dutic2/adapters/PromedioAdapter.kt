package com.example.dutic2.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.models.Promedio
import com.example.dutic2.ui.promedio.PromediosFragment
import com.google.android.material.textfield.TextInputLayout

class PromedioAdapter(
    var promedios
    : ArrayList<Promedio>,
    var mPromedioClickListener: PromedioClickListener,
    var promediosFragment: PromediosFragment
) : RecyclerView.Adapter<PromedioAdapter.PromedioViewHolder>() {

    interface PromedioClickListener {
        fun onPromedioClickListener()
        fun promedioDelete(promedio: Promedio, pos: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PromedioViewHolder {
        return PromedioViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.promedio_item_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return promedios.size
    }

    override fun onBindViewHolder(holder: PromedioViewHolder, position: Int) {
        holder.bind(promedios[position])
    }

    inner class PromedioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nota: TextInputLayout = itemView.findViewById(R.id.nota_promedio)
        var porcentaje: TextInputLayout = itemView.findViewById(R.id.porcentaje_promedio)

        fun bind(promedio: Promedio) {

            nota.editText?.setText(if (promedio.nota.toString() == "0") "" else promedio.nota.toString())
            porcentaje.editText?.setText(if (promedio.porcentaje.toString() == "0") "" else promedio.porcentaje.toString())
            itemView.findViewById<ImageButton>(R.id.delete_promedio).setOnClickListener {
                mPromedioClickListener.promedioDelete(promedio, adapterPosition)
            }
            val drawable =
                AppCompatResources.getDrawable(itemView.context, R.drawable.ic_error_icon)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            nota.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    try {
                        val temp = Integer.parseInt(p0.toString())
                        if (temp !in 0..20) {
                            nota.editText!!.setError(
                                "Numero entre 0 y 20",
                                drawable
                            )
                        }
                    } catch (e: Exception) {
                        nota.editText!!.setError("Ingrese nota", drawable)
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    nota.error = ""
                    try {
                        val temp = Integer.parseInt(p0.toString())
                        if (temp in 0..20)
                            promedio.nota = temp
                        if (!porcentaje.editText?.text?.isBlank()!!) {
                            mPromedioClickListener.onPromedioClickListener()
                        }
                    } catch (e: Exception) {
                        nota.editText!!.setError("Ingrese nota", drawable)
                    }
                }
            })

            porcentaje.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    try {
                        val temp = Integer.parseInt(p0.toString())
//                        val restante = promediosFragment.getRestante()
                        if (temp !in 0..100) {
                            porcentaje.editText!!.setError(
                                "Numero mayor a 0 y menor a 100",
                                drawable
                            )
                        }
                    } catch (e: Exception) {
                        porcentaje.editText!!.setError("Ingrese porcentaje", drawable)
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    porcentaje.error = ""
                    try {
                        val temp = Integer.parseInt(p0.toString())
                        if (temp in 0 until 100)
                            promedio.porcentaje = temp
                        if (!nota.editText?.text?.isBlank()!!) {
                            mPromedioClickListener.onPromedioClickListener()
                        }
                    } catch (e: Exception) {
                        porcentaje.editText!!.setError("Ingrese porcentaje", drawable)
                    }
                }
            })
        }
    }

}