package com.zzw.hiltdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 *
 * @author Created by lenna on 2020/9/18
 */
class CustomScaleView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attr, def) {

    //四条弧线 矩形
    private var mRightArcRectF1 = RectF()
    private var mRightArcRectF2 = RectF()
    private var mRightArcRectF3 = RectF()
    private var mRightArcRectF4 = RectF()

    private var mRightArcPaint1 = Paint()
    private var mRightArcPaint2 = Paint()
    private var mRightArcPaint3 = Paint()
    private var mRightArcPaint4 = Paint()
    //中间选中线条


    init {
        initView()
    }

    private fun initView() {


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }


}