package ims.yang.com.ims.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import ims.yang.com.ims.util.ActivityController

/**
 * @author yangchen
 * on 2019/2/28 23:56
 */
open class BaseActivity : AppCompatActivity() {

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


}
