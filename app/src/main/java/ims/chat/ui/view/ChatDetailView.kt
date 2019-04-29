package ims.chat.ui.view


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import ims.chat.R


class ChatDetailView(private val mContext: Context, attrs: AttributeSet) : LinearLayout(mContext, attrs) {


    private var mMyNameLL: LinearLayout? = null
    private var mGroupChatDelLL: LinearLayout? = null
    private var mReturnBtn: ImageButton? = null
    private var mTitle: TextView? = null
    private var mMenuBtn: ImageButton? = null
    private var mDelFriend: Button? = null
    private var mMyName: TextView? = null
    private var mBlockBtn: SlipButton? = null
    private var mBlockRl: RelativeLayout? = null
    private var mBlockLine: View? = null
    private var mClear_rl: RelativeLayout? = null

    fun initModule() {
        mMyNameLL = findViewById<View>(R.id.group_my_name_ll) as LinearLayout
        mGroupChatDelLL = findViewById<View>(R.id.group_chat_del_ll) as LinearLayout
        mReturnBtn = findViewById<View>(R.id.return_btn) as ImageButton
        mTitle = findViewById<View>(R.id.title) as TextView
        mMenuBtn = findViewById<View>(R.id.right_btn) as ImageButton
        mDelFriend = findViewById<View>(R.id.del_friend) as Button
        mMyName = findViewById<View>(R.id.chat_detail_my_name) as TextView
        mNoDisturbBtn = findViewById<View>(R.id.no_disturb_slip_btn) as SlipButton
        mBlockRl = findViewById<View>(R.id.block_rl) as RelativeLayout
        mBlockBtn = findViewById<View>(R.id.block_slip_btn) as SlipButton
        mBlockLine = findViewById(R.id.block_split_line)
        mClear_rl = findViewById<View>(R.id.clear_rl) as RelativeLayout

        mTitle!!.text = mContext.getString(R.string.chat_detail_title)
        mMenuBtn!!.visibility = View.GONE
    }

    fun setListeners(onClickListener: View.OnClickListener) {
        mMyNameLL!!.setOnClickListener(onClickListener)
        mGroupChatDelLL!!.setOnClickListener(onClickListener)
        mReturnBtn!!.setOnClickListener(onClickListener)
        mDelFriend!!.setOnClickListener(onClickListener)
        mClear_rl!!.setOnClickListener(onClickListener)
    }

    fun setOnChangeListener(listener: SlipButton.OnChangedListener) {
        mNoDisturbBtn.setOnChangedListener(R.id.no_disturb_slip_btn, listener)
        mBlockBtn!!.setOnChangedListener(R.id.block_slip_btn, listener)
    }


    fun setTitle(title: String) {
        mTitle!!.text = title
    }


    fun setMyName(str: String) {
        mMyName!!.text = str
    }

    fun setSingleView(friend: Boolean) {
        if (friend) {
            mDelFriend!!.visibility = View.VISIBLE
        } else {
            mDelFriend!!.visibility = View.GONE
        }
        mDelFriend!!.text = "删除好友"
    }


    fun initNoDisturb(status: Int) {
        mNoDisturbBtn.setChecked(status == 1)
    }

    fun setNoDisturbChecked(flag: Boolean) {
        mNoDisturbBtn.setChecked(flag)
    }

    companion object {
        lateinit var mNoDisturbBtn: SlipButton
    }
}
