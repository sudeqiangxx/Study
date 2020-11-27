package com.zzw.hiltdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.Scroller
import androidx.annotation.StyleableRes


/**
 *
 * @author Created by lenna on 2020/9/18
 */
 abstract class CustomView(context: Context, attributeSet: AttributeSet?=null, def: Int=0) :
    View(context, attributeSet, def) {
    init {
        init(attributeSet)
    }

    val ATTR = intArrayOf(
        R.attr.lf_scale_view_min,
        R.attr.lf_scale_view_max,
        R.attr.lf_scale_view_margin,
        R.attr.lf_scale_view_height
    )

    @StyleableRes
    val LF_SCALE_MIN = 0

    @StyleableRes
    val LF_SCALE_MAX = 1

    @StyleableRes
    val LF_SCALE_MARGIN = 2

    @StyleableRes
    val LF_SCALE_HEIGHT = 3

    @StyleableRes
    val LF_SCALE_CURRENT = 4

    private var mMax //最大刻度
            = 0
    private var mMin // 最小刻度
            = 0
    protected var mCountScale //滑动的总刻度
            = 0

    protected var mScaleScrollViewRange = 0

    private var mScaleMargin //刻度间距
            = 0
    private var mScaleHeight //刻度线的高度
            = 0
    protected var mScaleMaxHeight //整刻度线高度
            = 0

    protected var mRectWidth //总宽度
            = 0
    protected var mRectHeight //高度
            = 0

    private var mScroller: Scroller? = null
    protected var mScrollLastX = 0

    protected var mTempScale // 用于判断滑动方向
            = 0
    protected var mMidCountScale //中间刻度
            = 0

    private var mScrollListener: OnScrollListener? = null

    interface OnScrollListener {
        fun onScaleScroll(scale: Int)
    }


    protected fun init(attrs: AttributeSet?) {
        // 获取自定义属性
        val ta = context.obtainStyledAttributes(attrs, ATTR)
        mMin = ta.getInteger(LF_SCALE_MIN, 0)
        mMax = ta.getInteger(LF_SCALE_MAX, 200)
        mScaleMargin = ta.getDimensionPixelOffset(LF_SCALE_MARGIN, 15)
        mScaleHeight = ta.getDimensionPixelOffset(LF_SCALE_HEIGHT, 20)
        ta.recycle()
        mScroller = Scroller(context)
        initVar()
    }

    override fun onDraw(canvas: Canvas?) {
        // 画笔
        val paint = Paint()
        paint.color = Color.GRAY
        // 抗锯齿
        paint.isAntiAlias = true
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paint.isDither = true
        // 空心
        paint.style = Paint.Style.STROKE
        // 文字居中
        paint.textAlign = Paint.Align.CENTER
        onDrawLine(canvas, paint)
        onDrawScale(canvas, paint) //画刻度
        onDrawPointer(canvas, paint) //画指针
        super.onDraw(canvas)
    }

    protected abstract fun initVar()

    // 画线
    protected abstract fun onDrawLine(canvas: Canvas?, paint: Paint?)

    // 画刻度
    protected abstract fun onDrawScale(canvas: Canvas?, paint: Paint?)

    // 画指针
    protected abstract fun onDrawPointer(canvas: Canvas?, paint: Paint?)

    // 滑动到指定刻度
    abstract fun scrollToScale(scale: Int)

    fun setCurScale(scale: Int) {
        if (scale in mMin..mMax) {
            scrollToScale(scale)
            postInvalidate()
        }
    }

    /**
     * 使用Scroller时需重写
     */
    override fun computeScroll() {
        super.computeScroll()
        // 判断Scroller是否执行完毕
        if (mScroller!!.computeScrollOffset()) {
            scrollTo(mScroller!!.currX, mScroller!!.currY)
            // 通过重绘来不断调用computeScroll
            invalidate()
        }
    }

    private fun smoothScrollBy(dx: Int, dy: Int) {
        mScroller!!.startScroll(mScroller!!.finalX, mScroller!!.finalY, dx, dy)
    }

    fun smoothScrollTo(fx: Int, fy: Int) {
        val dx = fx - mScroller!!.finalX
        val dy = fy - mScroller!!.finalY
        smoothScrollBy(dx, dy)
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    fun setOnScrollListener(listener: OnScrollListener?) {
        mScrollListener = listener
    }
}