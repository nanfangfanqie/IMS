package ims.chat.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import ims.chat.R
import ims.chat.model.Constants
import ims.chat.utils.keyboard.adpater.EmoticonsAdapter
import ims.chat.utils.keyboard.data.EmoticonEntity
import ims.chat.utils.keyboard.data.EmoticonPageEntity
import ims.chat.utils.keyboard.interfaces.EmoticonClickListener


class TextEmoticonsAdapter(
    context: Context,
    emoticonPageEntity: EmoticonPageEntity<*>,
    onEmoticonClickListener: EmoticonClickListener<*>
) : EmoticonsAdapter<EmoticonEntity>(context, emoticonPageEntity, onEmoticonClickListener) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = mInflater.inflate(R.layout.item_emoticon_text, null)
            viewHolder.rootView = convertView
            viewHolder.ly_root = convertView!!.findViewById<View>(R.id.ly_root) as LinearLayout
            viewHolder.tv_content = convertView.findViewById<View>(R.id.tv_content) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val isDelBtn = isDelBtn(position)
        val emoticonEntity = mData[position]
        if (isDelBtn) {
            viewHolder.ly_root!!.setBackgroundResource(R.drawable.bg_emoticon)
        } else {
            viewHolder.tv_content!!.visibility = View.VISIBLE
            if (emoticonEntity != null) {
                viewHolder.tv_content!!.text = emoticonEntity.content
                viewHolder.ly_root!!.setBackgroundResource(R.drawable.bg_emoticon)
            }
        }

        viewHolder.rootView!!.setOnClickListener {
            if (mOnEmoticonClickListener != null) {
                mOnEmoticonClickListener.onEmoticonClick(emoticonEntity, Constants.EMOTICON_CLICK_TEXT, isDelBtn)
            }
        }

        updateUI(viewHolder, parent)
        return convertView
    }

    protected fun updateUI(viewHolder: ViewHolder, parent: ViewGroup) {
        if (mDefalutItemHeight != mItemHeight) {
            viewHolder.tv_content!!.layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mItemHeight)
        }
        mItemHeightMax =
            if (this.mItemHeightMax != 0) this.mItemHeightMax else (mItemHeight * mItemHeightMaxRatio).toInt()
        mItemHeightMin = if (this.mItemHeightMin != 0) this.mItemHeightMin else mItemHeight
        var realItemHeight = (parent.parent as View).measuredHeight / mEmoticonPageEntity.line
        realItemHeight = Math.min(realItemHeight, mItemHeightMax)
        realItemHeight = Math.max(realItemHeight, mItemHeightMin)
        viewHolder.ly_root!!.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, realItemHeight)
    }

    class ViewHolder {
        var rootView: View? = null
        var ly_root: LinearLayout? = null
        var tv_content: TextView? = null
    }
}