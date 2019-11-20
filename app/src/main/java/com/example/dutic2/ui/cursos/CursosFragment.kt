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
import com.example.dutic2.adapters.CursosAdapter
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

class CursosFragment : Fragment(), CursoViewHolder.CursoClickListener {
    override fun onCrsoClicked(curso: Curso) {
        val args = bundleOf("curso" to curso)
        navController.navigate(R.id.nav_cursoDetallesFragment, args)
    }

    private lateinit var cursosViewModel: CursosViewModel
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* FirebaseAuth.AuthStateListener {
             val user = it.currentUser
             if (user != null) {
                 Toast.makeText(context, "logged in", Toast.LENGTH_SHORT).show()
             } else {
                 val providers = arrayListOf(
                     AuthUI.IdpConfig.EmailBuilder().build(),
                     AuthUI.IdpConfig.GoogleBuilder().build()
                 )
                 startActivityForResult(
                     AuthUI.getInstance()
                         .createSignInIntentBuilder()
                         .setAvailableProviders(providers)
                         .setTheme(R.style.LoginTheme)
                         .setLogo(R.mipmap.logo)
                         .setIsSmartLockEnabled(false)
                         .build(),
                     589
                 )
             }

         }
         val textView: TextView = root.findViewById(R.id.text_home)
        cursoAdapter = CursosAdapter(context!!)
        cursosViewModel.getCursos().observe(this, Observer<ArrayList<Curso>> {
            cursoAdapter.setCursos(it)
        })
        cursosViewModel.text.observe(this, Observer {
            //            textView.text = it
            textView.text = it
        })
         */
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
            }
        navController = findNavController()
        texto_bienvenida.text = getString(R.string.hola).format("${user?.displayName}")
        GlideApp.with(this).load(user?.photoUrl).into(foto_usuario)

        recycler_view_cursos.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        var firestoreRecyclerAdapter: FirestoreRecyclerAdapter<Curso, CursoViewHolder>?
        cursosViewModel.getFRA()
            .observe(this, Observer {
                firestoreRecyclerAdapter = it
                recycler_view_cursos.adapter = firestoreRecyclerAdapter
                firestoreRecyclerAdapter?.startListening()
            })
        super.onViewCreated(view, savedInstanceState)
    }


}