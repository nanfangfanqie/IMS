package ims.chat.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by ${chenyn} on 2017/2/20.
 */

class ViewPagerAdapter : FragmentPagerAdapter {

    private var mFragmList: List<Fragment>? = null

    constructor(fm: FragmentManager) : super(fm) {
    }

    constructor(fm: FragmentManager, fragments: List<Fragment>) : super(fm) {
        this.mFragmList = fragments
    }

    override fun getItem(index: Int): Fragment {
        return mFragmList?.get(index)!!
    }

    override fun getCount(): Int {
        return mFragmList!!.size
    }
}