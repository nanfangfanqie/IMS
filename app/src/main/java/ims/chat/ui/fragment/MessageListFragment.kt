package ims.chat.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ims.chat.R
import ims.chat.adapter.MessageAdapter
import ims.chat.entity.Message
import kotlinx.android.synthetic.main.contact_list_header.*
import kotlinx.android.synthetic.main.list_frag.*

import java.util.ArrayList

/**
 * @author yangchen
 * on 3/12/2019 12:03 AM
 */
class MessageListFragment : BaseFragment() {
    private lateinit var messageAdapter: MessageAdapter
    private val msgs: List<Message>
        get() {
            return ArrayList()
        }

    /**
     * 刷新页面
     */
    private fun refresh() {
        val messageList = ArrayList<Message>()
        messageAdapter.setMessageList(messageList)
        rcyViewMsg.adapter = messageAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        rcyViewMsg.layoutManager = layoutManager
        verify_ll.visibility = View.GONE
        messageAdapter = MessageAdapter(msgs)
        rcyViewMsg.adapter = messageAdapter
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = false
            refresh()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_frag, container, false)
    }
}
