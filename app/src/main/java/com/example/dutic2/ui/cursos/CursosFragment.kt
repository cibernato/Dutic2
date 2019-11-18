package com.example.dutic2.ui.cursos

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.adapters.CursosAdapter
import com.example.dutic2.models.Curso
import com.example.dutic2.utils.GlideApp
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.fragment_cursos.*
import java.io.File

class CursosFragment : Fragment() {

    private lateinit var cursosViewModel: CursosViewModel
    lateinit var cursoAdapter: CursosAdapter
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

         }*/
        cursosViewModel =
            ViewModelProviders.of(this).get(CursosViewModel::class.java)
        //        val textView: TextView = root.findViewById(R.id.text_home)
//        cursoAdapter = CursosAdapter(context!!)
//        cursosViewModel.getCursos().observe(this, Observer<ArrayList<Curso>> {
//            cursoAdapter.setCursos(it)
//        })
//        cursosViewModel.text.observe(this, Observer {
//            //            textView.text = it
//            textView.text = it
//        })
        return inflater.inflate(R.layout.fragment_cursos, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        texto_bienvenida.text = getString(R.string.hola).format("${user?.displayName}")
        GlideApp.with(this).load(user?.photoUrl).into(foto_usuario)
        val ref = FirebaseFirestore.getInstance().collection("/usuarios/${user?.uid}/cursos")
        val option = FirestoreRecyclerOptions.Builder<Curso>()
            .setQuery(ref, SnapshotParser<Curso?> { snapshot: DocumentSnapshot ->
                val retornar = snapshot.toObject(Curso::class.java)
                retornar?.uid = snapshot.id
                return@SnapshotParser retornar!!

            }).setLifecycleOwner(viewLifecycleOwner).build()

        val firestoreRecyclerAdapter =
            object : FirestoreRecyclerAdapter<Curso, CursoViewHolder>(option),
                CursoViewHolder.CursoClickListener {
                override fun onCrsoClicked(curso: Curso) {
                    val args = bundleOf("curso" to curso)
                    navController.navigate(R.id.nav_cursoDetallesFragment, args)
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.curso_list_item, parent, false)
                    return CursoViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: CursoViewHolder,
                    position: Int,
                    model: Curso
                ) {
                    holder.bind(model, getString(R.string.tienes_dos_tareas_pendientes), this)
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.e("Firestore Adapter", "$e")
                    super.onError(e)
                }
            }
        recycler_view_cursos.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view_cursos.adapter = firestoreRecyclerAdapter
        firestoreRecyclerAdapter.startListening()
        super.onViewCreated(view, savedInstanceState)
    }

    class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nom = itemView.findViewById<TextView>(R.id.curso_nombre_list_item)
        var prof = itemView.findViewById<TextView>(R.id.curso_profesor_list_item)
        var pendientes = itemView.findViewById<TextView>(R.id.curso_list_item_pendientes)

        interface CursoClickListener {
            fun onCrsoClicked(curso: Curso)
        }

        fun bind(curso: Curso, string: String, mCursoClickListener: CursoClickListener) {
            nom.text = curso.nombre
            prof.text = curso.profesor
            when {
                curso.tareasPendientes == "0" -> pendientes.text = "No tiene tareas pendientes"
                curso.tareasPendientes == "1" -> pendientes.text = "Tiene 1 tarea pendiente"
                else -> pendientes.text = string.format(curso.tareasPendientes?.toInt())
            }
            itemView.setOnClickListener {
                mCursoClickListener.onCrsoClicked(curso)
            }
        }
    }


}