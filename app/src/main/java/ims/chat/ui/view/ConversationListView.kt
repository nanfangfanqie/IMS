package ims.chat.ui.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import ims.chat.R
import ims.chat.ui.fragment.ConversationListFragment
import ims.chat.utils.ThreadUtil



class ConversationListView(
    private val mConvListFragment: View,
    private val mContext: Context,
    private val mFragment: ConversationListFragment
) {
    private var mConvListView: ListView? = null
    private var mCreateGroup: ImageButton? = null
    private var mSearchHead: LinearLayout? = null
    private var mHeader: LinearLayout? = null
    private var mLoadingHeader: RelativeLayout? = null
    private var mLoadingIv: ImageView? = null
    private var mLoadingTv: LinearLayout? = null
    private var mNull_conversation: TextView? = null
    private var mSearch: LinearLayout? = null
    private var mAllUnReadMsg: TextView? = null

    fun initModule() {
        mConvListView = mConvListFragment.findViewById<View>(R.id.conv_list_view) as ListView
        mCreateGroup = mConvListFragment.findViewById<View>(R.id.create_group_btn) as ImageButton
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mHeader = inflater.inflate(R.layout.conv_list_head_view, mConvListView, false) as LinearLayout
        mSearchHead = inflater.inflate(R.layout.conversation_head_view, mConvListView, false) as LinearLayout
        mLoadingHeader = inflater.inflate(R.layout.jmui_drop_down_list_header, mConvListView, false) as RelativeLayout
        mLoadingIv = mLoadingHeader!!.findViewById(R.id.jmui_loading_img) as ImageView
        mLoadingTv = mLoadingHeader!!.findViewById(R.id.loading_view) as LinearLayout
        mSearch = mSearchHead!!.findViewById(R.id.search_title) as LinearLayout
        mNull_conversation = mConvListFragment.findViewById(R.id.null_conversation) as? TextView
        mAllUnReadMsg = mFragment.activity!!.findViewById(R.id.all_unread_number) as? TextView
        mConvListView!!.addHeaderView(mLoadingHeader)
        mConvListView!!.addHeaderView(mSearchHead)
        mConvListView!!.addHeaderView(mHeader)
    }

    fun setConvListAdapter(adapter: ListAdapter) {
        mConvListView!!.adapter = adapter
    }


    fun setListener(onClickListener: View.OnClickListener) {
        mSearch!!.setOnClickListener(onClickListener)
        mCreateGroup!!.setOnClickListener(onClickListener)
    }

    fun setItemListeners(onClickListener: AdapterView.OnItemClickListener) {
        mConvListView!!.onItemClickListener = onClickListener
    }

    fun setLongClickListener(listener: AdapterView.OnItemLongClickListener) {
        mConvListView!!.onItemLongClickListener = listener
    }


    fun showHeaderView() {
        mHeader!!.findViewById<View>(R.id.network_disconnected_iv).visibility = View.VISIBLE
        mHeader!!.findViewById<View>(R.id.check_network_hit).visibility = View.VISIBLE
    }

    fun dismissHeaderView() {
        mHeader!!.findViewById<View>(R.id.network_disconnected_iv).visibility = View.GONE
        mHeader!!.findViewById<View>(R.id.check_network_hit).visibility = View.GONE
    }


    fun showLoadingHeader() {
        mLoadingIv!!.visibility = View.VISIBLE
        mLoadingTv!!.visibility = View.VISIBLE
        val drawable = mLoadingIv!!.drawable as AnimationDrawable
        drawable.start()
    }

    fun dismissLoadingHeader() {
        mLoadingIv!!.visibility = View.GONE
        mLoadingTv!!.visibility = View.GONE
    }

    fun setNullConversation(isHaveConv: Boolean) {
        if (isHaveConv) {
            mNull_conversation!!.visibility = View.GONE
        } else {
            mNull_conversation!!.visibility = View.VISIBLE
        }
    }


    fun setUnReadMsg(count: Int) {
        ThreadUtil.runInUiThread {
            if (mAllUnReadMsg != null) {
                if (count > 0) {
                    mAllUnReadMsg!!.visibility = View.VISIBLE
                    if (count < 100) {
                        mAllUnReadMsg!!.text = count.toString() + ""
                    } else {
                        mAllUnReadMsg!!.text = "99+"
                    }
                } else {
                    mAllUnReadMsg!!.visibility = View.GONE
                }
            }
        }
    }


}
