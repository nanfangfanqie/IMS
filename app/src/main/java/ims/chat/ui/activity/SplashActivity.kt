package ims.chat.ui.activity

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import ims.chat.R
import com.tbruyelle.rxpermissions2.RxPermissions
import ims.chat.utils.HttpUtil
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.splash_activity.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


/**启动屏
 * @author yangchen
 * on 2019/2/28 23:15
 */
class SplashActivity : AppCompatActivity() {
    lateinit var timer:CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT;
        }
        init()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        //返回取消计时
        timer.cancel()
    }
    private fun init() {
        swpRefresh.setOnRefreshListener {
            loadSplashPic(splashPic)
        }
        val pic = getSharedPreferences("bing_pic", Context.MODE_PRIVATE).getString("bing_pic",null)
        if(null!=pic){
            Glide.with(this).load(pic).into(splashPic);
        }else{
            loadSplashPic(splashPic);
        }
        Handler().postDelayed({requestPermissions()},3000)
    }

    /**
     * 加载必应每日一图
     */
    private fun loadSplashPic(imageView: ImageView) {
        val api = "http:guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(api,object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                val splashPic = response.body()!!.string()
                val editor = PreferenceManager.getDefaultSharedPreferences(this@SplashActivity).edit()
                editor.putString("bing_pic",splashPic);
                editor.apply();
                runOnUiThread {
                    Glide.with(this@SplashActivity).load(splashPic).into(imageView)
                }
            }
        })
    }
    private fun requestPermissions(){
        //获取权限
        val rxPermission = RxPermissions(this)
        rxPermission.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA
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
