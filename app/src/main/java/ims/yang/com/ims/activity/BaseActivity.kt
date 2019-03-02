package ims.yang.com.ims.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import ims.yang.com.ims.util.ActivityController
import ims.yang.com.ims.util.MyToast

/**
 * @author yangchen
 * on 2019/2/28 23:56
 */
open class BaseActivity : AppCompatActivity() {
    var firstTime = 0L

    companion object {
        private const val TAG = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, javaClass.simpleName)
        ActivityController.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityController.removeActivity(this)
    }

    override fun onBackPressed(){
        if(ActivityController.activities.size>1){
            finish()
            return
        }
        val secondTime  = System.currentTimeMillis();
        if (secondTime - firstTime>2000L){
            MyToast.toastShort(this,"再按一次退出程序");
            firstTime = secondTime
        }else{
            ActivityController.finishAll()
        }
    }
}
