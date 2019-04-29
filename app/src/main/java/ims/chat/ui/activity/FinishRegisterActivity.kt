package ims.chat.ui.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import ims.chat.R
import ims.chat.application.ImsApplication
import ims.chat.database.UserEntry
import ims.chat.utils.SharePreferenceManager
import ims.chat.utils.ThreadUtil
import ims.chat.utils.photochoose.ChoosePhoto
import ims.chat.utils.photochoose.PhotoUtils
import ims.chat.utils.photochoose.SelectableRoundedImageView
import kotlinx.android.synthetic.main.activity_finish_register.*

import java.io.File

class FinishRegisterActivity : BaseActivity() {
    private var mNickNameEt: EditText? = null
    private var mAvatarIv: SelectableRoundedImageView? = null
    private var mFinishBtn: Button? = null
    private var mContext: Context? = null
    private var mDialog: ProgressDialog? = null
    private var mTv_nickCount: TextView? = null
    private var mChoosePhoto: ChoosePhoto? = null
    private var mIv_back: ImageView? = null


    private val listener = View.OnClickListener { view ->
        when (view) {
            mine_header -> if (ContextCompat.checkSelfPermission(
                    this@FinishRegisterActivity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this@FinishRegisterActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this@FinishRegisterActivity, "请在应用管理中打开“读写存储”和“相机”访问权限！", Toast.LENGTH_SHORT).show()
            } else {
                mChoosePhoto!!.showPhotoDialog(this@FinishRegisterActivity)
            }
            iv_back -> finish()
            finish_btn -> {
                mDialog = ProgressDialog(this@FinishRegisterActivity)
                mDialog!!.setCancelable(false)
                mDialog!!.show()
                val userId = SharePreferenceManager.getRegistrName()
                val password = SharePreferenceManager.getRegistrPass()
                SharePreferenceManager.setRegisterUsername(userId)
                JMessageClient.login(userId, password, object : BasicCallback() {
                    override fun gotResult(responseCode: Int, responseMessage: String) {
                        if (responseCode == 0) {
                            ImsApplication.registerOrLogin = 1
                            val username = JMessageClient.getMyInfo().userName
                            val appKey = JMessageClient.getMyInfo().appKey
                            var user: UserEntry? = UserEntry.getUser(username, appKey)
                            if (null == user) {
                                user = UserEntry(username, appKey)
                                user.save()
                            }
                            val nickName = mNickNameEt!!.text.toString()
                            val myUserInfo = JMessageClient.getMyInfo()
                            if (myUserInfo != null) {
                                myUserInfo.nickname = nickName
                            }
                            //注册时候更新昵称
                            JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, object : BasicCallback() {
                                override fun gotResult(status: Int, desc: String) {
                                    //更新跳转标志
                                    SharePreferenceManager.setCachedFixProfileFlag(false)
                                    mDialog!!.dismiss()
                                    if (status == 0) {
                                       val intent = Intent(this@FinishRegisterActivity,
                                           MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            })
                            //注册时更新头像
                            val avatarPath = SharePreferenceManager.getRegisterAvatarPath()
                            if (avatarPath != null) {
                                ThreadUtil.runInThread {
                                    JMessageClient.updateUserAvatar(File(avatarPath), object : BasicCallback() {
                                        override fun gotResult(responseCode: Int, responseMessage: String) {
                                            if (responseCode == 0) {
                                                SharePreferenceManager.setCachedAvatarPath(avatarPath)
                                            }
                                        }
                                    })
                                }
                            } else {
                                SharePreferenceManager.setCachedAvatarPath(null)
                            }
                        }
                    }
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_register)
        mContext = this
        initView()
    }

    private fun initView() {
        mNickNameEt = findViewById(R.id.nick_name_et) as EditText
        mAvatarIv = findViewById(R.id.mine_header) as SelectableRoundedImageView
        mFinishBtn = findViewById(R.id.finish_btn) as Button
        mTv_nickCount = findViewById(R.id.tv_nickCount) as TextView
        mIv_back = findViewById(R.id.iv_back) as ImageView
        mNickNameEt!!.addTextChangedListener(TextChange())
        mAvatarIv!!.setOnClickListener(listener)
        mFinishBtn!!.setOnClickListener(listener)
        mIv_back!!.setOnClickListener(listener)
        SharePreferenceManager.setCachedFixProfileFlag(true)
        mNickNameEt!!.requestFocus()
        SharePreferenceManager.setRegisterAvatarPath(null)
        mChoosePhoto = ChoosePhoto()
        mChoosePhoto!!.setPortraitChangeListener(this@FinishRegisterActivity, mAvatarIv, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PhotoUtils.INTENT_CROP, PhotoUtils.INTENT_TAKE, PhotoUtils.INTENT_SELECT -> mChoosePhoto!!.photoUtils.onActivityResult(
                this@FinishRegisterActivity,
                requestCode,
                resultCode,
                data
            )
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    private inner class TextChange : TextWatcher {

        override fun afterTextChanged(arg0: Editable) {
            val num = MAX_COUNT - arg0.length
            mTv_nickCount!!.text = num.toString() + ""
        }

        override fun beforeTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int,
            arg3: Int
        ) {

        }

        override fun onTextChanged(
            cs: CharSequence, start: Int, before: Int,
            count: Int
        ) {

        }
    }

    companion object {
        private val MAX_COUNT = 30
    }
}
