package ims.chat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import cn.jpush.im.android.api.model.Conversation
import ims.chat.R
import ims.chat.adapter.ChattingListAdapter
import ims.chat.ui.activity.ChatActivity
import ims.chat.utils.listview.DropDownListView


class ChatView : RelativeLayout {
    internal lateinit var mContext: Context
    private var mReturnButton: ImageButton? = null
    private var mGroupNumTv: TextView? = null
    private var mRightBtn: ImageButton? = null
    var listView: DropDownListView? = null
        private set
    private var mConv: Conversation? = null
    private var mAtMeBtn: Button? = null

    private var mChatTitle: TextView? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}


    fun initModule(density: Float, densityDpi: Int) {
        mReturnButton = findViewById<View>(R.id.jmui_return_btn) as ImageButton
        mGroupNumTv = findViewById<View>(R.id.jmui_group_num_tv) as TextView
        mRightBtn = findViewById<View>(R.id.jmui_right_btn) as ImageButton
        mChatTitle = findViewById<View>(R.id.jmui_title) as TextView
        mAtMeBtn = findViewById<View>(R.id.jmui_at_me_btn) as Button
        if (densityDpi <= 160) {
            mChatTitle!!.maxWidth = (180 * density + 0.5f).toInt()
        } else if (densityDpi <= 240) {
            mChatTitle!!.maxWidth = (190 * density + 0.5f).toInt()
        } else {
            mChatTitle!!.maxWidth = (200 * density + 0.5f).toInt()
        }
        listView = findViewById<View>(R.id.lv_chat) as DropDownListView

    }

    fun setToPosition(position: Int) {
        listView!!.smoothScrollToPosition(position)
        mAtMeBtn!!.visibility = View.GONE
    }

    fun setChatListAdapter(chatAdapter: ChattingListAdapter) {
        listView!!.adapter = chatAdapter
    }

    fun setToBottom() {
        listView!!.clearFocus()
        listView!!.post { listView!!.setSelection(listView!!.adapter.count - 1) }
    }

    fun setConversation(conv: Conversation) {
        this.mConv = conv
    }


    fun setListeners(listeners: ChatActivity) {
        mReturnButton!!.setOnClickListener(listeners)
        mRightBtn!!.setOnClickListener(listeners)
        mAtMeBtn!!.setOnClickListener(listeners)
    }



    fun setChatTitle(id: Int, count: Int) {
        mChatTitle!!.setText(id)
        mGroupNumTv!!.text = "($count)"
        mGroupNumTv!!.visibility = View.VISIBLE
    }

    fun setChatTitle(id: Int) {
        mChatTitle!!.setText(id)
    }



    fun setChatTitle(title: String) {
        mChatTitle!!.text = title
    }

    fun dismissGroupNum() {
        mGroupNumTv!!.visibility = View.GONE
    }
}
