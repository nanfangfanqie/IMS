package ims.chat.ui.controller

import android.app.Dialog
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import ims.chat.R
import ims.chat.adapter.ConversationListAdapter
import ims.chat.application.ImsApplication
import ims.chat.ui.activity.ChatActivity
import ims.chat.ui.fragment.ConversationListFragment
import ims.chat.ui.view.ConversationListView
import ims.chat.ui.view.SortTopConvList
import ims.chat.utils.DialogCreator
import ims.chat.utils.SortConvList
import java.util.ArrayList
import java.util.Collections


class ConversationListController(
    private val mConvListView: ConversationListView, private val mContext: ConversationListFragment,
    private val mWidth: Int
) : View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    var adapter: ConversationListAdapter? = null
        private set
    private var mDatas: MutableList<Conversation>? = ArrayList()
    private var mDialog: Dialog? = null

    internal var topConv: MutableList<Conversation>? = ArrayList()
    internal var forCurrent: MutableList<Conversation> = ArrayList()
    internal var delFeedBack: MutableList<Conversation> = ArrayList()

    init {
        initConvListAdapter()
    }

    private fun initConvListAdapter() {
        forCurrent.clear()
        topConv!!.clear()
        delFeedBack.clear()
        var i = 0
        mDatas = JMessageClient.getConversationList()
        if (mDatas != null && mDatas!!.size > 0) {
            mConvListView.setNullConversation(true)
            val sortConvList = SortConvList()
            Collections.sort(mDatas!!, sortConvList)
            for (con in mDatas!!) {
                if (con.targetId == "feedback_Android") {
                    delFeedBack.add(con)
                }
                if (!TextUtils.isEmpty(con.extra)) {
                    forCurrent.add(con)
                }
            }
            topConv!!.addAll(forCurrent)
            mDatas!!.removeAll(forCurrent)
            mDatas!!.removeAll(delFeedBack)

        } else {
            mConvListView.setNullConversation(false)
        }
        if (topConv != null && topConv!!.size > 0) {
            val top = SortTopConvList()
            Collections.sort(topConv!!, top)
            for (conv in topConv!!) {
                mDatas!!.add(i, conv)
                i++
            }
        }
        adapter = mContext.activity?.let { ConversationListAdapter(it, mDatas, mConvListView) }
        mConvListView.setConvListAdapter(adapter!!)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.create_group_btn -> mContext.showPopWindow()
            R.id.search_title -> {
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        //点击会话条目
        val intent = Intent()
        if (position > 0) {
            //这里-3是减掉添加的三个headView
            val conv = mDatas!![position - 3]
            intent.putExtra(ImsApplication.CONV_TITLE, conv.title)
            val targetId = (conv.targetInfo as UserInfo).userName
            intent.putExtra(ImsApplication.TARGET_ID, targetId)
            intent.putExtra(ImsApplication.TARGET_APP_KEY, conv.targetAppKey)
            intent.putExtra(ImsApplication.DRAFT, adapter!!.getDraft(conv.id))
            intent.setClass(mContext.activity!!, ChatActivity::class.java)
            mContext.context!!.startActivity(intent)

        }
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val conv = mDatas!![position - 3]
        if (conv != null) {
            val listener = View.OnClickListener { v ->
                when (v.id) {
                    //会话置顶
                    R.id.jmui_top_conv_ll -> {
                        //已经置顶,去取消
                        if (!TextUtils.isEmpty(conv.extra)) {
                            adapter!!.setCancelConvTop(conv)
                            //没有置顶,去置顶
                        } else {
                            adapter!!.setConvTop(conv)
                        }
                        mDialog!!.dismiss()
                    }
                    //删除会话
                    R.id.jmui_delete_conv_ll -> {
                        if (conv.type == ConversationType.group) {
                            JMessageClient.deleteGroupConversation((conv.targetInfo as GroupInfo).groupID)
                        } else {
                            JMessageClient.deleteSingleConversation((conv.targetInfo as UserInfo).userName)
                        }
                        mDatas!!.removeAt(position - 3)
                        if (mDatas!!.size > 0) {
                            mConvListView.setNullConversation(true)
                        } else {
                            mConvListView.setNullConversation(false)
                        }
                        adapter!!.notifyDataSetChanged()
                        mDialog!!.dismiss()
                    }
                    else -> {
                    }
                }
            }
            mDialog =
                DialogCreator.createDelConversationDialog(mContext.activity, listener, TextUtils.isEmpty(conv.extra))
            mDialog!!.show()
            mDialog!!.window!!.setLayout((0.8 * mWidth).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        }
        return true
    }
}
