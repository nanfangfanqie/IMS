package ims.chat.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import cn.jpush.im.android.api.JMessageClient
import com.bumptech.glide.Glide
import ims.chat.R
import ims.chat.baidu.OCRActivity
import ims.chat.entity.User
import ims.chat.task.ViewPagerAdapter
import ims.chat.ui.controller.ActivityController
import ims.chat.ui.fragment.BaseFragment
import ims.chat.ui.fragment.FriendFragment
import ims.chat.ui.fragment.MessageListFragment
import ims.chat.utils.*
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.viewpager.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var messageListFragment: MessageListFragment? = null
    private var friendListFragment: FriendFragment? = null
    private var menuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        init()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        //添加消息碎片
        val adapter = ViewPagerAdapter(supportFragmentManager)
        messageListFragment = MessageListFragment()
        friendListFragment = FriendFragment()
        adapter.addFragment(messageListFragment!!)
        adapter.addFragment(friendListFragment!!)
        adapter.addFragment(BaseFragment.newInstance("动态"))
        viewPager.adapter = adapter
    }

    private fun init() {
        toolbar.setTitle(R.string.main_ui)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        //替换导航栏
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        navView.setNavigationItemSelectedListener(this)
        //底部导航栏
        navBottom!!.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.message -> {
                    viewpager!!.currentItem = 0
                    toolbar.title = "消息"
                }
                R.id.contacts -> {
                    viewpager!!.currentItem = 1
                    toolbar.title = "联系人"
                }
                R.id.moments -> {
                    viewpager!!.currentItem = 2
                    toolbar.title = "动态"
                }
                else -> {

                }
            }
            false
        }

        viewpager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
                    0 ->{ toolbar.title = "消息"}
                    1 -> {
                        toolbar.title = "联系人"
                    }
                    2 -> toolbar.title = "动态"
                    else -> {
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                if (menuItem != null) {
                    menuItem!!.isChecked = false
                } else {
                    navBottom!!.menu.getItem(0).isChecked = false
                }
                menuItem = navBottom!!.menu.getItem(position)
                menuItem!!.isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
        setupViewPager(viewpager!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var intent:Intent
        when (item.itemId) {
            android.R.id.home ->{
                drawerLayout!!.openDrawer(Gravity.START)
                val user = JMessageClient.getMyInfo()
                val att = SharePreferenceManager.getCachedAvatarPath();
                if(null!=att){
                    Glide.with(this@MainActivity).load(att).into(icon_img)
                }
                nav_userName.text = user.nickname
                nav_phone.text= user.userName

            }
            R.id.add ->{
                intent = Intent(this@MainActivity,SearchForAddFriendActivity::class.java)
                startActivity(intent)
            }
            R.id.search_title ->{
                ToastUtil.shortToast(this,"查找")
            }
            else -> {
            }
        }
        return true
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.navRobot -> {
                navView.setCheckedItem(R.id.navRobot)
                val intent = Intent(this, RobotChatActivity::class.java)
                startActivity(intent)
            }
            R.id.news ->{
                navView.setCheckedItem(R.id.news)
                //启动新闻界面
                val intent = Intent(this@MainActivity, NewsActivity::class.java)
                startActivity(intent)
            }

            R.id.ocr ->{
                navView.setCheckedItem(R.id.news)
                //启动新闻界面
                val intent = Intent(this@MainActivity, OCRActivity::class.java)
                startActivity(intent)
            }
            R.id.exit -> {
                navView.setCheckedItem(R.id.exit)
                val alertDialog:AlertDialog.Builder = AlertDialog.Builder(this)

                alertDialog.setTitle("退出")
                alertDialog.setIcon(R.drawable.exit_black)
                alertDialog.setMessage("确定退出应用吗？")
                alertDialog.setPositiveButton("确定") { _: DialogInterface, _: Int ->
                    JMessageClient.logout()
                    ActivityController.finishAll();
                }
                alertDialog.setNegativeButton("取消"){ _: DialogInterface, _: Int ->
                }
                alertDialog.show()
            }
        }
        drawerLayout!!.closeDrawers()
        return false
    }

    companion object {
        fun actionStart(context: Context, vararg dataArr: User) {
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
