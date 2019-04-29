package ims.chat.ui.controller

import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import ims.chat.R
import ims.chat.adapter.ViewPagerAdapter
import ims.chat.ui.activity.MainActivity
import ims.chat.ui.fragment.ContactsFragment
import ims.chat.ui.fragment.ConversationListFragment
import ims.chat.ui.fragment.MeFragment
import ims.chat.ui.view.MainView

import java.util.ArrayList


class MainController(private val mMainView: MainView, private val mContext: MainActivity) : View.OnClickListener,
    ViewPager.OnPageChangeListener {
    private var mConvListFragment: ConversationListFragment? = null
    private var mMeFragment: MeFragment? = null
    private var mContactsFragment: ContactsFragment? = null


    init {
        setViewPager()
    }

    private fun setViewPager() {
        val fragments = ArrayList<Fragment>()
        // 初始化碎片
        mConvListFragment = ConversationListFragment()
        mContactsFragment = ContactsFragment()
        mMeFragment = MeFragment()
        fragments.add(mConvListFragment!!)
        fragments.add(mContactsFragment!!)
        fragments.add(mMeFragment!!)
        val viewPagerAdapter = ViewPagerAdapter(
            mContext.supportFragmentManger,
            fragments
        )
        mMainView.setViewPagerAdapter(viewPagerAdapter)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.actionbar_msg_btn -> mMainView.setCurrentItem(0, false)
            R.id.actionbar_contact_btn -> mMainView.setCurrentItem(1, false)
            R.id.actionbar_me_btn -> mMainView.setCurrentItem(2, false)
        }
    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        mMainView.setButtonColor(position)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    fun sortConvList() {
        mConvListFragment!!.sortConvList()
    }


}