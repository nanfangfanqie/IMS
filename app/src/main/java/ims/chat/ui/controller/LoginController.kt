package ims.chat.ui.controller

import android.app.ProgressDialog
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import ims.chat.R
import ims.chat.application.ImsApplication
import ims.chat.database.UserEntry
import ims.chat.ui.activity.FinishRegisterActivity
import ims.chat.ui.activity.LoginActivity
import ims.chat.ui.activity.MainActivity
import ims.chat.utils.*
import kotlinx.android.synthetic.main.input_form.*


@Suppress("DEPRECATION")
class LoginController(private val mContext: LoginActivity) : View.OnClickListener {

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSubmit -> {
                val userID = mContext.edtUserName.text.toString()
                val password = mContext.edtPassword.text.toString()
                Log.d("userID",userID)
                if (TextUtils.isEmpty(userID)) {
                    ToastUtil.shortToast(mContext, "用户名不得为空")
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.shortToast(mContext, "密码不得为空")
                }
                if (userID.length < 4 || userID.length > 20) {
                    ToastUtil.shortToast(mContext, "用户名为4-20位字符")
                    return
                }
                if (password.length < 4 || password.length > 20) {
                    ToastUtil.shortToast(mContext, "用户名为4-20位字符")
                    return
                }
                if (StringUtil.isContainChinese(userID)) {
                    ToastUtil.shortToast(mContext, "用户名不支持中文")
                    return
                }
                if (!StringUtil.whatStartWith(userID)) {
                    ToastUtil.shortToast(mContext, "用户名以字母或者数字开头")
                    return
                }
                if (!StringUtil.whatContain(userID)) {
                    ToastUtil.shortToast(mContext, "只能含有: 数字 字母 下划线 . - @")
                    return
                }
                if (ImsApplication.registerOrLogin % 2 == 1) {
                    val progressDialog = ProgressDialog(mContext)
                    progressDialog.setTitle("Tips:")
                    progressDialog.setMessage("登陆中...")
                    progressDialog.setCancelable(false);
                    progressDialog.show()
                    JMessageClient.login(
                        userID,
                        password,
                        object : BasicCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String?) {
                                progressDialog.dismiss();
                                if (responseCode == 0) {
                                    SharePreferenceManager.setCachedPsw(password)
                                    val userInfo: UserInfo = JMessageClient.getMyInfo()
                                    val avatarFile = userInfo.avatarFile
                                    if (avatarFile != null) {
                                        SharePreferenceManager.setCachedAvatarPath(avatarFile.absolutePath)
                                    } else {
                                        SharePreferenceManager.setCachedAvatarPath(null)
                                    }
                                    val username = userInfo.userName
                                    val appKey = userInfo.appKey
                                    var user = UserEntry.getUser(username, appKey)
                                    if (null == user) {
                                        user = UserEntry(username, appKey)
                                        user.save()
                                    }
                                    MainActivity.actionStart(mContext)

                                } else {
                                    ToastUtil.shortToast(mContext, "登陆失败$responseMessage")
                                }
                            }
                        });
                    //注册
                } else {
                    JMessageClient.register(userID, password, object : BasicCallback() {
                        override fun gotResult(i: Int, s: String?) {
                            if (i == 0) {
                                SharePreferenceManager.setRegisterName(userID)
                                SharePreferenceManager.setRegistePass(password)
                                mContext.startActivity(Intent(mContext, FinishRegisterActivity::class.java))
                                ToastUtil.shortToast(mContext, "注册成功")
                            } else {
                                HandleResponseCode.onHandle(mContext, i, false)
                            }
                        }
                    })
                }
            }
        }
    }
}

