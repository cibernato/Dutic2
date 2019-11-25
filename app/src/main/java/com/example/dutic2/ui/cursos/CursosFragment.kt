package com.example.dutic2.ui.cursos

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.dutic2.models.Curso
import com.example.dutic2.models.CursoViewHolder
import com.example.dutic2.utils.GlideApp
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_cursos.*
import java.lang.Exception

class CursosFragment : Fragment(), CursoViewHolder.CursoClickListener,
    CursosViewModel.ItemsChangeNotifier {
    override fun onCrsoClicked(curso: Curso) {
        val args = bundleOf("curso" to curso)
        navController.navigate(R.id.nav_cursoDetallesFragment, args)
    }

    interface CursosFragmentListener {
        fun sendToActivity(cursos: Array<Curso>)
    }

    lateinit var cursosFragmentListener: CursosFragmentListener
    var firestoreRecyclerAdapter: FirestoreRecyclerAdapter<Curso, CursoViewHolder>? = null
    var cursos: Array<Curso>? = arrayOf()
    private lateinit var cursosViewModel: CursosViewModel
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
        val ref = FirebaseFirestore.getInstance().collection("/usuarios/${user?.uid}/cursos")
        val option = FirestoreRecyclerOptions.Builder<Curso>()
            .setQuery(ref, SnapshotParser<Curso?> { snapshot: DocumentSnapshot ->
                val retornar = snapshot.toObject(Curso::class.java)
                retornar?.uid = snapshot.id
                return@SnapshotParser retornar!!

            }).setLifecycleOwner(viewLifecycleOwner).build()
        cursosViewModel =
            ViewModelProviders.of(this).get(CursosViewModel::class.java).apply {
                this.option = option
                this.mCursoClickListener = this@CursosFragment
                this.text = getString(R.string.tienes_dos_tareas_pendientes)
                this.mItemsChangeNotifier = this@CursosFragment
            }
        navController = findNavController()
        texto_bienvenida.text = getString(R.string.hola).format("${user?.displayName}")
        GlideApp.with(this).load(user?.photoUrl).into(foto_usuario)

        recycler_view_cursos.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)


        cursosViewModel.getFRA()
            .observe(this, Observer {
                firestoreRecyclerAdapter = it
                recycler_view_cursos.adapter = firestoreRecyclerAdapter
                firestoreRecyclerAdapter?.startListening()
            })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        notifyFragment()
        super.onPause()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CursosFragmentListener) {
            cursosFragmentListener = context
        } else {
            throw RuntimeException("$context must implement CursosFragmentListener")
        }

    }

    override fun notifyFragment() {
        try {
            cursos = firestoreRecyclerAdapter?.snapshots?.toTypedArray()
            cursosFragmentListener.sendToActivity(cursos!!)
        } catch (e: Exception) {
            Log.e("erroe on pause", "$e")
        }
    }

}