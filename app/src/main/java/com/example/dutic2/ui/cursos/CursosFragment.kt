package com.example.dutic2.ui.cursos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
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
        navController.navigate(R.id.nav_cursoDetallesFragment, args)   }


    lateinit var sharedMainViewModel: SharedMainViewModel
    var firestoreRecyclerAdapter: FirestoreRecyclerAdapter<Curso, CursoViewHolder>? = null
    var cursos: Array<Curso>? = arrayOf()
    //    private lateinit var cursosViewModel: CursosViewModel
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cursos, container, false)
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
        super.onViewCreated(view, savedInstanceState)
    }



}