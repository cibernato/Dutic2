package com.example.dutic2.utils

import android.util.Log
import com.airbnb.epoxy.EpoxyController
import com.example.dutic2.models.FotoModel_
import java.io.File
import java.util.*

class FotoController(var references: MutableList<File>, var mEpoxyClickListener: EpoxyClickListener) : EpoxyController() {

    interface EpoxyClickListener{
        fun onClickEpoxyModel(
            model: FotoModel_,
            position: Int
        )
    }
    override fun buildModels() {
        references.forEach {
            FotoModel_().apply {
                imageRes = it
            }.id("${UUID.randomUUID()}").listener { model, _, _, position ->
                mEpoxyClickListener.onClickEpoxyModel(model, position)
                Log.e("Model", "Model . $model")
            }.addTo(this)
        }
    }
}