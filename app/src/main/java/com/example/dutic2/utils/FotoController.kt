package com.example.dutic2.utils

import com.airbnb.epoxy.EpoxyController
import com.example.dutic2.models.FotoModel_
import com.google.firebase.storage.StorageReference

class FotoController(var references: ArrayList<StorageReference>) : EpoxyController(){
    override fun buildModels() {
        references.forEach {
            FotoModel_().imageRes= it
        }

    }
}