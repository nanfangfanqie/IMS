package ims.chat.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import ims.chat.R
import ims.chat.utils.CommonUtils
import ims.chat.utils.ToastUtil

class ResetPasswordActivity : BaseActivity() {

    private var mOld_password: EditText? = null
    private var mNew_password: EditText? = null
    private var mRe_newPassword: EditText? = null
    private var mBtn_sure: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        initView()
        initData()

    }

    private fun initView() {
        initTitle(true, true, "修改密码", "", false, "保存")
        mOld_password = findViewById(R.id.old_password) as EditText
        mNew_password = findViewById(R.id.new_password) as EditText
        mRe_newPassword = findViewById(R.id.re_newPassword) as EditText
        mBtn_sure = findViewById(R.id.btn_sure) as Button
    }

    private fun initData() {
        mBtn_sure!!.setOnClickListener {
            val oldPsw = mOld_password!!.text.toString().trim { it <= ' ' }
            val newPsw = mNew_password!!.text.toString().trim { it <= ' ' }
            val reNewPsw = mRe_newPassword!!.text.toString().trim { it <= ' ' }

            val passwordValid = JMessageClient.isCurrentUserPasswordValid(oldPsw)
            if (passwordValid) {
                if (newPsw == reNewPsw) {
                    val dialog = ProgressDialog(this@ResetPasswordActivity)
                    dialog.setMessage(getString(R.string.modifying_hint))
                    dialog.show()
                    JMessageClient.updateUserPassword(oldPsw, newPsw, object : BasicCallback() {
                        override fun gotResult(responseCode: Int, responseMessage: String) {
                            dialog.dismiss()
                            if (responseCode == 0) {
                                ToastUtil.shortToast(this@ResetPasswordActivity, "修改成功")
                            } else {
                                ToastUtil.shortToast(this@ResetPasswordActivity, "修改失败, 新密码要在4-128字节之间")
                            }
                        }
                    })
                } else {
                    ToastUtil.shortToast(this@ResetPasswordActivity, "两次输入不相同")
                }
            } else {
                ToastUtil.shortToast(this@ResetPasswordActivity, "原密码不正确")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        CommonUtils.hideKeyboard(this)
    }
}
