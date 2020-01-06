package com.example.dutic2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import com.example.dutic2.R
import com.example.dutic2.ui.temp.BlankFragment
import com.example.dutic2.utils.CustomCanvas
import kotlinx.android.synthetic.main.activity_apuntes_rapidos.*

class ApuntesRapidosActivity : AppCompatActivity() {

    var showingFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val myCustomCanvas = CustomCanvas(this)
//        myCustomCanvas.systemUiVisibility =SYSTEM_UI_FLAG_FULLSCREEN
//        myCustomCanvas.contentDescription = "Dibuje algo xd"
        setContentView(R.layout.activity_apuntes_rapidos)

        custom_content.setOnClickListener{
            showingFragment = if(!showingFragment){
                supportFragmentManager.beginTransaction().replace(R.id.constraint_content,BlankFragment()).addToBackStack("").commit()
                Log.e("onCreate","deberia agregagr")
                true
            }else{
                supportFragmentManager.popBackStack()
                Log.e("onCreate","deberia borrar")
                false
            }
        }
    }

}
