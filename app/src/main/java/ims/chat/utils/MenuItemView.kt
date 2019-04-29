package ims.chat.utils

import android.view.View
import android.widget.LinearLayout
import ims.chat.R


class MenuItemView(private val mView: View) {
    private var mAddFriendLl: LinearLayout? = null
    private var mSendMsgLl: LinearLayout? = null

    fun initModule() {
        mAddFriendLl = mView.findViewById<View>(R.id.add_friend_with_confirm_ll) as LinearLayout
        mSendMsgLl = mView.findViewById<View>(R.id.send_message_ll) as LinearLayout
    }

    fun setListeners(listener: View.OnClickListener) {
        mAddFriendLl!!.setOnClickListener(listener)
        mSendMsgLl!!.setOnClickListener(listener)
    }

    fun showAddFriendDirect() {
        mAddFriendLl!!.visibility = View.GONE
    }

    fun showAddFriend() {
        mAddFriendLl!!.visibility = View.VISIBLE
    }

}
