@file:Suppress("DEPRECATION")

package ims.chat.ui.activity

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
import com.bumptech.glide.Glide
import com.yang.crypt.MessageDigetUtil
import ims.chat.R
import ims.chat.application.ImsApplication
import ims.chat.constant.API
import ims.chat.ui.controller.LoginController
import ims.chat.entity.User
import ims.chat.listener.Listener
import ims.chat.task.UserTask
import ims.chat.utils.*
import java.net.SocketTimeoutException
import ims.chat.vo.MessageResult
import kotlinx.android.synthetic.main.input_form.*
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * 登录活动
 * @author yangchen
 * on 2019/2/28 23:18
 */
@Suppress("DEPRECATION")
class LoginActivity : BaseActivity(), View.OnClickListener, TextWatcher {
    internal lateinit var progressDialog: ProgressDialog
    private val loginListener = object : Listener<User> {
        override fun onSuccess(user: User) {
            //跳转
            progressDialog.setMessage("登陆成功")
            progressDialog.dismiss()
            finish()
            MainActivity.actionStart(this@LoginActivity, user)
        }

        override fun onPreExecute() {
            progressDialog = ProgressDialog(this@LoginActivity)
            progressDialog.setTitle(R.string.loging)
            progressDialog.setMessage("正在连接服务器...")
            progressDialog.show()
        }

        override fun onError(messageResult: MessageResult<*>) {
            progressDialog.dismiss()
            if (messageResult.data is String) {
                Toast.makeText(this@LoginActivity, messageResult.data as String, Toast.LENGTH_SHORT).show()
            } else {
                val throwable = messageResult.data as Throwable
                if (throwable is SocketTimeoutException) {
                    Toast.makeText(this@LoginActivity, R.string.timeOut, Toast.LENGTH_SHORT).show()
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
    private fun init() {
        edtUserName.addTextChangedListener(this)
        edtPassword.addTextChangedListener(this)
        txtToggle.setOnClickListener(this)
        btnSubmit.setOnClickListener(LoginController(this))
        //长按配置ip和端口
        btnSubmit.setOnLongClickListener {
            startActivity(Intent(this@LoginActivity, ConfigActivity::class.java))
            true
        }
        btnSubmit.setText(R.string.login)
        val userName = SharePreferenceManager.getCachedUsername()
        val userAvatar = SharePreferenceManager.getCachedAvatarPath()
        val password = SharePreferenceManager.getCachedPsw()
        edtUserName.setText(userName)
        edtPassword.setText(password)
        if(null!=userAvatar){
            Glide.with(this@LoginActivity).load(userAvatar).into(imgHeader)
        }
    }
    override fun onClick(v: View) {
        val userName = SharePreferenceManager.getCachedUsername()
        val userAvatar = SharePreferenceManager.getCachedAvatarPath()
        val password = SharePreferenceManager.getCachedPsw()
        when (v) {
            txtToggle ->{
                if(ImsApplication.registerOrLogin % 2== 1){
                    //注册
                    toolbar.title = "注册"
                    btnSubmit.text = "注册"
                    txtLable.text = "已有帐号？"
                    txtToggle.text = "现在登录"
                    imgHeader.setImageResource(R.drawable.qipao)
                }else{
                    //登录
                    toolbar.title = "登录"
                    btnSubmit.text = "登录"
                    txtLable.text = "还没有帐号？"
                    txtToggle.text = "现在注册"
                    if(null!=userAvatar){
                        Glide.with(this@LoginActivity).load(userAvatar).into(imgHeader)
                    }
                }
                ImsApplication.registerOrLogin ++
            }
        }
    }

    private fun login() {
        val phone = edtUserName.text.toString()
        val password = edtPassword.text.toString()
        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this@LoginActivity, R.string.telOrPassCantNull, Toast.LENGTH_SHORT).show()
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
