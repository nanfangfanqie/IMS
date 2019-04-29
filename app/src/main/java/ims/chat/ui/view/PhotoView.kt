/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ims.chat.ui.view

import android.content.Context
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView


class PhotoView @JvmOverloads constructor(
    fromChatActivity: Boolean,
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : ImageView(context, attr, defStyle), IPhotoView {

    private val mAttacher: PhotoViewAttacher?

    private var mPendingScaleType: ImageView.ScaleType? = null

    init {
        super.setScaleType(ImageView.ScaleType.MATRIX)
        mAttacher = PhotoViewAttacher(this, fromChatActivity, context)

        if (null != mPendingScaleType) {
            scaleType = mPendingScaleType as ScaleType
            mPendingScaleType = null
        }
    }

    override fun canZoom(): Boolean {
        return mAttacher!!.canZoom()
    }

    override fun getDisplayRect(): RectF {
        return mAttacher!!.displayRect
    }

    override fun getMinScale(): Float {
        return mAttacher!!.minScale
    }

    override fun getMidScale(): Float {
        return mAttacher!!.midScale
    }

    override fun getMaxScale(): Float {
        return mAttacher!!.maxScale
    }

    override fun getScale(): Float {
        return mAttacher!!.scale
    }

    override fun getScaleType(): ImageView.ScaleType {
        return mAttacher!!.scaleType
    }

    override fun setAllowParentInterceptOnEdge(allow: Boolean) {
        mAttacher!!.setAllowParentInterceptOnEdge(allow)
    }

    override fun setMinScale(minScale: Float) {
        mAttacher!!.minScale = minScale
    }

    override fun setMidScale(midScale: Float) {
        mAttacher!!.midScale = midScale
    }

    override fun setMaxScale(maxScale: Float) {
        mAttacher!!.maxScale = maxScale
    }

    override// setImageBitmap calls through to this method
    fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mAttacher?.update()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mAttacher?.update()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mAttacher?.update()
    }

    override fun setOnMatrixChangeListener(listener: PhotoViewAttacher.OnMatrixChangedListener) {
        mAttacher!!.setOnMatrixChangeListener(listener)
    }

    override fun setOnLongClickListener(l: View.OnLongClickListener?) {
        mAttacher!!.setOnLongClickListener(l)
    }

    override fun setOnPhotoTapListener(listener: PhotoViewAttacher.OnPhotoTapListener) {
        mAttacher!!.setOnPhotoTapListener(listener)
    }

    override fun setOnViewTapListener(listener: PhotoViewAttacher.OnViewTapListener) {
        mAttacher!!.setOnViewTapListener(listener)
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        if (null != mAttacher) {
            mAttacher.scaleType = scaleType
        } else {
            mPendingScaleType = scaleType
        }
    }

    override fun setZoomable(zoomable: Boolean) {
        mAttacher!!.setZoomable(zoomable)
    }

    override fun zoomTo(scale: Float, focalX: Float, focalY: Float) {
        mAttacher!!.zoomTo(scale, focalX, focalY)
    }

    override fun onDetachedFromWindow() {
        mAttacher!!.cleanup()
        super.onDetachedFromWindow()
    }

}