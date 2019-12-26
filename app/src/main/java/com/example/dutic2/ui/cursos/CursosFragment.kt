package com.example.dutic2.ui.cursos

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.activities.MainActivity
import com.example.dutic2.activities.SharedMainViewModel
import com.example.dutic2.dialogs.AddCursoDialog
import com.example.dutic2.models.Curso
import com.example.dutic2.models.CursoViewHolder
import com.example.dutic2.utils.GlideApp
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_cursos.*
import java.util.*

class CursosFragment : Fragment(), CursoViewHolder.CursoClickListener {
    override fun onCrsoClicked(curso: Curso) {
        val args = bundleOf("curso" to curso)
        navController.navigate(R.id.nav_cursoDetallesFragment, args)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    lateinit var sharedMainViewModel: SharedMainViewModel
    var firestoreRecyclerAdapter: FirestoreRecyclerAdapter<Curso, CursoViewHolder>? = null
    var cursos: ArrayList<Curso>? = arrayListOf()
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_cursos, container, false)
        (activity as MainActivity).setColorBar(resources.getColor(R.color.colorPrimary))
        val displaymetrics = DisplayMetrics()
        val devicewidth: Int
        activity!!.windowManager.defaultDisplay.getMetrics(displaymetrics)
        devicewidth =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                displaymetrics.widthPixels / 10
            } else {
                displaymetrics.widthPixels / 6

            }
        v.findViewById<ImageView>(R.id.foto_usuario).layoutParams.width = devicewidth
        v.findViewById<ImageView>(R.id.foto_usuario).layoutParams.height = devicewidth
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let {
            sharedMainViewModel =
                ViewModelProviders.of(it).get(SharedMainViewModel::class.java).apply {
                    this.mCursoClickListener = this@CursosFragment
                    this.text = getString(R.string.tienes_dos_tareas_pendientes)
                }
        }
        navController = findNavController()
        texto_bienvenida.text = getString(R.string.hola).format("${user?.displayName}")
        GlideApp.with(this).load(user?.photoUrl).into(foto_usuario)

        recycler_view_cursos.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        sharedMainViewModel.getFRA()
            .observe(this, Observer {
                firestoreRecyclerAdapter = it
                recycler_view_cursos.adapter = firestoreRecyclerAdapter
                firestoreRecyclerAdapter?.startListening()
            })
        sharedMainViewModel.getCursosActualizados().observe(this, Observer {
            cursos?.clear()
            cursos?.addAll(it)
        })
        clickListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun clickListeners() {
        btn_apuntes.setOnClickListener {
            val args = Bundle()
            var cursos: Array<Curso>? = null
            activity?.let {
                ViewModelProviders.of(it).get(SharedMainViewModel::class.java)
                    .getCursosActualizados().observe(this,
                    Observer { array ->
                        cursos = array
                    })
            }
            try {
                args.apply {
                    putParcelableArray("cursos", cursos)
                    putString("flag", "voz")
                }
                findNavController().navigate(R.id.nav_plantilla, args)
            } catch (e: java.lang.Exception) {
                Log.e("Error en try", "$e, values $cursos , args $args")
            }
        }
        btn_notas.setOnClickListener {
            val args = Bundle()
            try {
                args.apply {
                    putParcelableArray("cursos", cursos?.toTypedArray())
                    putString("flag", "voz")
                }
                navController.navigate(R.id.nav_promediosGeneral, args)
            } catch (e: java.lang.Exception) {
                Log.e("Error en try", "$e, values $cursos , args $args")
            }
        }
        btn_recordatorios.setOnClickListener {
            //Toast.makeText(context!!, "Funcionalidad extra", Toast.LENGTH_LONG).show()
            val args = Bundle()
            var cursos: Array<Curso>? = null
            activity?.let {
                ViewModelProviders.of(it).get(SharedMainViewModel::class.java)
                    .getCursosActualizados().observe(this,
                    Observer { array ->
                        cursos = array
                    })
            }
            try {
                args.apply {
                    putParcelableArray("cursos", cursos)
                    putString("flag", "voz")
                }
                findNavController().navigate(R.id.nav_plantilla, args)
            } catch (e: java.lang.Exception) {
                Log.e("Error en try", "$e, values $cursos , args $args")
            }
        }
        btn_otros.setOnClickListener {
            val args = Bundle()
            var cursos: Array<Curso>? = null
            activity?.let {
                ViewModelProviders.of(it).get(SharedMainViewModel::class.java)
                    .getCursosActualizados().observe(this,
                    Observer { array ->
                        cursos = array
                    })
            }
            try {
                args.apply {
                    putParcelableArray("cursos", cursos)
                    putString("flag", "voz")
                }
                findNavController().navigate(R.id.nav_plantilla, args)
            } catch (e: java.lang.Exception) {
                Log.e("Error en try", "$e, values $cursos , args $args")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_menu) {
            val d = AddCursoDialog.newInstance(Curso())
            d.setTargetFragment(this, 168)
            d.show(fragmentManager!!, "s")

        }
        return super.onOptionsItemSelected(item)
    }


    private fun addCurso(cursoPorSubir: Curso) {
        FirebaseFirestore.getInstance().collection("/usuarios/${user?.uid}/cursos")
            .add(cursoPorSubir).addOnSuccessListener {
                Log.e("Prueba de subida", it.id)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 168) {
            addCurso(data?.getParcelableExtra<Curso>("curso")!!)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}