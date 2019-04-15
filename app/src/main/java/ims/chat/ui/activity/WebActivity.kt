package ims.chat.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import ims.chat.R
import kotlinx.android.synthetic.main.web_activity.*

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_activity)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window;
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor =Color.parseColor("#ff3333");

        }
        val intent = intent
        val url = intent.getStringExtra("url")
        webView.settings.javaScriptEnabled = false
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
    }

    companion object {
        fun actionStart(context: Context, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }
}
