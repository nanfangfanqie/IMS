package ims.chat.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.event.ContactNotifyEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.eventbus.EventBus
import com.activeandroid.ActiveAndroid
import ims.chat.R

import ims.chat.adapter.FriendAdapter
import ims.chat.application.ImsApplication
import ims.chat.database.FriendEntry
import ims.chat.database.FriendRecommendEntry
import ims.chat.database.UserEntry
import ims.chat.entity.Event
import ims.chat.entity.EventType
import ims.chat.entity.FriendInvitation
import ims.chat.ui.activity.FriendRecommendActivity
import ims.chat.utils.SharePreferenceManager
import ims.chat.utils.pinyin.HanziToPinyin
import ims.chat.utils.pinyin.PinyinComparator
import kotlinx.android.synthetic.main.contact_list_header.*

import kotlinx.android.synthetic.main.list_frag.*
import java.util.*

/**
 * @author yangchen
 * on 3/12/2019 12:03 AM
 */
class FriendFragment : BaseFragment() {
    private lateinit var friendAdapter: FriendAdapter
    private val forDelete = ArrayList<FriendEntry>()
    private var mList = ArrayList<FriendEntry>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = activity?.let { LinearLayoutManager(it) }
        rcyViewMsg.layoutManager = layoutManager
        friendAdapter = FriendAdapter(mList)
        //设置适配器
        rcyViewMsg.adapter = friendAdapter
        get()
        swipeToRefresh.setOnRefreshListener {
            get()
            swipeToRefresh.isRefreshing = false

        }
        val intent:Intent
        search_title.setOnClickListener {

//            intent = Intent(ImsApplication.getContext(),SearchContactsActivity::class.java!!)
//            startActivity(intent)
        }
        verify_ll.setOnClickListener{
            //添加好友
            startActivity(Intent(ImsApplication.getContext(),FriendRecommendActivity::class.java!!))
            dismissNewFriends()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_frag, container, false)
    }

    private fun dismissNewFriends() {
        SharePreferenceManager.setCachedNewFriendNum(0)
        ImsApplication.forAddFriend.clear()
        friend_verification_num.visibility = View.INVISIBLE
    }

    //接收到好友事件
    fun onEvent(event: ContactNotifyEvent) {
        val user = ImsApplication.getUserEntry()
        val reason = event.reason
        val username = event.fromUsername
        val appKey = event.getfromUserAppKey()
        //对方接收了你的好友请求
        if (event.type == ContactNotifyEvent.Type.invite_accepted) {
            JMessageClient.getUserInfo(username, appKey, object : GetUserInfoCallback() {
                override fun gotResult(responseCode: Int, responseMessage: String, info: UserInfo) {
                    if (responseCode == 0) {
                        var name = info.nickname
                        if (TextUtils.isEmpty(name)) {
                            name = info.userName
                        }
                        val friendEntry = FriendEntry.getFriend(user, username, appKey)
                        if (friendEntry == null) {
                            val newFriend = FriendEntry(
                                info.userID,
                                username,
                                info.notename,
                                info.nickname,
                                appKey,
                                info.avatar,
                                name,
                                getLetter(name),
                                user
                            )
                            newFriend.save()
                            refresh(newFriend)
                        }
                    }
                }
            })
            val entry = FriendRecommendEntry.getEntry(user, username, appKey)
            entry.state = FriendInvitation.ACCEPTED.value
            entry.save()

            var conversation: Conversation? = JMessageClient.getSingleConversation(username)
            if (conversation == null) {
                conversation = Conversation.createSingleConversation(username)
                EventBus.getDefault().post(
                    Event.Builder()
                        .setType(EventType.createConversation)
                        .setConversation(conversation)
                        .build()
                )
            }

            //拒绝好友请求
        } else if (event.type == ContactNotifyEvent.Type.invite_declined) {
            ImsApplication.forAddFriend.remove(username)
            val entry = FriendRecommendEntry.getEntry(user, username, appKey)
            entry.state = FriendInvitation.BE_REFUSED.value
            entry.reason = reason
            entry.save()
            //收到好友邀请
        } else if (event.type == ContactNotifyEvent.Type.invite_received) {
            //如果同一个人申请多次,则只会出现一次;当点击进验证消息界面后,同一个人再次申请则可以收到
            if (ImsApplication.forAddFriend.size > 0) {
                for (forAdd in ImsApplication.forAddFriend) {
                    if (forAdd == username) {
                        return
                    } else {
                        ImsApplication.forAddFriend.add(username)
                    }
                }
            } else {
                ImsApplication.forAddFriend.add(username)
            }
            JMessageClient.getUserInfo(username, appKey, object : GetUserInfoCallback() {
                override fun gotResult(status: Int, desc: String, userInfo: UserInfo) {
                    if (status == 0) {
                        var name = userInfo.nickname
                        if (TextUtils.isEmpty(name)) {
                            name = userInfo.userName
                        }
                        var entry: FriendRecommendEntry? = FriendRecommendEntry.getEntry(user, username, appKey)
                        if (null == entry) {
                            if (null != userInfo.avatar) {
                                val path = userInfo.avatarFile.path
                                entry = FriendRecommendEntry(
                                    userInfo.userID, username, userInfo.notename, userInfo.nickname, appKey, path,
                                    name, reason, FriendInvitation.INVITED.value, user, 0
                                )
                            } else {
                                entry = FriendRecommendEntry(
                                    userInfo.userID, username, userInfo.notename, userInfo.nickname, appKey, null,
                                    username, reason, FriendInvitation.INVITED.value, user, 0
                                )
                            }
                        } else {
                            entry.state = FriendInvitation.INVITED.value
                            entry.reason = reason
                        }
                        entry.save()
                        //收到好友请求数字 +1
                        val showNum = SharePreferenceManager.getCachedNewFriendNum() + 1
                        showNewFriends(showNum)
                        SharePreferenceManager.setCachedNewFriendNum(showNum)
                    }
                }
            })
        } else if (event.type == ContactNotifyEvent.Type.contact_deleted) {
            ImsApplication.forAddFriend.remove(username)
            val friendEntry = FriendEntry.getFriend(user, username, appKey)
            friendEntry.delete()
            refresh(friendEntry)
        }
    }

    fun get() {
        val user = UserEntry.getUser(
            JMessageClient.getMyInfo().userName,
            JMessageClient.getMyInfo().appKey
        )
        ContactManager.getFriendList(object : GetUserInfoListCallback() {
            override fun gotResult(responseCode: Int, responseMessage: String?, userInfoList: MutableList<UserInfo>?) {
                if (responseCode == 0) {
                    if (userInfoList!!.size != 0) {
                        view_line.visibility = View.VISIBLE
                        ActiveAndroid.beginTransaction()
                        try {
                            for (userInfo in userInfoList) {
                                val displayName = userInfo.displayName
                                val letter: String
                                if (!TextUtils.isEmpty(displayName.trim { it <= ' ' })) {
                                    val tokens = HanziToPinyin.getInstance()
                                        .get(displayName)
                                    val sb = StringBuilder()
                                    if (tokens !=
                                        null && tokens.size > 0
                                    ) {
                                        for (token in tokens) {
                                            if (token.type == HanziToPinyin.Token.PINYIN) {
                                                sb.append(token.target)
                                            } else {
                                                sb.append(token.source)
                                            }
                                        }
                                    }
                                    val sortString = sb.toString().substring(0, 1).toUpperCase()
                                    if (sortString.matches("[A-Z]".toRegex())) {
                                        letter = sortString.toUpperCase()
                                    } else {
                                        letter = "#"
                                    }
                                } else {
                                    letter = "#"
                                }
                                //避免重复请求时导致数据重复A
                                var friend: FriendEntry? = FriendEntry.getFriend(
                                    user,
                                    userInfo.userName, userInfo.appKey
                                )
                                if (null == friend) {
                                    if (TextUtils.isEmpty(userInfo.avatar)) {
                                        friend = FriendEntry(
                                            userInfo.userID,
                                            userInfo.userName,
                                            userInfo.notename,
                                            userInfo.nickname,
                                            userInfo.appKey,
                                            null,
                                            displayName,
                                            letter,
                                            user
                                        )
                                    } else {
                                        friend = FriendEntry(
                                            userInfo.userID,
                                            userInfo.userName,
                                            userInfo.notename,
                                            userInfo.nickname,
                                            userInfo.appKey,
                                            userInfo.avatarFile.absolutePath,
                                            displayName,
                                            letter,
                                            user
                                        )
                                    }
                                    friend.save()
                                    mList.add(friend)
                                }
                                forDelete.add(friend)
                            }
                            ActiveAndroid.setTransactionSuccessful()
                        } finally {
                            ActiveAndroid.endTransaction()
                        }
                    }else{view_line.visibility = View.GONE}
                    //其他端删除好友后,登陆时把数据库中的也删掉
                    val friends = ImsApplication.getUserEntry().getFriends()
                    friends.removeAll(forDelete)
                    for (del in friends) {
                        del.delete()
                        mList.remove(del)
                    }
                    Collections.sort(mList, PinyinComparator())
                    friendAdapter = FriendAdapter(mList)
                    friendAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getLetter(name: String): String {
        val letter: String
        val tokens = HanziToPinyin.getInstance()
            .get(name)
        val sb = StringBuilder()
        if (tokens != null && tokens.size > 0) {
            for (token in tokens) {
                if (token.type == HanziToPinyin.Token.PINYIN) {
                    sb.append(token.target)
                } else {
                    sb.append(token.source)
                }
            }
        }
        val sortString = sb.toString().substring(0, 1).toUpperCase()
        if (sortString.matches("[A-Z]".toRegex())) {
            letter = sortString.toUpperCase()
        } else {
            letter = "#"
        }
        return letter
    }

    fun refresh(entry: FriendEntry) {
        mList.add(entry)
        if (null == friendAdapter) {
            friendAdapter = FriendAdapter(mList)
        } else {
            Collections.sort(mList, PinyinComparator())
        }
        friendAdapter.notifyDataSetChanged()
    }

    fun showNewFriends(num: Int) {
        friend_verification_num.visibility = View.VISIBLE
        if (num > 99) {
            friend_verification_num.text = "99+"
        } else {
            friend_verification_num.text = num.toString()
        }
    }
}
