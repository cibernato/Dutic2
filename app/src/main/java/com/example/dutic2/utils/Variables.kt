package com.example.dutic2.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Environment
import android.view.View
import java.io.File

val DIRECTORIO_RAIZ = Environment.getExternalStorageDirectory().toString() + File.separator + "Dutic 2"
fun View.convertToBitmap(): Bitmap {
    val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    measure(measureSpec, measureSpec)
    layout(0, 0, measuredWidth, measuredHeight)
    val r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
    r.eraseColor(Color.TRANSPARENT)
    val canvas = Canvas(r)
    draw(canvas)
    return r }