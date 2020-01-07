package com.example.dutic2.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.example.dutic2.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.abs

class CustomCanvas : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    // Path representing the drawing so far
    private val drawing = Path()
    // Path representing what's currently being drawn
    private val curPath = Path()

    private val drawColor = ResourcesCompat.getColor(resources, R.color.md_deep_orange_600, null)
    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = strokeWidthVariable // default: Hairline-width (really thin)
    }
//    private lateinit var extraCanvas: Canvas
//    lateinit var extraBitmap: Bitmap

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private lateinit var frame: Rect

    var startClickTime = 0L
    val MAX_DURATION = 200L

    var strokeWidthVariable = 12f

    init {
        background = resources.getDrawable(R.color.md_yellow_200)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
//        if (::extraBitmap.isInitialized) extraBitmap.recycle()
//        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        extraCanvas = Canvas(extraBitmap)
        // Calculate a rectangular frame around the picture.

        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the drawing so far
        canvas.drawPath(drawing, paint)
        // Draw any current squiggle
        canvas.drawPath(curPath, paint)
        // Draw a frame around the canvas
        canvas.drawRect(frame, paint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startClickTime = Calendar.getInstance().timeInMillis
                touchStart()
            }
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp(event)
        }
        return true
    }

    private fun touchStart() {
        drawing.addPath(curPath)
        curPath.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }
    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            curPath.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
        }
        invalidate()

    }

    private fun touchUp(event: MotionEvent) {
        if( (Calendar.getInstance().timeInMillis-startClickTime)< MAX_DURATION ){
            super.performClick()
        }else{
            Log.e("Click","cualquier cosa menos click xd")
        }
    }

}