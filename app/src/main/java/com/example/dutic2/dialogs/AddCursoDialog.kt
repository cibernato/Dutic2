package com.example.dutic2.dialogs


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.dutic2.R
import com.example.dutic2.activities.SharedMainViewModel
import com.example.dutic2.models.Curso
import kotlinx.android.synthetic.main.fragment_add_curso_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class AddCursoDialog : DialogFragment() {

    var cursoRecibido: Curso? = null
    var cursos = arrayListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_curso_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        aceptar_add_curso_dialog.setOnClickListener {
            if (datosSonValidos()) {
                sendResults()
                dismiss()
            } else {
                Toast.makeText(context!!, "Llene todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        cancelar_add_curso_dialog.setOnClickListener {
            dismiss()
        }
        activity?.let {
            ViewModelProviders.of(it).get(SharedMainViewModel::class.java)
                .getCursosActualizados().observe(this, Observer {
                    cursos.clear()
                    val x = arrayListOf<Curso>().apply { addAll(it) }
                    x.forEach {
                        cursos.add(it.nombre!!)
                    }
                })
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun sendResults() {
        cursoRecibido?.nombre = nombre_curso_dialog.editText!!.text.toString().trim().capitalize()
        cursoRecibido?.profesor =
            profesor_curso_dialog.editText!!.text.toString().trim().capitalize()
        val intent = Intent().apply {
            putExtra("curso", cursoRecibido)
        }
        targetFragment?.onActivityResult(targetRequestCode, 168, intent)

    }

    private fun datosSonValidos(): Boolean {
        return nombre_curso_dialog.editText!!.text.toString().isNotEmpty() and profesor_curso_dialog.editText!!.text.toString().isNotEmpty() &&
                nombre_curso_dialog.editText!!.text.toString() !in cursos
    }

    companion object {
        fun newInstance(curso: Curso) = AddCursoDialog().apply { cursoRecibido = curso }
    }


}
