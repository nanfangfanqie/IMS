package ims.chat.ui.controller

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import ims.chat.R
import ims.chat.ui.activity.SearchForAddFriendActivity
import ims.chat.ui.fragment.ConversationListFragment


class MenuItemController(private val mFragment: ConversationListFragment) : View.OnClickListener {

    //会话界面的加号
    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        val intent: Intent
        when (v.id) {
            R.id.add_friend_with_confirm_ll -> {
                mFragment.dismissPopWindow()
                intent = Intent(mFragment.context, SearchForAddFriendActivity::class.java)
                intent.flags = 1
                mFragment.startActivity(intent)
            }
            R.id.send_message_ll -> {
                mFragment.dismissPopWindow()
                intent = Intent(mFragment.context, SearchForAddFriendActivity::class.java)
                intent.flags = 2
                mFragment.startActivity(intent)
            }
            else -> {
            }
        }

    }
}
