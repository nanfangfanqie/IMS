package ims.yang.com.ims.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View

import ims.yang.com.ims.R
import ims.yang.com.ims.entity.User
import ims.yang.com.ims.listener.UserListener
import ims.yang.com.ims.util.MyToast
import ims.yang.com.ims.util.ResourceUtil
import ims.yang.com.ims.vo.MessageResult
import kotlinx.android.synthetic.main.input_form.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * 注册活动
 */
class RegisterActivity : BaseActivity(), View.OnClickListener {
    private val userListener = object : UserListener {
        @Throws(InterruptedException::class)
        override fun onSuccess(user: User) {

        }

        override fun onPreExecute() {

        }

        override fun onError(messageResult: MessageResult<*>) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        toolbar.setTitle(R.string.register)
        setSupportActionBar(toolbar)
        init()
    }

    private fun init() {
        imgHeader.setOnClickListener(this)
        edtPasswordConfirm.visibility = View.VISIBLE
        btnSubmit.setOnClickListener(this)
        btnSubmit.setText(R.string.register)
        val intent = intent
        val userName = intent.getStringExtra("param1")

    }

    override fun onClick(v: View) {
        when (v) {

            imgHeader -> MyToast.toastShort(this, ResourceUtil.getString(this, R.string.is_developing))
            btnSubmit -> {

            }
            else -> {
            }
        }
    }

    companion object {

        fun actionStart(context: Context, vararg dataArr: String) {
            val intent = Intent(context, RegisterActivity::class.java)
            if (dataArr.isNotEmpty()) {
                for (i in dataArr.indices) {
                    intent.putExtra("param" + (i + 1), dataArr[i])
                }
            }
            context.startActivity(intent)
        }
    }
}
