package com.example.dutic2.ui.publicaciones


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.dutic2.R
import com.example.dutic2.models.Curso

/**
 * A simple [Fragment] subclass.
 */
class TareasEntregadasFragment : Fragment() {
    lateinit var curso :Curso

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tareas_entregadas, container, false)
    }

    companion object{
        fun newInstance(curso : Curso) = NovedadesFragment().apply {
            this.curso = curso
        }
    }

}
