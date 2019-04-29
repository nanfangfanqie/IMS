package ims.chat.ui.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import ims.chat.R
import ims.chat.adapter.StickyListAdapter
import ims.chat.application.ImsApplication
import ims.chat.ui.controller.ContactsController
import ims.chat.ui.sidebar.SideBar
import ims.chat.utils.SharePreferenceManager
import ims.chat.utils.listview.StickyListHeadersListView


class ContactsView(private val mContext: Context, attrs: AttributeSet?) : LinearLayout(mContext, attrs) {
    private var mListView: StickyListHeadersListView? = null
    private var mSideBar: SideBar? = null
    private val mDialogTextView: TextView? = null
    private var mIb_goToAddFriend: ImageButton? = null
    private val mLetterHintTv: TextView? = null
    private var mVerify_ll: LinearLayout? = null
    private var mNewFriendNum: TextView? = null
    private val mInflater: LayoutInflater
    private var mSearch_title: LinearLayout? = null
    private var mLoadingIv: ImageView? = null
    private var mLoadingTv: LinearLayout? = null
    private var mView_line: View? = null

    init {
        mInflater = LayoutInflater.from(mContext)

    }


    fun initModule(ratio: Float, density: Float) {
        mIb_goToAddFriend = findViewById<View>(R.id.ib_goToAddFriend) as ImageButton

        mListView = findViewById<View>(R.id.listview) as StickyListHeadersListView
        mSideBar = findViewById<View>(R.id.sidebar) as SideBar
        mSideBar!!.setTextView(mDialogTextView)

        //        mLetterHintTv = (TextView) findViewById(R.id.group_dialog);
        mSideBar!!.setTextView(mLetterHintTv)
        mSideBar!!.bringToFront()


        val header = mInflater.inflate(R.layout.contact_list_header, null)
        mVerify_ll = header.findViewById<View>(R.id.verify_ll) as LinearLayout
        mNewFriendNum = header.findViewById<View>(R.id.friend_verification_num) as TextView
        mSearch_title = header.findViewById<View>(R.id.search_title) as LinearLayout
        mView_line = header.findViewById(R.id.view_line)

        val loadingHeader = mInflater.inflate(R.layout.jmui_drop_down_list_header, null) as RelativeLayout
        mLoadingIv = loadingHeader.findViewById<View>(R.id.jmui_loading_img) as ImageView
        mLoadingTv = loadingHeader.findViewById<View>(R.id.loading_view) as LinearLayout
        mNewFriendNum!!.visibility = View.INVISIBLE
        mListView!!.addHeaderView(loadingHeader)
        mListView!!.addHeaderView(header, null, false)
        mListView!!.isDrawingListUnderStickyHeader = true
        mListView!!.areHeadersSticky = true
        mListView!!.stickyHeaderTopOffset = 0

    }


    fun setListener(contactsController: ContactsController) {
        mIb_goToAddFriend!!.setOnClickListener(contactsController)
        mVerify_ll!!.setOnClickListener(contactsController)
        //        mGroup_ll.setOnClickListener(contactsController);
        mSearch_title!!.setOnClickListener(contactsController)
    }

    fun setSelection(position: Int) {
        mListView!!.setSelection(position)
    }

    fun setSideBarTouchListener(listener: SideBar.OnTouchingLetterChangedListener) {
        mSideBar!!.setOnTouchingLetterChangedListener(listener)
    }

    fun setAdapter(adapter: StickyListAdapter) {
        mListView!!.adapter = adapter
    }

    fun showNewFriends(num: Int) {
        mNewFriendNum!!.visibility = View.VISIBLE
        if (num > 99) {
            mNewFriendNum!!.text = "99+"
        } else {
            mNewFriendNum!!.text = num.toString()
        }
    }

    fun dismissNewFriends() {
        SharePreferenceManager.setCachedNewFriendNum(0)
        ImsApplication.forAddFriend.clear()
        mNewFriendNum!!.visibility = View.INVISIBLE
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

    /**
     * desc:当没有好友时候在群组下面画一条线.以区分和好友列表
     * 当有好友时候,字母索充当分割线
     *
     * 如果不这样设置的话,当有好友时候还会显示view线,这样ui界面不太好看
     */

    fun showLine() {
        mView_line!!.visibility = View.VISIBLE
    }

    fun dismissLine() {
        mView_line!!.visibility = View.GONE
    }

    fun showContact() {
        mSideBar!!.visibility = View.VISIBLE
        mListView!!.visibility = View.VISIBLE
    }
}
