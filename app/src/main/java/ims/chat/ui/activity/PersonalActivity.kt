package ims.chat.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.bigkoo.pickerview.TimePickerView
import ims.chat.R
import ims.chat.utils.SharePreferenceManager
import ims.chat.utils.ThreadUtil
import ims.chat.utils.ToastUtil
import ims.chat.utils.citychoose.view.myinterface.SelectAddressInterface
import ims.chat.utils.photochoose.ChoosePhoto
import ims.chat.utils.photochoose.PhotoUtils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by ${chenyn} on 2017/2/23.
 */

class PersonalActivity : BaseActivity(), SelectAddressInterface, View.OnClickListener {

    private var mRl_cityChoose: RelativeLayout? = null
    private var mTv_city: TextView? = null
    private var mRl_gender: RelativeLayout? = null
    private var mRl_birthday: RelativeLayout? = null

    private var mTv_birthday: TextView? = null
    private var mTv_gender: TextView? = null
    private var mSign: RelativeLayout? = null
    private var mTv_sign: TextView? = null
    private var mRl_nickName: RelativeLayout? = null
    private var mTv_nickName: TextView? = null
    private var mIv_photo: ImageView? = null
    private var mChoosePhoto: ChoosePhoto? = null
    private var mMyInfo: UserInfo? = null
    private var mTv_userName: TextView? = null
    private var mRl_zxing: RelativeLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        initView()
        initListener()
        initData()
    }

    private fun initData() {
        mMyInfo = JMessageClient.getMyInfo()
        if (mMyInfo != null) {
            mTv_nickName!!.text = mMyInfo!!.nickname
            SharePreferenceManager.setRegisterUsername(mMyInfo!!.nickname)
            mTv_userName!!.text = "用户名:" + mMyInfo!!.userName
            mTv_sign!!.text = mMyInfo!!.signature
            val gender = mMyInfo!!.gender
            if (gender != null) {
                if (gender == UserInfo.Gender.male) {
                    mTv_gender!!.text = "男"
                } else if (gender == UserInfo.Gender.female) {
                    mTv_gender!!.text = "女"
                } else {
                    mTv_gender!!.text = "保密"
                }
            }
            val birthday = mMyInfo!!.birthday
            if (birthday == 0L) {
                mTv_birthday!!.text = ""
            } else {
                val date = Date(mMyInfo!!.birthday)
                val format = SimpleDateFormat("yyyy-MM-dd")
                mTv_birthday!!.text = format.format(date)
            }
            mTv_city!!.text = mMyInfo!!.address
            mMyInfo!!.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                override fun gotResult(responseCode: Int, responseMessage: String, avatarBitmap: Bitmap) {
                    if (responseCode == 0) {
                        mIv_photo!!.setImageBitmap(avatarBitmap)
                    } else {
                        mIv_photo!!.setImageResource(R.drawable.rc_default_portrait)
                    }
                }
            })
            //            dialog.dismiss();
        }
    }

    private fun initListener() {
        mRl_cityChoose!!.setOnClickListener(this)
        mRl_birthday!!.setOnClickListener(this)
        mRl_gender!!.setOnClickListener(this)
        mSign!!.setOnClickListener(this)
        mRl_nickName!!.setOnClickListener(this)
        mIv_photo!!.setOnClickListener(this)
        mRl_zxing!!.setOnClickListener(this)
    }

    private fun initView() {

        mRl_cityChoose = findViewById(R.id.rl_cityChoose) as RelativeLayout
        mTv_city = findViewById(R.id.tv_city) as TextView
        mRl_gender = findViewById(R.id.rl_gender) as RelativeLayout
        mRl_birthday = findViewById(R.id.rl_birthday) as RelativeLayout
        mTv_birthday = findViewById(R.id.tv_birthday) as TextView
        mTv_gender = findViewById(R.id.tv_gender) as TextView
        mSign = findViewById(R.id.sign) as RelativeLayout
        mTv_sign = findViewById(R.id.tv_sign) as TextView
        mRl_nickName = findViewById(R.id.rl_nickName) as RelativeLayout
        mTv_nickName = findViewById(R.id.tv_nickName) as TextView
        mIv_photo = findViewById(R.id.iv_photo) as ImageView
        mTv_userName = findViewById(R.id.tv_userName) as TextView
        mRl_zxing = findViewById(R.id.rl_zxing) as RelativeLayout
        mChoosePhoto = ChoosePhoto()
        mChoosePhoto!!.setPortraitChangeListener(this@PersonalActivity, mIv_photo!!, 2)

    }

    override fun setAreaString(area: String) {
        mTv_city!!.text = area
    }

    override fun setTime(time: String) {}

    override fun setGender(gender: String) {
        mTv_gender!!.text = gender
    }

    override fun onClick(v: View) {
        intent = Intent(this@PersonalActivity, NickSignActivity::class.java)
        when (v.id) {
            R.id.iv_photo -> {
                //头像
                if (ContextCompat.checkSelfPermission(
                        this@PersonalActivity,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        this@PersonalActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this@PersonalActivity, "请在应用管理中打开“读写存储”和“相机”访问权限！", Toast.LENGTH_SHORT).show()
                }
                mChoosePhoto!!.setInfo(this@PersonalActivity, true)
                mChoosePhoto!!.showPhotoDialog(this@PersonalActivity)
            }
            R.id.rl_nickName -> {
                //昵称
                intent.flags = FLAGS_NICK
                intent.putExtra("old_nick", mMyInfo!!.nickname)
                startActivityForResult(intent, NICK_NAME)
            }
            R.id.sign -> {
                //签名
                intent.flags = FLAGS_SIGN
                intent.putExtra("old_sign", mMyInfo!!.signature)
                startActivityForResult(intent, SIGN)
            }
            R.id.rl_gender -> {
                //弹出性别选择器
            }
            R.id.rl_birthday -> {
                //弹出时间选择器选择生日
                val timePickerView =
                    TimePickerView.Builder(this@PersonalActivity, TimePickerView.OnTimeSelectListener { date, v ->
                        mMyInfo!!.birthday = date.time
                        JMessageClient.updateMyInfo(UserInfo.Field.birthday, mMyInfo, object : BasicCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String) {
                                if (responseCode == 0) {
                                    mTv_birthday!!.text = getDataTime(date)
                                    Toast.makeText(this@PersonalActivity, "更新成功", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@PersonalActivity, "更新失败", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    })
                        .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                        .setCancelText("取消")
                        .setSubmitText("确定")
                        .setContentSize(20)//滚轮文字大小
                        .setTitleSize(20)//标题文字大小
                        .setOutSideCancelable(true)
                        .isCyclic(true)
                        .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                        .setSubmitColor(Color.GRAY)//确定按钮文字颜色
                        .setCancelColor(Color.GRAY)//取消按钮文字颜色
                        .isCenterLabel(false)
                        .build()
                timePickerView.show()
            }
            else -> {
            }
        }//                dialog = new SelectAddressDialog(PersonalActivity.this);
        //                dialog.showDateDialog(PersonalActivity.this, mMyInfo);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            val bundle = data.extras
            when (resultCode) {
                SIGN -> {
                    val sign = bundle!!.getString(SIGN_KEY)
                    ThreadUtil.runInThread {
                        mMyInfo!!.signature = sign
                        JMessageClient.updateMyInfo(UserInfo.Field.signature, mMyInfo, object : BasicCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String) {
                                if (responseCode == 0) {
                                    mTv_sign!!.text = sign
                                    ToastUtil.shortToast(this@PersonalActivity, "更新成功")
                                } else {
                                    ToastUtil.shortToast(this@PersonalActivity, "更新失败")
                                }
                            }
                        })
                    }
                }
                NICK_NAME -> {
                    val nick = bundle!!.getString(NICK_NAME_KEY)
                    ThreadUtil.runInThread {
                        mMyInfo!!.nickname = nick
                        JMessageClient.updateMyInfo(UserInfo.Field.nickname, mMyInfo, object : BasicCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String) {
                                if (responseCode == 0) {
                                    mTv_nickName!!.text = nick
                                    ToastUtil.shortToast(this@PersonalActivity, "更新成功")
                                } else {
                                    ToastUtil.shortToast(this@PersonalActivity, "更新失败,请正确输入")
                                }
                            }
                        })
                    }
                }
                else -> {
                }
            }
        }
        when (requestCode) {
            PhotoUtils.INTENT_CROP, PhotoUtils.INTENT_TAKE, PhotoUtils.INTENT_SELECT -> mChoosePhoto!!.photoUtils.onActivityResult(
                this@PersonalActivity,
                requestCode,
                resultCode,
                data
            )
        }
    }

    fun getDataTime(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    companion object {

        const val SIGN = 1
        const val FLAGS_SIGN = 2
        const val SIGN_KEY = "sign_key"

        const val NICK_NAME = 4
        const val FLAGS_NICK = 3
        const val NICK_NAME_KEY = "nick_name_key"
    }

}
