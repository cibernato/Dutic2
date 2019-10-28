package com.example.dutic2.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.adapters.CursosAdapter
import com.example.dutic2.models.Curso
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var cursoAdapter: CursosAdapter


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
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
//        cursoAdapter = CursosAdapter(context!!)
//        homeViewModel.getCursos().observe(this, Observer<ArrayList<Curso>> {
//            cursoAdapter.setCursos(it)
//        })
        homeViewModel.text.observe(this, Observer {
            //            textView.text = it
            textView.text = it
        })


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val ref = FirebaseFirestore.getInstance().collection("/testing/pruebas de envio/prueba 1")
        val option = FirestoreRecyclerOptions.Builder<Curso>()
            .setQuery(ref, Curso::class.java).setLifecycleOwner(viewLifecycleOwner).build()

        val firestoreRecyclerAdapter =
            object : FirestoreRecyclerAdapter<Curso, CursoViewHolder>(option) {

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.curso_list_item, parent, false)
                    return CursoViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: CursoViewHolder, position: Int, model: Curso) {
                    holder.bind(model)
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
        fun bind(curso: Curso) {
            nom.text = curso.nombre
            prof.text = curso.profesor
        }


    }
}