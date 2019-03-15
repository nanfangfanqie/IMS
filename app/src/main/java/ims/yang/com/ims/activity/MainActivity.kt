package ims.yang.com.ims.activity

import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import ims.yang.com.ims.R
import ims.yang.com.ims.adapter.MessageAdapter
import ims.yang.com.ims.adapter.ViewPagerAdapter
import ims.yang.com.ims.entity.Message
import ims.yang.com.ims.fragment.BaseFragment
import ims.yang.com.ims.fragment.FriendListFragment
import ims.yang.com.ims.fragment.MessageListFragment
import ims.yang.com.ims.util.MyToast
import ims.yang.com.ims.util.ResourceUtil

import java.util.ArrayList

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawerLayout: DrawerLayout? = null
    private var viewPager: ViewPager? = null
    private var menuItem: MenuItem? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var messageListFragment: MessageListFragment? = null
    private var friendListFragment:FriendListFragment? = null

    private fun setupViewPager(viewPager: ViewPager) {
        //添加消息碎片
        val adapter = ViewPagerAdapter(supportFragmentManager)
        messageListFragment = MessageListFragment()
        friendListFragment = FriendListFragment()
        adapter.addFragment(messageListFragment)
        adapter.addFragment(friendListFragment)
        //        adapter.addFragment(BaseFragment.newInstance("消息"));
//        adapter.addFragment(BaseFragment.newInstance("联系人"))
        adapter.addFragment(BaseFragment.newInstance("动态"))
        viewPager.adapter = adapter
    }

    private fun init() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.main_ui)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val actionBar = supportActionBar

        /**
         * 替换导航栏
         */
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setCheckedItem(R.id.nav_backup)
        navView.setNavigationItemSelectedListener(this)
        //初始化viewpager
        viewPager = findViewById(R.id.viewpager)
        /**
         * 初始化底部导航栏
         */
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView!!.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.message -> {
                    viewPager!!.currentItem = 0
                    toolbar.title = "消息"
                }
                R.id.contacts -> {
                    viewPager!!.currentItem = 1
                    toolbar.title = "联系人"
                }
                R.id.moments -> {
                    viewPager!!.currentItem = 2
                    toolbar.title = "动态"
                }
                else -> {
                }
            }//                        messageListFragment
            false
        }

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
                    0 -> toolbar.title = "消息"
                    1 -> toolbar.title = "联系人"
                    2 -> toolbar.title = "动态"
                    else -> {
                    }
                }
            }
            override fun onPageSelected(position: Int) {
                if (menuItem != null) {
                    menuItem!!.isChecked = false
                } else {
                    bottomNavigationView!!.menu.getItem(0).isChecked = false
                }
                menuItem = bottomNavigationView!!.menu.getItem(position)
                menuItem!!.isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
        setupViewPager(viewPager!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout!!.openDrawer(Gravity.START)
            R.id.settings -> MyToast.toastShort(this, ResourceUtil.getString(this, R.string.is_developing))
            else -> {
            }
        }
        return true
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        //TODO
        drawerLayout!!.closeDrawers()
        return false
    }

    companion object {

        fun actionStart(context: Context, vararg dataArr: String) {
            val intent = Intent(context, MainActivity::class.java)
            if (dataArr.isNotEmpty()) {
                for (i in dataArr.indices) {
                    intent.putExtra("param" + (i + 1), dataArr[i])
                }
            }
            context.startActivity(intent)
        }
    }
}
