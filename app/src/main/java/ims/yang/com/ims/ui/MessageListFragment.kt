package ims.yang.com.ims.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ims.yang.com.ims.R
import ims.yang.com.ims.adapter.MessageAdapter
import ims.yang.com.ims.entity.Message
import kotlinx.android.synthetic.main.list_frag.*

import java.util.ArrayList

/**
 * @author yangchen
 * on 3/12/2019 12:03 AM
 */
class MessageListFragment : Fragment() {
    private lateinit var messageAdapter: MessageAdapter

    private val msgs: List<Message>
        get() {
            val messageList = ArrayList<Message>()
            for (i in 0..29) {
                val message = Message("很长很长很长很长很长很长很长很长很长的消息$i")
                messageList.add(message)
            }
            return messageList
        }

    /**
     * 刷新页面
     */
    private fun refresh() {
        val messageList = ArrayList<Message>()
        for (i in 0..29) {
            val message = Message("不那么长的消息$i")
            messageList.add(message)
        }
        messageAdapter.setMessageList(messageList)
        rcyViewMsg.adapter = messageAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        rcyViewMsg.layoutManager = layoutManager
        messageAdapter = MessageAdapter(msgs)
        rcyViewMsg.adapter = messageAdapter
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
