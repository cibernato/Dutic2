package com.example.dutic2.utils

import android.util.Log
import android.widget.Toast
import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.OnModelClickListener
import com.example.dutic2.models.FotoModel
import com.example.dutic2.models.FotoModel_
import com.google.firebase.storage.StorageReference

class FotoController(var references: ArrayList<StorageReference>) : EpoxyController() {
    //@AutoModel
//lateinit var model: FotoModel_
    override fun buildModels() {
        references.forEach {
            FotoModel_().apply {
                imageRes = it
            }.id("${it.hashCode()}").listener { model, parentView, clickedView, position ->
                Log.e(
                    "funciona ",
                    " dtos $model , $clickedView , $position , $parentView"
                )
            }.addTo(this)
        }
    }
}