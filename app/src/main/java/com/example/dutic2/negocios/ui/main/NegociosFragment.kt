package com.example.dutic2.negocios.ui.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dutic2.R
import kotlinx.android.synthetic.main.fragment_negocios.*

/**
 * A simple [Fragment] subclass.
 */
class NegociosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_negocios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trabajadores_button.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.negocios_content,
                TrabajadoresFragment()
            ).addToBackStack("1").commit()
        }
        permisos_button.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.negocios_content,
                PermisosFragment()
            ).addToBackStack("12").commit()
        }
        transacciones_button.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.negocios_content,
                TransaccionesFragment()
            ).addToBackStack("13").commit()
        }

    }
}
