package ims.chat.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.model.UserInfo
import com.activeandroid.ActiveAndroid
import ims.chat.R

import ims.chat.adapter.FriendAdapter
import ims.chat.application.ImsApplication
import ims.chat.database.FriendEntry
import ims.chat.database.UserEntry
import kotlinx.android.synthetic.main.contact_list_header.*

import kotlinx.android.synthetic.main.list_frag.*

import java.util.ArrayList

/**
 * @author yangchen
 * on 3/12/2019 12:03 AM
 */
class ContactsFragment : JmBaseFragment() {
    private lateinit var friendAdapter: FriendAdapter
    private val forDelete = ArrayList<FriendEntry>()
    private var friends = ArrayList<FriendEntry>()

    /**
     * 刷新页面
     */
    private fun refresh() {
        get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = activity?.let { LinearLayoutManager(it) }
        rcyViewMsg.layoutManager = layoutManager
        friendAdapter = FriendAdapter(friends)
        //设置适配器
        rcyViewMsg.adapter = friendAdapter
        swipeToRefresh.setOnRefreshListener {
            //模拟发送网络请求
            //停止刷新
            swipeToRefresh.isRefreshing = false
            refresh()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_frag, container, false)
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
                                    val sb = StringBuilder()
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
                                    user, userInfo.userName, userInfo.appKey
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
                                    friends.add(friend)
                                }
                                forDelete.add(friend)
                            }
                            ActiveAndroid.setTransactionSuccessful()
                        } finally {
                            ActiveAndroid.endTransaction()
                        }
                    }else{view_line.visibility = View.GONE}
                    //其他端删除好友后,登陆时把数据库中的也删掉
                    val friends = ImsApplication.getUserEntry().friends
                    friends.removeAll(forDelete)
                    for (del in friends) {
                        del.delete()
                        friends.remove(del)
                    }
                    friendAdapter = FriendAdapter(friends)
                }
            }
        })
    }

}
