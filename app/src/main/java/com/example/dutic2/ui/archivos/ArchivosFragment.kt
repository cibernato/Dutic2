package com.example.dutic2.ui.archivos

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.dutic2.R
import com.example.dutic2.models.FotoModel
import com.example.dutic2.utils.FotoController
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.archivos_fragment.*
import com.airbnb.epoxy.OnModelClickListener

class ArchivosFragment : Fragment()  {


    companion object {
        fun newInstance() = ArchivosFragment()
    }

    private lateinit var viewModel: ArchivosViewModel
    lateinit var archivosController: FotoController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.archivos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ArchivosViewModel::class.java)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = fotos_recycler_view as EpoxyRecyclerView
        val references = arrayListOf<StorageReference>()
        anadir_foto.setOnClickListener {
            Log.e("EpoxyRecyclerVie"," ${recyclerView.adapter.toString()}, ${recyclerView.adapter?.itemCount}")
        }
        references.add(FirebaseStorage.getInstance().reference.child("default.jpg") )
        references.add(FirebaseStorage.getInstance().reference.child("default.jpg") )
//        recyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        recyclerView.layoutManager = GridLayoutManager(context,3)
        archivosController = FotoController(references)
        recyclerView.setControllerAndBuildModels(archivosController)
//        recyclerView.withModels {
//            this.apply {
//                references.forEach {
//                    FotoModel_().imageRes = it
//                }
//            }
//        }


        super.onViewCreated(view, savedInstanceState)
    }
//    override fun OnClick() {
//        archivosController
//        Toast.makeText(context,"Probando 1 2 3 xd ", Toast.LENGTH_SHORT).show()
//    }
}
