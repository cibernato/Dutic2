package com.example.dutic2.ui.cursoDetalles

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController

import com.example.dutic2.R
import com.example.dutic2.activities.MainActivity
import com.example.dutic2.models.Curso
import com.example.dutic2.utils.GlideApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.curso_detalles_fragment.*

class CursoDetallesFragment : Fragment() {

    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var curso: Curso

    companion object {
        fun newInstance() = CursoDetallesFragment()
    }

    private lateinit var viewModel: CursoDetallesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        curso = arguments?.getSerializable("curso") as Curso
        return inflater.inflate(R.layout.curso_detalles_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inicializarVistas()
        viewModel = ViewModelProviders.of(this).get(CursoDetallesViewModel::class.java)

    }

    private fun inicializarVistas() {
        texto_bienvenida.text = getString(R.string.hola).format("${user?.displayName}")
        GlideApp.with(this).load(user?.photoUrl).into(foto_usuario)
        curso_detalles_nombre.text = getString(R.string.curso_nombre).format(curso.nombre)
        when {
            curso.tareasPendientes == "0" -> pendientes_curso.text = "No tiene tareas pendientes"
            curso.tareasPendientes == "1" -> pendientes_curso.text = "Tiene 1 tarea pendiente"
            else -> pendientes_curso.text =
                getString(R.string.tienes_dos_tareas_pendientes).format(curso.tareasPendientes?.toInt())
        }

        curso_detalles_calendario.setOnClickListener {
            val args =bundleOf("curso" to curso)
            findNavController ().navigate(R.id.nav_calendario2,args)
        }
        curso_detalle_imagenes.setOnClickListener {
            if (checkPermissionGranted()) {
                val args = bundleOf("curso" to curso)
                findNavController ().navigate(R.id.nav_cursoFotos,args)
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                }
            }

        }
        curso_detalles_notas_de_voz.setOnClickListener {
            val args =bundleOf("curso" to curso)
            findNavController().navigate(R.id.nav_notas_de_voz,args)
        }
        curso_detalles_publicaciones.setOnClickListener {
            findNavController().navigate(R.id.nav_temas)
        }

    }
    private fun checkPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (activity as MainActivity).supportActionBar?.title =
            ((arguments?.getSerializable("curso")) as Curso).nombre

        super.onCreateOptionsMenu(menu, inflater)
    }
}



