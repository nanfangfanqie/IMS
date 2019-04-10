package ims.yang.com.ims.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ims.yang.com.ims.R
import ims.yang.com.ims.util.HttpConfig
import kotlinx.android.synthetic.main.config_activity.*


class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.config_activity)
        edtIP.setText(HttpConfig.ip)
        edtPort.setText(HttpConfig.port)
        btnSubmit.setOnClickListener(View.OnClickListener {
            val ip = edtIP.text.toString()
            val port = edtIP.text.toString()
            if (ip.isEmpty() || port.isEmpty()) {
                return@OnClickListener
            } else {
                HttpConfig.ip = ip
                HttpConfig.port = port
                finish()
            }
        })


    }
}
