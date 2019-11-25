package com.example.dutic2.ui.publicaciones

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dutic2.R
import com.example.dutic2.models.Curso
import com.example.dutic2.models.Publicacion
import com.example.dutic2.models.PublicacionViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

class NovedadesViewModel:ViewModel() {
    lateinit var cursoModel: Curso
    lateinit var option: FirestoreRecyclerOptions<Publicacion>
    lateinit var text: String
    var isTarea = false
    lateinit var mPublicacionClickListener: PublicacionViewHolder.PublicacionClickListener

    fun getFRA(): LiveData<FirestoreRecyclerAdapter<Publicacion, PublicacionViewHolder>> {
        return _fra
    }
    private val _fra by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Publicacion, PublicacionViewHolder>>().apply {
            value = object : FirestoreRecyclerAdapter<Publicacion, PublicacionViewHolder>(option) {

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.novedades_list_item, parent, false)
                    if (isTarea){
                        itemView.background= AppCompatResources.getDrawable(itemView.context,R.drawable.contenido)
                        return PublicacionViewHolder(itemView)
                    }
                    return PublicacionViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: PublicacionViewHolder,
                    position: Int,
                    model: Publicacion
                ) {
                    holder.bind(model,  mPublicacionClickListener)
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.e("Firestore Adapter", "$e")
                    super.onError(e)
                }
            }
        }
    }


}