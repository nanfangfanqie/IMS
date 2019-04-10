package ims.yang.com.ims.ui

import android.Manifest
import android.os.CountDownTimer
import android.os.Bundle
import android.os.Handler
import ims.yang.com.ims.R
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer


/**启动屏
 * @author yangchen
 * on 2019/2/28 23:15
 */
class SplashActivity : BaseActivity() {
    lateinit var timer:CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        init()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //返回取消计时
        timer.cancel()
    }

    private fun init() {
        Handler().postDelayed({requestPermissions()},500)
    }
    private fun requestPermissions(){
        //获取权限
        val rxPermission = RxPermissions(this)
        rxPermission.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET
        ).subscribe(Consumer<Boolean> {
            if (it!!){
                LoginActivity.actionStart(this@SplashActivity)
                finish()
            }else{
                finish()
            }
        })
    }

    companion object {
        //倒计时时间
        private const val MILLIS_IN_FUTURE = 500L
        //倒计时间隔
        private const val COUNTDOWN_INTERVAL = 500L
    }

}
