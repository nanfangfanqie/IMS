package ims.yang.com.ims.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.yang.crypt.MessageDigetUtil
import java.net.SocketTimeoutException
import ims.yang.com.ims.R
import ims.yang.com.ims.constant.API
import ims.yang.com.ims.entity.User
import ims.yang.com.ims.listener.UserListener
import ims.yang.com.ims.task.UserTask
import ims.yang.com.ims.util.HttpConfig
import ims.yang.com.ims.util.MyToast
import ims.yang.com.ims.util.ResourceUtil
import ims.yang.com.ims.util.UrlUtil
import ims.yang.com.ims.vo.MessageResult
import kotlinx.android.synthetic.main.input_form.*

import kotlinx.android.synthetic.main.login_activity.*

/**
 * 登录活动
 * @author yangchen
 * on 2019/2/28 23:18
 */
@Suppress("DEPRECATION")
class LoginActivity : BaseActivity(), View.OnClickListener, TextWatcher {
    internal lateinit var progressDialog: ProgressDialog

    @Suppress("DEPRECATION")
    private val loginListener = object : UserListener {
        override fun onSuccess(user: User) {
            //跳转
            progressDialog.setMessage("登陆成功")
            progressDialog.dismiss()
            finish()
            MainActivity.actionStart(this@LoginActivity, user)
        }

        override fun onPreExecute() {
            progressDialog = ProgressDialog(this@LoginActivity)
            progressDialog.setTitle("登陆中")
            progressDialog.setMessage("正在连接服务器")
            progressDialog.show()
        }

        override fun onError(messageResult: MessageResult<*>) {
            progressDialog.dismiss()
            if (messageResult.data is String) {
                Toast.makeText(this@LoginActivity, messageResult.data as String, Toast.LENGTH_SHORT).show()
            } else {
                val throwable = messageResult.data as Throwable
                if (throwable is SocketTimeoutException) {
                    Toast.makeText(this@LoginActivity, "连接超时", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.login)
        setSupportActionBar(toolbar)
        init()
    }

    /**
     * 初始化
     */
    internal fun init() {
        edtUserName.addTextChangedListener(this)
        edtPassword.addTextChangedListener(this)
        txtRegister.setOnClickListener(this)
        txtForgot.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        //长按设置配置
        btnSubmit.setOnLongClickListener {
            startActivity(Intent(this@LoginActivity, ConfigActivity::class.java))
            true
        }
        btnSubmit.setText(R.string.login)
    }

    override fun onClick(v: View) {
        val userName = edtUserName.text.toString()
        val password = edtPassword.text.toString()
        when (v) {
            txtRegister -> if ("" != userName.trim { it <= ' ' } && "" != password.trim { it <= ' ' }) {
                RegisterActivity.actionStart(this, userName, password)
            } else {
                RegisterActivity.actionStart(this)
            }
            btnSubmit -> login()
            txtForgot ->
                //修改密码界面
                MyToast.toastShort(this, ResourceUtil.getString(this, R.string.is_developing))
            else -> {
            }
        }
    }

    private fun login() {
        val phone = edtUserName.text.toString()
        val password = edtPassword.text.toString()
        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this@LoginActivity, "用户名和密码不得为空", Toast.LENGTH_SHORT).show()
        } else {
            try {
                val user = User()
                user.telphone = phone
                user.password = MessageDigetUtil.sha256(password)
                UserTask(loginListener).execute(HttpConfig.url + API.LOGIN.url + UrlUtil.parseURLPair(user))
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "login: " + e.message)
            }

        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        //点击禁用按钮
        btnSubmit.isClickable = true
        btnSubmit.setBackgroundColor(ResourceUtil.getColor(this, R.color.gray))
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (TextUtils.isEmpty(edtUserName.text) || TextUtils.isEmpty(edtPassword.text)) {
            btnSubmit.isClickable = false
            btnSubmit.setBackgroundColor(ResourceUtil.getColor(this, R.color.gray))
        }
    }

    override fun afterTextChanged(s: Editable) {
        if (edtUserName.text.isNotEmpty() && edtPassword.text.isNotEmpty()) {
            btnSubmit.isClickable = true
            btnSubmit.setBackgroundColor(ResourceUtil.getColor(this, R.color.colorPrimary))
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
        fun actionStart(context: Context, vararg dataArr: String) {
            val intent = Intent(context, LoginActivity::class.java)
            if (dataArr.isNotEmpty()) {
                for (i in dataArr.indices) {
                    intent.putExtra("param" + (i + 1), dataArr[i])
                }
            }
            context.startActivity(intent)
        }
    }
}
