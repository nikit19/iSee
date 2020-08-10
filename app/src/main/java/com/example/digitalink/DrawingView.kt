package com.example.digitalink

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context?,
    attributeSet: AttributeSet? = null
) :
    View(context, attributeSet) {
    private val currentStrokePaint: Paint = Paint()
    private val canvasPaint: Paint
    private val currentStroke: Path
    private lateinit var drawCanvas: Canvas
    private lateinit var canvasBitmap: Bitmap

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
        canvas.drawPath(currentStroke, currentStrokePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val x = event.x
        val y = event.y
        when (action) {
            MotionEvent.ACTION_DOWN -> currentStroke.moveTo(x, y)
            MotionEvent.ACTION_MOVE -> currentStroke.lineTo(x, y)
            MotionEvent.ACTION_UP -> {
                currentStroke.lineTo(x, y)
                drawCanvas.drawPath(currentStroke, currentStrokePaint)
                currentStroke.reset()
            }
            else -> {
            }
        }
        StrokeManager.addNewTouchEvent(event)
        invalidate()
        return true
    }

    companion object {
        private const val STROKE_WIDTH_DP = 5
    }

    init {
        currentStrokePaint.color = -0x1000000 // black
        currentStrokePaint.isAntiAlias = true
        // Set stroke width based on display density.
        currentStrokePaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            STROKE_WIDTH_DP.toFloat(),
            resources.displayMetrics
        )
        currentStrokePaint.style = Paint.Style.STROKE
        currentStrokePaint.strokeJoin = Paint.Join.ROUND
        currentStrokePaint.strokeCap = Paint.Cap.ROUND
        currentStroke = Path()
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    fun clear() {
        currentStroke.reset()
        onSizeChanged(
            canvasBitmap.width,
            canvasBitmap.height,
            canvasBitmap.width,
            canvasBitmap.height
        )
    }
}