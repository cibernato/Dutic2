package com.example.dutic2.ui.promedio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.adapters.PromedioAdapter
import com.example.dutic2.models.Curso
import com.example.dutic2.models.Promedio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_promedio.*

class PromediosFragment : Fragment(), PromedioAdapter.PromedioClickListener {

    lateinit var promedio_adapter: PromedioAdapter
    var CABECERA = "Usted tiene %.2f acumulado y tiene el  %d  de sus notas"
    var lista_promedios: ArrayList<Promedio> = arrayListOf()
    private lateinit var curso: Curso
    private var gson = Gson()
    var porcentajeAcumulado = 0
    var notaAcumulada = 0.0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        curso = arguments?.getParcelable<Curso>("curso") as Curso
        return inflater.inflate(R.layout.fragment_promedio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val array: Array<Promedio> =
            gson.fromJson(curso.promedios, object : TypeToken<Array<Promedio>>() {}.type)
        lista_promedios.addAll(array)
        Log.e("onViewCreated", "after ${lista_promedios}")
        promedio_adapter = PromedioAdapter(lista_promedios, this, this)
        add_promedio.setOnClickListener { addPromedio() }
        recycler_view_promedios.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view_promedios.adapter = promedio_adapter
        mostrarTotal()
    }

    override fun onPromedioClickListener() {
        mostrarTotal()
    }

    private fun mostrarTotal() {
        notaAcumulada = 0.0
        porcentajeAcumulado = 0
        lista_promedios.forEach {
            porcentajeAcumulado += it.porcentaje
            notaAcumulada += (it.nota.toDouble() * (it.porcentaje.toDouble()) / 100.0)
        }
        promedio_calculado.text = String.format(CABECERA, notaAcumulada, porcentajeAcumulado)
    }

    override fun promedioDelete(promedio: Promedio, pos: Int) {
        lista_promedios.remove(promedio)
        promedio_adapter.notifyItemRemoved(pos)
        mostrarTotal()
    }

    private fun addPromedio() {
        lista_promedios.add(Promedio(0, 0))
        Log.e("AddPromedio", "despues ${lista_promedios}")
        promedio_adapter.notifyItemInserted(lista_promedios.size)
        mostrarTotal()
    }

    override fun onStop() {
        val array = lista_promedios.toTypedArray()
        val envio = gson.toJson(array)
        val calendarioReferencia =
            FirebaseFirestore.getInstance()
                .document("/usuarios/${FirebaseAuth.getInstance().currentUser?.uid}/cursos/${curso.uid}")
        Log.e("OnStop Promedio", "Guardando : $array , en JSON : $envio")
        calendarioReferencia.set(hashMapOf("promedios" to envio, "promedioTotal" to "%.2f".format(notaAcumulada)), SetOptions.merge())
            .addOnCompleteListener {
                Log.e("OnStop Promedio", "Guardado exitosamente")
            }.addOnFailureListener {
            Log.e("OnStop Promedio", "eeror $it")
        }
        super.onStop()
    }

//    fun getRestante()= 100- porcentajeAcumulado
}