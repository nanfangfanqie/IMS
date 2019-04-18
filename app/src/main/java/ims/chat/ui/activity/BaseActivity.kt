package ims.chat.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ims.chat.ui.controller.ActivityController


/**BaseActivity 用于管理所有的活动
 * @author yangchen
 * on 2019/2/28 23:56
 */
open class BaseActivity : AppCompatActivity() {
    protected var firstTime = 0L

    companion object {
        private const val TAG = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityController.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityController.removeActivity(this)
    }
}
