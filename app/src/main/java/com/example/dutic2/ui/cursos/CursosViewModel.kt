package com.example.dutic2.ui.cursos

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dutic2.R
import com.example.dutic2.models.Curso
import com.example.dutic2.models.CursoViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

class CursosViewModel : ViewModel() {
    lateinit var option: FirestoreRecyclerOptions<Curso>
    lateinit var text: String
    lateinit var mCursoClickListener: CursoViewHolder.CursoClickListener

    fun init(
        text: String,
        option: FirestoreRecyclerOptions<Curso>,
        mCursoClickListener: CursoViewHolder.CursoClickListener
    ) {
        this.option = option
        this.text = text
        this.mCursoClickListener = mCursoClickListener

    }


    fun getFRA(): LiveData<FirestoreRecyclerAdapter<Curso, CursoViewHolder>> {
        return _fra
    }

    private val _fra by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Curso, CursoViewHolder>>().apply {
            Log.e("VECES CONSULTANDO", "CONSUTLKADNO A FIREBASE FIRESTORE AVER ")
            value = object : FirestoreRecyclerAdapter<Curso, CursoViewHolder>(option) {

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
                    holder.bind(model, text, mCursoClickListener)
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.e("Firestore Adapter", "$e")
                    super.onError(e)
                }
            }
        }
    }
}