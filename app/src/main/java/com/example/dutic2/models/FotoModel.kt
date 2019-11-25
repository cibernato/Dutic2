package com.example.dutic2.models

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.dutic2.R
import com.example.dutic2.utils.GlideApp
import java.io.File

@EpoxyModelClass(layout = R.layout.foto_model)
abstract class FotoModel : EpoxyModelWithHolder<FotoModel.FotoHolder>() {

    @EpoxyAttribute
    lateinit var listener : View.OnClickListener
    @EpoxyAttribute
    lateinit var imageRes: File


    override fun bind(holder: FotoHolder) {

        GlideApp.with(holder.itemView).load(imageRes).into(holder.imageView)
    }

    inner class FotoHolder : EpoxyHolder() {

        lateinit var imageView: ImageView
        lateinit var itemView: View

        override fun bindView(itemView: View) {

            val displaymetrics = DisplayMetrics()
            (itemView.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
            val devicewidth: Int = displaymetrics.widthPixels / 3
            val deviceheight: Int = displaymetrics.heightPixels / 3
            itemView.layoutParams.width = devicewidth
            itemView.layoutParams.height = deviceheight
            imageView = itemView.findViewById(R.id.list_item_foto)
            imageView.layoutParams.height
            this.itemView = itemView
            itemView.setOnClickListener {
                listener.onClick(itemView)
            }
        }
    }
}