package com.example.dutic2.ui.tareas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R


class TareasFragment : Fragment() {

    private lateinit var tareasViewModel: TareasViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tareasViewModel =
            ViewModelProviders.of(this).get(TareasViewModel::class.java)
        return inflater.inflate(R.layout.fragment_tareas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val xd = FirebaseStorage.getInstance().reference.child("default.jpg")
//        GlideApp.with(this).load(xd).into(imagen_prueba)
        super.onViewCreated(view, savedInstanceState)
    }
}