package com.example.dutic2.activities

import android.app.Activity
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dutic2.R
import com.example.dutic2.models.Curso
import com.example.dutic2.models.CursoViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class SharedMainViewModel : ViewModel() {
    val user = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseFirestore.getInstance().collection("/usuarios/${user?.uid}/cursos")
    val option = FirestoreRecyclerOptions.Builder<Curso>()
        .setQuery(ref, SnapshotParser<Curso?> { snapshot: DocumentSnapshot ->
            val retornar = snapshot.toObject(Curso::class.java)
            retornar?.uid = snapshot.id
            return@SnapshotParser retornar!!
        }).build()
    lateinit var text: String
    lateinit var mCursoClickListener: CursoViewHolder.CursoClickListener

    var cursosActualizados = MutableLiveData<Array<Curso>>().apply { value = arrayOf() }


    fun getFRA(): LiveData<FirestoreRecyclerAdapter<Curso, CursoViewHolder>> {
        return _fra
    }

    fun getCursosActualizados(): LiveData<Array<Curso>> {
        val t = _fra.value?.snapshots?.toTypedArray()
        cursosActualizados.postValue(t)
        return cursosActualizados
    }

    private val _fra by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Curso, CursoViewHolder>>().apply {
            Log.e("viewModel Shared", "entra ")
            value = object : FirestoreRecyclerAdapter<Curso, CursoViewHolder>(option) {

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.curso_list_item, parent, false)
                    val displaymetrics = DisplayMetrics()
                    (itemView.context as Activity).windowManager.defaultDisplay.getMetrics(
                        displaymetrics
                    )
                    val c = (itemView.context as Activity).resources.configuration.orientation
                    val deviceheight: Int = if (c == Configuration.ORIENTATION_LANDSCAPE) {
                        (displaymetrics.widthPixels / 6).toInt()
                    } else {
                        displaymetrics.widthPixels / 3

                    }
                    itemView.findViewById<ConstraintLayout>(R.id.curso_list_item_layout)
                        .layoutParams.height = deviceheight
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

                override fun onDataChanged() {
                    super.onDataChanged()
                    val t = this@apply.value?.snapshots?.toTypedArray()
                    cursosActualizados.postValue(t)
                    Log.e("viewModelShared fra", "Noptficia el cambio en datos ")
                }
            }
        }
    }
}