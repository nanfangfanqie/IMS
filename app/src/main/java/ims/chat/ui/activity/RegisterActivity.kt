package ims.chat.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.yang.crypt.MessageDigetUtil

import ims.chat.R
import ims.chat.constant.API
import ims.chat.entity.User
import ims.chat.listener.Listener
import ims.chat.task.UserTask
import ims.chat.utils.*
import ims.chat.vo.MessageResult
import kotlinx.android.synthetic.main.input_form.*
import kotlinx.android.synthetic.main.toolbar.*
import java.net.SocketTimeoutException

/**
 * 注册活动
 */
@Suppress("DEPRECATION")
class RegisterActivity : BaseActivity(), View.OnClickListener {
    internal lateinit var progressDialog: ProgressDialog
    private val userListener = object : Listener<User> {

        override fun onSuccess() {
            progressDialog.setMessage("注册成功")
            Toast.makeText(this@RegisterActivity,R.string.registerSuce,Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }

        override fun onPreExecute() {
            progressDialog = ProgressDialog(this@RegisterActivity)
            progressDialog.setTitle(R.string.registering)
            progressDialog.setMessage("正在连接服务器...")
            progressDialog.show()
        }

        override fun onError(messageResult: MessageResult<*>) {
            progressDialog.dismiss()
            if (messageResult.data is String) {
                Toast.makeText(this@RegisterActivity, messageResult.data as String, Toast.LENGTH_SHORT).show()
            } else {
                val throwable = messageResult.data as Throwable
                if (throwable is SocketTimeoutException) {
                    Toast.makeText(this@RegisterActivity, R.string.timeOut, Toast.LENGTH_SHORT).show()
                }
            }
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
                register()
            }
            else -> {
            }
        }
    }

    fun register(){
        val phone = edtUserName.text.toString();
        val password = edtPassword.text.toString()
        val passwordConfirm = edtPasswordConfirm.text.toString();
        if(phone.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()){
            Toast.makeText(this,R.string.completeFull,Toast.LENGTH_SHORT).show()
            return
        }
        if (password != passwordConfirm){
            Toast.makeText(this,R.string.passwordDiff,Toast.LENGTH_SHORT).show()
            return
        }
        //开始注册
        val user = User();
        user.pkId = StringUtil.getUUID();
        user.password = MessageDigetUtil.sha256(password)
        user.telphone = phone
        //指定默认用户名
        user.nickName = "手机用户${phone.substring(7)}";
        //启动注册任务
        UserTask(userListener).execute(HttpConfig.url + API.REGISTER.url + UrlUtil.parseURLPair(user))
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
