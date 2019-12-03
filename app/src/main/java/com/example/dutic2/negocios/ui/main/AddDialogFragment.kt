package com.example.dutic2.negocios.ui.main


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment

import com.example.dutic2.R
import com.example.dutic2.negocios.models.Permiso
import com.example.dutic2.negocios.models.Trabajador
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_add_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class AddDialogFragment : DialogFragment() {

    var recibido: Any? = null
    lateinit var text: TextInputLayout
    lateinit var estados: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_add_dialog, container, false)
        estados = resources.getStringArray(R.array.Estados)
        val spinner = v.findViewById<Spinner>(R.id.dialog_spinner)
        spinner.adapter =
            ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, estados)
        spinner.setSelection(1)
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (recibido is Trabajador) {
                    Log.e("ptmr","que cosa recibe ${estados[position]}, ${estados[position] == "Eliminado"}")
                    when {
                        estados[position] == "Eliminado" -> {
                            (recibido as Trabajador).estado = "*"
                        }
                        estados[position] == "Inactivo" -> {
                            (recibido as Trabajador).estado = "i"
                        }
                        else -> {
                            (recibido as Trabajador).estado = "a"
                        }
                    }
                }
                if (recibido is Permiso) {
                    when {
                        estados[position] == "Activo" -> {
                            (recibido as Permiso).estado = "a"
                        }
                        estados[position] == "Inactivo" -> {
                            (recibido as Permiso).estado = "i"
                        }
                        else -> {
                            (recibido as Permiso).estado = "*"
                        }
                    }
                }
            }
        }
        text = v.findViewById(R.id.add_dialog_text)
        if (recibido is Trabajador) {
            text.editText?.setText((recibido as Trabajador).nombre)
            when ((recibido as Trabajador).estado) {
                "a" -> {
                    spinner.setSelection(0)
                }
                "i" -> {
                    spinner.setSelection(1)
                }
                else -> {
                    spinner.setSelection(2)
                }
            }
            if ((recibido as Trabajador).codigo.isEmpty()){
                spinner.setSelection(0)
            }
        }
        if (recibido is Permiso) {
            text.editText?.setText((recibido as Permiso).nombre)
            when ((recibido as Permiso).estado) {
                "a" -> {
                    spinner.setSelection(0)
                }
                "i" -> {
                    spinner.setSelection(1)
                }
                else -> {
                    spinner.setSelection(2)
                }
            }
            if ((recibido as Permiso).codigo.isEmpty()){
                spinner.setSelection(0)
            }
        }

        v.findViewById<Button>(R.id.aceptar_dialog_button).setOnClickListener {
            if(text.editText!!.text.toString().isNotEmpty()){
                sendResult()
                dismiss()
            }else{
                Toast.makeText(context,"Coloque un nombre",Toast.LENGTH_LONG).show()
            }
        }
        v.findViewById<Button>(R.id.cancelar_button_dialog).setOnClickListener {
            dismiss()
        }

        return v
    }

    companion object {
        fun newInstance(permiso: Permiso) = AddDialogFragment().apply {
            recibido = permiso
        }

        fun newInstance(trabajador: Trabajador) = AddDialogFragment().apply {
            recibido = trabajador
        }
    }

    fun sendResult() {
        if (recibido is Trabajador) (recibido as Trabajador).nombre =
            text.editText!!.text.toString()
        if (recibido is Permiso) (recibido as Permiso).nombre = text.editText!!.text.toString()
        val intent = Intent().apply {

            putExtra("valor", recibido as Parcelable)

        }
        targetFragment?.onActivityResult(targetRequestCode, 123, intent)
    }
}
