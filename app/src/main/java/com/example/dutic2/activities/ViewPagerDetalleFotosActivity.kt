package com.example.dutic2.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.dutic2.R
import com.example.dutic2.adapters.ViewPagerFotosAdpater
import com.example.dutic2.models.Curso
import com.example.dutic2.ui.cursoDetalles.PruebaFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_view_pager_detalle_fotos.*
import java.io.File

class ViewPagerDetalleFotosActivity : AppCompatActivity() {

    private var path = arrayListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager_detalle_fotos)
        try {
            path.addAll(intent.getSerializableExtra("paths") as ArrayList<File>)
        }catch (e: Exception){
            e.printStackTrace()
        }
        val adapter = ViewPagerFotosAdpater(this,path)
        view_pager.adapter = adapter
        view_pager.currentItem = intent.getIntExtra("pos",0)
        foto_compartir.setOnClickListener {
            comparirFoto()
        }
        val f = PruebaFragment()
        view_pager.setOnClickListener {
            if (supportFragmentManager.backStackEntryCount>=1){
                super.onBackPressed()

            }else{
                supportFragmentManager.beginTransaction().add(R.id.frame_to,f).addToBackStack("fck").commit()
                frame_to.requestFocus()
            }
            Log.e("Foto_detalle", "Enbtra a esda wea")
//            supportFragmentManager.beginTransaction().replace(R.id.frame_to,f).addToBackStack("fck").commit()
        }
        foto_eliminar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea eliminar esta foto?")
                .setCancelable(false)
                .setPositiveButton("Si") { _, _ -> eliminarFoto(view_pager.currentItem) }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            val alertDialog = builder.create()
            alertDialog.show()
        }


    }
    fun comparirFoto(){
        val f = path[view_pager.currentItem]
        val photoURI = FileProvider.getUriForFile(
            this,
            "com.example.dutic2.fileprovider",
            f
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, photoURI)
        intent.type = "image/*"
        startActivity(Intent.createChooser(intent, "Share Image:"))
    }
//    private fun detallesImagen() {
//
//        val dialog_detalles = Dialog_detalles()
//        dialog_detalles.setPhotoPath(fotos.get(viewPager.getCurrentItem()))
//        dialog_detalles.showToast(supportFragmentManager, "kappa")
//    }

    private fun eliminarFoto(currentItem: Int) {

        try {
            val f = path[currentItem]
            f.delete()
            val curso = intent.getParcelableExtra("curso") as Curso
            val user  = FirebaseAuth.getInstance().currentUser
            val uri= path[currentItem].toUri().lastPathSegment
            Log.e("Eliminacion", "/${user?.uid}/${curso.uid}/${uri}")
           FirebaseStorage.getInstance().getReference("/${user?.uid}/${curso.uid}/${uri}").delete().addOnSuccessListener {
                Log.e("Eliminacion", "Elemento $uri Eliminado correctament")
               path.removeAt(currentItem)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
