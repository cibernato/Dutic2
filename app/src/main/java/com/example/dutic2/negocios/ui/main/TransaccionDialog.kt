package com.example.dutic2.negocios.ui.main


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R
import com.example.dutic2.negocios.NegociosViewModel
import com.example.dutic2.negocios.models.Permiso
import com.example.dutic2.negocios.models.Trabajador
import com.example.dutic2.negocios.models.Transaccion
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_transaccion_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class TransaccionDialog : DialogFragment() {

    var nomPermisos = arrayListOf<String>()
    var nomTrabajdores = arrayListOf<String>()
    var permisos = arrayListOf<Permiso>()
    var trabajadores = arrayListOf<Trabajador>()
    var transaccion: Transaccion? = null
    lateinit var pAdapter: ArrayAdapter<String>
    lateinit var tAdapter: ArrayAdapter<String>
    private lateinit var negociosViewModel: NegociosViewModel
    lateinit var estados: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            negociosViewModel = ViewModelProviders.of(it).get(NegociosViewModel::class.java)
        }
        pAdapter =
            ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, nomPermisos)
        tAdapter =
            ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, nomTrabajdores)
        negociosViewModel.getPermisosActualizados().observe(this, Observer {
            val t = arrayListOf<Permiso>().apply { addAll(it) }
            t.forEach { permiso ->
                if (permiso.estado == "a") {
                    permisos.add(permiso)
                    nomPermisos.add(permiso.nombre)
                    pAdapter.notifyDataSetChanged()
                }
            }
        })
        negociosViewModel.getTrabajadoresActualizados().observe(this, Observer {
            val t = arrayListOf<Trabajador>().apply { addAll(it) }
            t.forEach { trabajador ->
                if (trabajador.estado == "a") {
                    trabajadores.add(trabajador)
                    nomTrabajdores.add(trabajador.nombre)
                    tAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_transaccion_dialog, container, false)
        val estadoSpinner = v.findViewById<Spinner>(R.id.estado_spinner)
        estados = resources.getStringArray(R.array.Estados)
        estadoSpinner.adapter = ArrayAdapter(
            context!!, android.R.layout.simple_spinner_dropdown_item,
            estados
        )
        v.findViewById<Spinner>(R.id.permiso_spinner).adapter = pAdapter
        v.findViewById<Spinner>(R.id.trabajador_spinner).adapter = tAdapter
        if (transaccion?.codigo.isNullOrEmpty()) {
            v.findViewById<Spinner>(R.id.permiso_spinner).setSelection(0)
            v.findViewById<Spinner>(R.id.trabajador_spinner).setSelection(0)
        } else {
            v.findViewById<Spinner>(R.id.permiso_spinner)
                .setSelection(permisos.indexOf(permisos.find {
                    (it.nombre == transaccion?.nombrePermiso)
                }))
            v.findViewById<Spinner>(R.id.trabajador_spinner)
                .setSelection(trabajadores.indexOf(trabajadores.find {
                    (it.nombre == transaccion?.nombreTrabajador)
                }))
        }
        v.findViewById<TextInputLayout>(R.id.horas_input).editText?.setText(transaccion?.horas)
        estadoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when {
                    estados[position] == "Eliminado" -> {
                        transaccion?.estado = "*"
                    }
                    estados[position] == "Inactivo" -> {
                        transaccion?.estado = "i"
                    }
                    else -> {
                        transaccion?.estado = "a"
                    }
                }
            }
        }
        v.findViewById<Spinner>(R.id.permiso_spinner).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    transaccion?.codigoPermiso = permisos[position].codigo
                    transaccion?.nombrePermiso = permisos[position].nombre
                    transaccion?.estadoPermiso = permisos[position].estado

                }
            }
        v.findViewById<Spinner>(R.id.trabajador_spinner).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    transaccion?.codigoTrabajador = trabajadores[position].codigo
                    transaccion?.estadoTrabajdor = trabajadores[position].estado
                    transaccion?.nombreTrabajador = trabajadores[position].nombre
                }
            }
        v.findViewById<Button>(R.id.aceptar_add_transaccion).setOnClickListener {
            if (horas_input.editText?.text.toString().isNotEmpty()) {
                sendResult()
                dismiss()
            } else {
                Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_LONG).show()

            }
        }
        v.findViewById<Button>(R.id.cancelar_add_transaccion).setOnClickListener {
            dismiss()
        }

        return v
    }

    fun sendResult() {
        transaccion?.horas = horas_input.editText?.text.toString()
        val intent = Intent().apply {
            putExtra("valor", transaccion)
        }
        targetFragment?.onActivityResult(targetRequestCode, 147, intent)
    }


    companion object {
        fun newInstance(transaccionR: Transaccion) = TransaccionDialog().apply {
            transaccion = transaccionR
        }
    }

}
