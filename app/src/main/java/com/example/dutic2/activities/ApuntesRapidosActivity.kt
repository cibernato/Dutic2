package com.example.dutic2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import com.example.dutic2.R
import com.example.dutic2.utils.CustomCanvas

class ApuntesRapidosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myCustomCanvas = CustomCanvas(this)
        myCustomCanvas.systemUiVisibility =SYSTEM_UI_FLAG_FULLSCREEN
        myCustomCanvas.contentDescription = "Dibuje algo xd"
        setContentView(myCustomCanvas)

    }
}
