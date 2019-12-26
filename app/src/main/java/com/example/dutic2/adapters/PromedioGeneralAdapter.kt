package com.example.dutic2.adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.models.Curso
import com.example.dutic2.models.Promedio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PromedioGeneralAdapter(
    var cursos: ArrayList<Curso>,
    var mPromedioGeneralClickListener: PromedioGeneralClickListener
) :
    RecyclerView.Adapter<PromedioGeneralAdapter.PromedioGeneralHolder>() {
    var gson = Gson()

    interface PromedioGeneralClickListener {
        fun onPromedioGeneralListener(curso: Curso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromedioGeneralHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.promedio_general_list_item, parent, false)
        val displaymetrics = DisplayMetrics()
        (view.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
        val devicewidth: Int = displaymetrics.widthPixels / 3
        val deviceheight: Int = displaymetrics.heightPixels / 7
        val imageToResize =
            view.findViewById<AppCompatTextView>(R.id.promedio_general_list_item_nota)
        imageToResize.layoutParams.width = devicewidth
        imageToResize.layoutParams.height = deviceheight
        view.findViewById<TextView>(R.id.promedio_general_list_item_nota)
        return PromedioGeneralHolder(view)
    }

    override fun getItemCount(): Int {
        return cursos.size
    }

    override fun onBindViewHolder(holder: PromedioGeneralHolder, position: Int) {
        holder.bind(cursos[position])
    }


    inner class PromedioGeneralHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nota = itemView.findViewById<AppCompatTextView>(R.id.promedio_general_list_item_nota)
        var detalle =
            itemView.findViewById<AppCompatTextView>(R.id.promedio_general_list_item_detalle_curso)
        var titulo: TextView = itemView.findViewById(R.id.promedio_general_list_item_titulo_curso)
        var pBar = itemView.findViewById<ProgressBar>(R.id.promedio_general_list_item_progressBar)

        fun bind(curso: Curso) {
            titulo.text = curso.nombre
            nota.text = curso.promedioTotal

            try {

                val t = gson.fromJson<Array<Promedio>>(curso.promedios,
                    object : TypeToken<Array<Promedio>>() {}.type)
                var progreso = 0
                t.forEach {
                    progreso += it.porcentaje
                }
                pBar.progress = progreso
                Log.e("OnBind", "Comapra para cada actrualizacion, promedio $progreso")

                if (curso.promedioTotal!!.toDouble() > 10.5) {
                    pBar.progressDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
                    if (progreso in 50..99) {
                        detalle.text =
                            "Felicitaciones este curso ya esta aprobado, a subir esas notas."
                    } else if (progreso == 100) {
                        detalle.text = "Felicitaciones acabaste este curso."
                    }

                } else {
                    pBar.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                    detalle.text = "Ponle mas ganas falta poco."
                    nota.setBackgroundColor(Color.RED)

                }
                itemView.setOnClickListener {
                    mPromedioGeneralClickListener.onPromedioGeneralListener(curso)
                }
            }catch (e:Exception){
                Log.e("Error onBind()","$e")
            }
        }
    }
}