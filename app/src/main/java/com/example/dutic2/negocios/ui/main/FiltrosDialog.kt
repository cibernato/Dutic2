package com.example.dutic2.negocios.ui.main


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.dutic2.R
import kotlinx.android.synthetic.main.fragment_filtros_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class FiltrosDialog : DialogFragment() {

    var filtros: ArrayList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_filtros_dialog, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if ("a" in filtros!!) activos_check.isChecked = true
        if ("i" in filtros!!) inactivos_check.isChecked = true
        if ("*" in filtros!!) eliminados_check.isChecked = true
        cancelar_filtros.setOnClickListener {
            dismiss()
        }
        aceptar_filtros.setOnClickListener {
            sendResult()
            dismiss()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun sendResult() {
        filtros?.clear()
        if (activos_check.isChecked) filtros?.add("a")
        if (inactivos_check.isChecked) filtros?.add("i")
        if (eliminados_check.isChecked) filtros?.add("*")
        val i = Intent().apply {
            putExtra("filtros", filtros)
        }
        targetFragment?.onActivityResult(targetRequestCode, 159, i)

    }

    companion object {
        fun newInstance(filtros: ArrayList<String>) = FiltrosDialog().apply {
            this.filtros = filtros
        }
    }

}
