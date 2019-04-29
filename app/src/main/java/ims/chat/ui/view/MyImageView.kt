package ims.chat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class MyImageView : ImageView {
    private var onMeasureListener: OnMeasureListener? = null

    fun setOnMeasureListener(onMeasureListener: OnMeasureListener) {
        this.onMeasureListener = onMeasureListener
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //将图片测量的大小回调到onMeasureSize()方法中
        if (onMeasureListener != null) {
            onMeasureListener!!.onMeasureSize(measuredWidth, measuredHeight)
        }
    }

    interface OnMeasureListener {
        fun onMeasureSize(width: Int, height: Int)
    }

}
