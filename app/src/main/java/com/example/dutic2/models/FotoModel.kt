package com.example.dutic2.models

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.example.dutic2.R
import com.example.dutic2.utils.GlideApp
import com.google.firebase.storage.StorageReference
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
            imageView = itemView.findViewById(R.id.list_item_foto)
            this.itemView = itemView
            itemView.setOnClickListener {
                listener.onClick(itemView)
            }
        }
    }
}