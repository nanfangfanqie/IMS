package ims.yang.com.ims.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import java.util.ArrayList

/**
 * @author yangchen
 * on 2019/3/10 22:22
 */
class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val fragmentList = ArrayList<Fragment>()

    override fun getItem(i: Int): Fragment {
        return fragmentList[i]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}
