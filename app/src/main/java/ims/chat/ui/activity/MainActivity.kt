package ims.chat.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.view.KeyEvent
import android.view.MenuItem
import cn.jiguang.api.JCoreInterface
import cn.jpush.im.android.api.JMessageClient
import ims.chat.R
import ims.chat.baidu.OCRActivity
import ims.chat.ui.controller.ActivityController
import ims.chat.ui.controller.MainController
import ims.chat.ui.view.MainView
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : FragmentActivity() {
    private var mMainController: MainController? = null
    private var mMainView: MainView? = null
    lateinit var navigationView: NavigationView

    val supportFragmentManger: FragmentManager
        get() = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mMainView = findViewById(R.id.main_view)
        navigationView = findViewById(R.id.navView)
        main_view!!.initModule()
        mMainController = MainController(main_view, this)
        main_view!!.setOnClickListener(mMainController)
        main_view!!.setOnPageChangeListener(mMainController)
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
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
                    navView.setCheckedItem(R.id.ocr)
                    //OCR工具集合
                    val intent = Intent(this@MainActivity, OCRActivity::class.java)
                    startActivity(intent)
                }
                R.id.exit -> {
                    navView.setCheckedItem(R.id.exit)
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
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
            //关闭抽屉
            drawerLayout!!.closeDrawers()
            return@setNavigationItemSelectedListener false
            }
        }



    override fun onPause() {
        JCoreInterface.onPause(this)
        super.onPause()
    }
    override fun onResume() {
        JCoreInterface.onResume(this)
        mMainController!!.sortConvList()
        super.onResume()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }
}
