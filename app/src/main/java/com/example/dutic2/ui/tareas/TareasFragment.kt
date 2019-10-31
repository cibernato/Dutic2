package com.example.dutic2.ui.tareas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R
import com.example.dutic2.utils.GlideApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_tareas.*


class TareasFragment : Fragment() {

    private lateinit var tareasViewModel: TareasViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tareasViewModel =
            ViewModelProviders.of(this).get(TareasViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tareas, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        tareasViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val xd = FirebaseStorage.getInstance().reference.child("default.jpg")
        GlideApp.with(this).load(xd).into(imagen_prueba)
        super.onViewCreated(view, savedInstanceState)
    }
}