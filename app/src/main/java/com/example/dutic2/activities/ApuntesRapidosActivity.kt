package com.example.dutic2.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.dutic2.R
import com.example.dutic2.ui.temp.BlankFragment
import com.example.dutic2.utils.DIRECTORIO_RAIZ
import com.example.dutic2.utils.log
import com.example.dutic2.utils.toast
import kotlinx.android.synthetic.main.activity_apuntes_rapidos.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class ApuntesRapidosActivity : AppCompatActivity() {

    var showingFragment = false
    var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apuntes_rapidos)
        custom_content.setOnClickListener {
            showingFragment = if (!showingFragment) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.constraint_content, BlankFragment()).addToBackStack("").commit()
                true
            } else {
                supportFragmentManager.popBackStack()
                false
            }
        }
    }

    fun saveBitmap() {
        try {
            bitmap = Bitmap.createBitmap(custom_content.measuredWidth,custom_content.measuredHeight,Bitmap.Config.ARGB_8888)
            custom_content.draw(Canvas(bitmap!!))
            if (bitmap != null) {
                val uri = bitmapToFile(bitmap!!)
                log("URI guardado $uri")
                toast("Bitmap saved in a file.")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): Uri {
        var file = File(DIRECTORIO_RAIZ + File.separator)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

}
