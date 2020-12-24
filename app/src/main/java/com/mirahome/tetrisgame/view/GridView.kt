package com.mirahome.tetrisgame.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import com.mirahome.tetrisgame.constant.Constants.DEFAULT_MARGIN
import com.mirahome.tetrisgame.constant.Constants.GRID_LINE_WIDTH
import com.mirahome.tetrisgame.constant.Constants.SIMPLE_GRID_NUM

class GridView : View {


    var mWidth: Int = 0
    var mHeight: Int = 0

    var mGridWidth: Int = 0
    var mMargin: Int = 0
    var mGridNum = SIMPLE_GRID_NUM

    var mGridLinePaint: Paint? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mHeight = wm.defaultDisplay.width
        mWidth = (wm.defaultDisplay.width * 0.7).toInt()
        mGridWidth = (mHeight - DEFAULT_MARGIN) / mGridNum
        val mHeight1 = mHeight
        mMargin = ((mHeight1) - mGridWidth * mGridNum) / 2

        mGridLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mGridLinePaint?.strokeWidth = GRID_LINE_WIDTH
        mGridLinePaint?.color = Color.parseColor("#141212")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(resolveMeasure(widthMeasureSpec, mWidth), resolveMeasure(heightMeasureSpec, mHeight))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawGridLine(canvas!!)
    }


    private fun resolveMeasure(measureSpec: Int, defaultSize: Int): Int {
        var result = 0
        val specSize = MeasureSpec.getSize(measureSpec)
        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED -> result = defaultSize

            MeasureSpec.AT_MOST -> result = Math.min(specSize, defaultSize)

            MeasureSpec.EXACTLY -> { }

            else -> result = defaultSize
        }
        return result
    }

    private fun drawGridLine(canvas: Canvas) {
        val horizontalLineLength = mGridWidth * ((mGridNum * 0.7).toInt())
        for (i in 0..(mGridNum + 1)) {
            val horizontalStartX = mMargin
            val horizontalStartY = (mGridWidth * i) + mMargin
            val horizontalStopX = horizontalLineLength + mMargin
            val verticalStartX = (mGridWidth * i) + mMargin
            val verticalStartY = mMargin
            val verticalStopY = mHeight - mMargin
            mGridLinePaint?.let { canvas.drawLine(horizontalStartX.toFloat(), horizontalStartY.toFloat(), horizontalStopX.toFloat(), horizontalStartY.toFloat(), it) }
            mGridLinePaint?.let { canvas.drawLine(verticalStartX.toFloat(), verticalStartY.toFloat(), verticalStartX.toFloat(), verticalStopY.toFloat(), it) }
        }
    }
}