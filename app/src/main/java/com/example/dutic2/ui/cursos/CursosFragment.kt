package com.example.dutic2.ui.cursos

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import android.widget.Toast
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
import com.example.dutic2.models.Curso
import com.example.dutic2.models.CursoViewHolder
import com.example.dutic2.utils.GlideApp
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_cursos.*

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
    var cursos: Array<Curso>? = arrayOf()
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_cursos, container, false)
        (activity as MainActivity).setColorBar(Color.parseColor("#43a047"))
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
        clickListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun clickListeners() {
        btn_apuntes.setOnClickListener {

        }
        btn_notas.setOnClickListener {

        }
        btn_recordatorios.setOnClickListener {

        }
        btn_otros.setOnClickListener {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_menu) {
            Toast.makeText(context, "Bien", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }



}