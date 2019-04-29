package ims.chat.ui.controller

import android.content.Intent
import android.view.View
import cn.jpush.im.android.api.model.UserInfo
import ims.chat.R
import ims.chat.ui.activity.FriendInfoActivity
import ims.chat.ui.activity.FriendSettingActivity
import ims.chat.ui.view.FriendInfoView



class FriendInfoController(friendInfoView: FriendInfoView, private val mContext: FriendInfoActivity) :
    View.OnClickListener {
    private var friendInfo: UserInfo? = null

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_goToChat -> mContext.startChatActivity()
            R.id.iv_friendPhoto -> mContext.startBrowserAvatar()
            R.id.jmui_commit_btn -> {
                val intent = Intent(mContext, FriendSettingActivity::class.java)
                intent.putExtra("userName", friendInfo!!.userName)
                intent.putExtra("noteName", friendInfo!!.notename)
                mContext.startActivity(intent)
            }
            R.id.return_btn -> mContext.finish()
            else -> {
            }
        }
    }

    fun setFriendInfo(info: UserInfo) {
        friendInfo = info
    }

}
