package ims.chat.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ims.chat.R

import ims.chat.adapter.FriendAdapter
import ims.chat.entity.Friend

import kotlinx.android.synthetic.main.list_frag.*

import java.util.ArrayList

/**
 * @author yangchen
 * on 3/12/2019 12:03 AM
 */
class FriendListFragment : Fragment() {
    private lateinit var friendAdapter: FriendAdapter

    private val friends: List<Friend>
        get() {
            val userList = ArrayList<Friend>()
            for (i in 0..29) {
                val user = Friend("好友昵称$i")
                userList.add(user)
            }
            return userList
        }

    /**
     * 刷新页面
     */
    fun refresh() {
        val friendList = ArrayList<Friend>()
        for (i in 0..29) {
            val user = Friend("我的好友昵称$i")
            friendList.add(user)
        }
        friendAdapter.setFriendList(friendList)
        rcyViewMsg.adapter = friendAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
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

}
