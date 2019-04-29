package ims.chat.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.eventbus.EventBus
import com.activeandroid.query.Update
import ims.chat.R
import ims.chat.application.ImsApplication
import ims.chat.ui.controller.FriendInfoController
import ims.chat.database.FriendEntry
import ims.chat.entity.Event
import ims.chat.entity.EventType
import ims.chat.ui.view.FriendInfoView
import ims.chat.utils.DialogCreator
import ims.chat.utils.HandleResponseCode
import ims.chat.utils.NativeImageLoader

class FriendInfoActivity : BaseActivity() {

    private var mTargetId: String? = null
    var targetAppKey: String? = null
        private set
    private var mIsFromContact: Boolean = false
    private var mFriendInfoView: FriendInfoView? = null
    var userInfo: UserInfo? = null
        private set
    private var mFriendInfoController: FriendInfoController? = null
    private var mGroupId: Long = 0
    private var mTitle: String? = null
    private var mIsGetAvatar = false
    private var mIsAddFriend = false
    private var mIsFromSearch = false
    private var mFromGroup = false
    private var mUserID: String? = null

    val userName: String
        get() = userInfo!!.userName

    val width: Int
        get() = mWidth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_info)
        mFriendInfoView = findViewById(R.id.friend_info_view) as FriendInfoView
        mTargetId = intent.getStringExtra(ImsApplication.TARGET_ID)
        targetAppKey = intent.getStringExtra(ImsApplication.TARGET_APP_KEY)
        mUserID = intent.getStringExtra("targetId")
        if (targetAppKey == null) {
            targetAppKey = JMessageClient.getMyInfo().appKey
        }
        mFriendInfoView!!.initModel(this)
        mFriendInfoController = FriendInfoController(mFriendInfoView!!, this)
        mFriendInfoView!!.setListeners(mFriendInfoController!!)
        mFriendInfoView!!.setOnChangeListener(mFriendInfoController!!)
        mIsFromContact = intent.getBooleanExtra("fromContact", false)
        mIsFromSearch = intent.getBooleanExtra("fromSearch", false)
        mIsAddFriend = intent.getBooleanExtra("addFriend", false)
        mFromGroup = intent.getBooleanExtra("group_grid", false)

        //从通讯录中点击过来
        if (mIsFromContact || mIsFromSearch || mFromGroup || mIsAddFriend) {
            updateUserInfo()
        } else {
            mGroupId = intent.getLongExtra(ImsApplication.GROUP_ID, 0)
            val conv: Conversation
            if (mGroupId == 0L) {
                conv = JMessageClient.getSingleConversation(mTargetId, targetAppKey)
                userInfo = conv.targetInfo as UserInfo
            } else {
                conv = JMessageClient.getGroupConversation(mGroupId)
                val groupInfo = conv.targetInfo as GroupInfo
                userInfo = groupInfo.getGroupMemberInfo(mTargetId, targetAppKey)
            }

            //先从Conversation里获得UserInfo展示出来
            mFriendInfoView!!.initInfo(userInfo)
            updateUserInfo()
        }

    }

    private fun updateUserInfo() {
        val dialog = DialogCreator.createLoadingDialog(
            this@FriendInfoActivity,
            this@FriendInfoActivity.getString(R.string.jmui_loading)
        )
        dialog.show()
        if (TextUtils.isEmpty(mTargetId) && !TextUtils.isEmpty(mUserID)) {
            mTargetId = mUserID
        }
        JMessageClient.getUserInfo(mTargetId, targetAppKey, object : GetUserInfoCallback() {
            override fun gotResult(responseCode: Int, responseMessage: String, info: UserInfo) {
                dialog.dismiss()
                if (responseCode == 0) {
                    //拉取好友信息时候要更新数据库中的nickName.因为如果对方修改了nickName我们是无法感知的.如果不在拉取信息
                    //时候更新数据库的话会影响到搜索好友的nickName, 注意要在没有备注名并且有昵称时候去更新.因为备注名优先级更高
                    Update(FriendEntry::class.java).set("DisplayName=?", info.displayName)
                        .where("Username=?", mTargetId!!).execute()
                    Update(FriendEntry::class.java).set("NickName=?", info.nickname).where("Username=?", mTargetId!!)
                        .execute()
                    Update(FriendEntry::class.java).set("NoteName=?", info.notename).where("Username=?", mTargetId!!)
                        .execute()
                    if (info.avatarFile != null) {
                        Update(FriendEntry::class.java).set("Avatar=?", info.avatarFile.absolutePath)
                            .where("Username=?", mTargetId!!).execute()
                    }
                    userInfo = info
                    mFriendInfoController!!.setFriendInfo(info)
                    mTitle = info.notename
                    if (TextUtils.isEmpty(mTitle)) {
                        mTitle = info.nickname
                    }
                    mFriendInfoView!!.initInfo(info)
                } else {
                    HandleResponseCode.onHandle(this@FriendInfoActivity, responseCode, false)
                }
            }
        })
    }


    fun startChatActivity() {
        if (mIsFromContact || mIsAddFriend || mIsFromSearch || mFromGroup) {
            val intent = Intent(this, ChatActivity::class.java)
            var title = userInfo!!.notename
            if (TextUtils.isEmpty(title)) {
                title = userInfo!!.nickname
                if (TextUtils.isEmpty(title)) {
                    title = userInfo!!.userName
                }
            }
            intent.putExtra(ImsApplication.CONV_TITLE, title)
            intent.putExtra(ImsApplication.TARGET_ID, userInfo!!.userName)
            intent.putExtra(ImsApplication.TARGET_APP_KEY, userInfo!!.appKey)
            startActivity(intent)
        } else {
            if (mGroupId != 0L) {
                val intent = Intent()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(ImsApplication.TARGET_ID, mTargetId)
                intent.putExtra(ImsApplication.TARGET_APP_KEY, targetAppKey)
                intent.setClass(this, ChatActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent()
                intent.putExtra("returnChatActivity", true)
                intent.putExtra(ImsApplication.CONV_TITLE, mTitle)
                setResult(ImsApplication.RESULT_CODE_FRIEND_INFO, intent)
            }
        }
        var conv: Conversation? = JMessageClient.getSingleConversation(mTargetId, targetAppKey)
        //如果会话为空，使用EventBus通知会话列表添加新会话
        if (conv == null) {
            conv = Conversation.createSingleConversation(mTargetId, targetAppKey)
            EventBus.getDefault().post(
                Event.Builder()
                    .setType(EventType.createConversation)
                    .setConversation(conv)
                    .build()
            )
        }
        finish()

    }

    //点击头像预览大图
    fun startBrowserAvatar() {
        if (userInfo != null && !TextUtils.isEmpty(userInfo!!.avatar)) {
            if (mIsGetAvatar) {
                //如果缓存了图片，直接加载
                val bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(userInfo!!.userName)
                if (bitmap != null) {
                    val intent = Intent()
                    intent.putExtra("browserAvatar", true)
                    intent.putExtra("avatarPath", userInfo!!.avatarFile.absolutePath)
                    intent.setClass(this, BrowserViewPagerActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val dialog = DialogCreator.createLoadingDialog(this, this.getString(R.string.jmui_loading))
                dialog.show()
                userInfo!!.getBigAvatarBitmap(object : GetAvatarBitmapCallback() {
                    override fun gotResult(status: Int, desc: String, bitmap: Bitmap) {
                        if (status == 0) {
                            mIsGetAvatar = true
                            //缓存头像
                            NativeImageLoader.getInstance().updateBitmapFromCache(userInfo!!.userName, bitmap)
                            val intent = Intent()
                            intent.putExtra("browserAvatar", true)
                            intent.putExtra("avatarPath", userInfo!!.userName)
                            intent.setClass(this@FriendInfoActivity, BrowserViewPagerActivity::class.java)
                            startActivity(intent)
                        }
                        dialog.dismiss()
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (resultCode == ImsApplication.RESULT_CODE_EDIT_NOTENAME) {
            mTitle = data.getStringExtra(ImsApplication.NOTENAME)
            val friend = mTargetId?.let { targetAppKey?.let { it1 ->
                FriendEntry.getFriend(ImsApplication.getUserEntry(), it,
                    it1
                )
            } }
            if (null != friend) {
                friend.displayName = (mTitle as String?).toString()
                friend.save()
            }
        }
    }

    //将获得的最新的昵称返回到聊天界面
    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(ImsApplication.CONV_TITLE, mTitle)
        setResult(ImsApplication.RESULT_CODE_FRIEND_INFO, intent)
        finish()
        super.onBackPressed()
    }

}
