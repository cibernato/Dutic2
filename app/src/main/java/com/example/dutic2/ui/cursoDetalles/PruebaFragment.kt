package com.example.dutic2.ui.cursoDetalles


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dutic2.R
import kotlinx.android.synthetic.main.fragment_prueba.*

/**
 * A simple [Fragment] subclass.
 */
class PruebaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prueba, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageButton.setOnClickListener {
            Log.e("Boton en fragmen","Funciona ")
            Toast.makeText(context,"Amonos causa",Toast.LENGTH_LONG).show()
        }
    }
}
