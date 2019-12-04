package com.example.dutic2.adapters

import android.app.Activity
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R

class DocumentoAdapter(
    var documentos: ArrayList<Uri>,
    var mOnDocumentoClickListener: OnDocumentoClickListener
) :
    RecyclerView.Adapter<DocumentoAdapter.DocumentoHolder>() {

    interface OnDocumentoClickListener {
        fun onDocumentoClick(documento: Uri)
//        fun onDocumentoLongClick(documento:Uri):Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentoHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.documento_list_item, parent, false)
        return DocumentoHolder(view)
    }

    override fun getItemCount(): Int {
        return documentos.size
    }

    override fun onBindViewHolder(holder: DocumentoHolder, position: Int) {
        holder.bind(documentos[position])
    }


    inner class DocumentoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.documento_list_item_nombre)
        val icono: ImageView = itemView.findViewById(R.id.documento_list_item_icono)
        fun bind(uri: Uri) {
            val n = uri.lastPathSegment
            nombre.text = n?.substring(0, n.lastIndexOf('.'))
            val displaymetrics = DisplayMetrics()
            (itemView.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
            val devicewidth: Int = displaymetrics.widthPixels / 3
            val deviceheight: Int = displaymetrics.heightPixels / 4
            icono.layoutParams.width = devicewidth
            icono.layoutParams.height = deviceheight
            when (n?.substring(n.lastIndexOf('.') + 1, n.length)) {
                "pdf" -> {
                    icono.setImageResource(R.drawable.ic_pdf_icon)
                }
                "ppt" -> {
                    icono.setImageResource(R.drawable.ic_ppt_icon)
                }
                "pptx" -> {
                    icono.setImageResource(R.drawable.ic_ppt_icon)
                }
                "doc" -> {
                    icono.setImageResource(R.drawable.ic_doc_icon)
                }
                "docx" -> {
                    icono.setImageResource(R.drawable.ic_doc_icon)
                }
                "xls" -> {
                    icono.setImageResource(R.drawable.ic_exel_icon)
                }
                "xlsx" -> {
                    icono.setImageResource(R.drawable.ic_exel_icon)
                }
            }
            itemView.setOnClickListener {
                mOnDocumentoClickListener.onDocumentoClick(uri)
            }
//            itemView.setOnLongClickListener {
//                mOnDocumentoClickListener.onDocumentoLongClick(uri)
//            }

        }
    }

}