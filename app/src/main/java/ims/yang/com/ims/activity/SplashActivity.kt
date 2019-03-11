package ims.yang.com.ims.activity

import android.os.CountDownTimer
import android.os.Bundle
import ims.yang.com.ims.R

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
        //TODO 初始化操作
         timer = object : CountDownTimer(Companion.MILLIS_IN_FUTURE, Companion.COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                //启动登录页面
                LoginActivity.actionStart(this@SplashActivity);
                finish()
            }
        }
        timer.start()
    }

    companion object {
        //倒计时时间
        private const val MILLIS_IN_FUTURE = 500L
        //倒计时间隔
        private const val COUNTDOWN_INTERVAL = 500L
    }

}
