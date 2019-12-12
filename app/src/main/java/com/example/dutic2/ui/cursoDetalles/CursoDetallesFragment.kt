package com.example.dutic2.ui.cursoDetalles

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.dutic2.R
import com.example.dutic2.activities.MainActivity
import com.example.dutic2.activities.SharedMainViewModel
import com.example.dutic2.models.Curso
import com.example.dutic2.utils.DIRECTORIO_RAIZ
import com.example.dutic2.utils.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.curso_detalles_fragment.*
import java.io.File

class CursoDetallesFragment : Fragment() {

    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var curso: Curso

    companion object {
        fun newInstance() = CursoDetallesFragment()
    }

    private lateinit var viewModel: SharedMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        curso = arguments?.getParcelable<Curso>("curso") as Curso
        var v = inflater.inflate(R.layout.curso_detalles_fragment, container, false)

        val displaymetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val deviceheight: Int = displaymetrics.heightPixels / 10

        v.findViewById<Button>(R.id.curso_detalles_notas_de_voz).layoutParams.height = deviceheight
        v.findViewById<Button>(R.id.curso_detalles_publicaciones).layoutParams.height = deviceheight
        v.findViewById<Button>(R.id.curso_detalles_archivos).layoutParams.height = deviceheight
        v.findViewById<Button>(R.id.curso_detalles_calendario).layoutParams.height = deviceheight
        v.findViewById<Button>(R.id.curso_detalle_imagenes).layoutParams.height = deviceheight

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inicializarVistas()
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(SharedMainViewModel::class.java)
        }
        cambiarTitulo()

    }

    private fun cambiarTitulo() {
        (activity as MainActivity).supportActionBar?.title = curso.nombre
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
            val args = bundleOf("curso" to curso)
            findNavController().navigate(R.id.nav_calendario2, args)
        }
        curso_detalle_imagenes.setOnClickListener {
            if (checkPermissionGranted()) {
                val args = bundleOf("curso" to curso)
                findNavController().navigate(R.id.nav_cursoFotos, args)
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                }
            }

        }
        val args = bundleOf("curso" to curso)
        curso_detalles_notas_de_voz.setOnClickListener {

            findNavController().navigate(R.id.nav_notas_de_voz, args)
        }
        curso_detalles_publicaciones.setOnClickListener {

            findNavController().navigate(R.id.nav_publicaciones, args)
        }
        curso_detalles_archivos.setOnClickListener {

            findNavController().navigate(R.id.nav_archivos, args)
        }

    }

    private fun checkPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.curso_detalles_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.eliminar_curso) {
            AlertDialog.Builder(context!!).setTitle("Confirmacion")
                .setMessage("Esta seguro que desea eliminar el curso\n Se eliminara TODOS los datos almacenados en la nube y localmente\n Esta operaciopn no se puede deshacer")
                .setPositiveButton("SI") { dialog, _ ->
                    eliminarCurso()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun eliminarCurso() {
        try {
            progressBar_curso_detalles.visibility = View.VISIBLE
            val c = File(DIRECTORIO_RAIZ + File.separator + curso.nombre)
            borrarCurso(c)
            Log.e("detalles eliminar", "${c.absolutePath} , ${c.listFiles()}")
            FirebaseFirestore.getInstance()
                .document("/usuarios/${FirebaseAuth.getInstance().currentUser?.uid}/cursos/${curso.uid}")
                .delete().addOnCompleteListener {
                    Toast.makeText(
                        context!!,
                        "Curso eliminado satisfactoriamente",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    findNavController().navigateUp()
                }
        }catch (e:Exception){
            Log.e("detalles eliminar", "${e.localizedMessage}")
        }
    }

    private fun borrarCurso(f: File) {
        if (f.isDirectory)
            for (child in f.listFiles())
                borrarCurso(child)
        f.delete()

    }
}



