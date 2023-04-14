package com.faruqabdulhakim.androidfacedetection

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Size
import android.view.View
import com.google.mlkit.vision.face.Face

class Overlay : View {
    private var previewWidth = 0
    private var widthScaleFactor = 1F
    private var previewHeight = 0
    private var heightScaleFactor = 1F

    private var faces = emptyArray<Face>()

    private var orientation = Configuration.ORIENTATION_LANDSCAPE

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawOverlay(canvas)
    }

    internal fun setOrientation(orientation: Int) {
        this.orientation = orientation
    }

    internal fun setPreviewSize(size: Size) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            previewWidth = size.width
            previewHeight = size.height
        } else {
            // swap because default is landscape
            previewWidth = size.height
            previewHeight = size.width
        }
    }

    internal fun setFaces(faceList: List<Face>) {
        faces = faceList.toTypedArray()
        postInvalidate()
    }

    private fun drawOverlay(canvas: Canvas) {
        widthScaleFactor = width.toFloat() / previewWidth
        heightScaleFactor = height.toFloat() / previewHeight

        for (face in faces) {
            drawFaceBorder(face, canvas)
        }
    }

    private fun drawFaceBorder(face: Face, canvas: Canvas) {
        val bounds = face.boundingBox
        val top = bounds.top.toFloat() * heightScaleFactor
        val bottom = bounds.bottom.toFloat() * heightScaleFactor

//      flipped horizontal
        val left = width.toFloat() - bounds.left.toFloat() * widthScaleFactor
        val right = width.toFloat() - bounds.right.toFloat() * widthScaleFactor

        canvas.drawRect(left, top, right, bottom, paint)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor (
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)
}