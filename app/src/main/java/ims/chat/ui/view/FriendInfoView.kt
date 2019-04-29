package ims.chat.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.*
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.model.UserInfo
import ims.chat.R
import ims.chat.ui.controller.FriendInfoController
import ims.chat.ui.activity.FriendInfoActivity

import java.text.SimpleDateFormat
import java.util.Date



class FriendInfoView : LinearLayout {


    private val mListeners: FriendInfoController? = null
    private var mOnChangeListener: FriendInfoController? = null
    private var mIv_friendPhoto: ImageView? = null
    private var mTv_noteName: TextView? = null
    private var mTv_signature: TextView? = null
    private var mTv_userName: TextView? = null
    private var mTv_gender: TextView? = null
    private var mTv_birthday: TextView? = null
    private var mTv_address: TextView? = null
    private var mBtn_goToChat: Button? = null
    private var mContext: Context? = null
    private var mSetting: ImageView? = null
    private var mReturnBtn: ImageButton? = null
    private var mRl_NickName: RelativeLayout? = null
    private var mTv_NickName: TextView? = null


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun initModel(context: FriendInfoActivity) {
        this.mContext = context
        mIv_friendPhoto = findViewById<View>(R.id.iv_friendPhoto) as ImageView
        mTv_noteName = findViewById<View>(R.id.tv_nickName) as TextView
        mTv_signature = findViewById<View>(R.id.tv_signature) as TextView
        mTv_userName = findViewById<View>(R.id.tv_userName) as TextView
        mTv_gender = findViewById<View>(R.id.tv_gender) as TextView
        mTv_birthday = findViewById<View>(R.id.tv_birthday) as TextView
        mTv_address = findViewById<View>(R.id.tv_address) as TextView
        mBtn_goToChat = findViewById<View>(R.id.btn_goToChat) as Button
        mSetting = findViewById<View>(R.id.jmui_commit_btn) as ImageView
        mReturnBtn = findViewById<View>(R.id.return_btn) as ImageButton
        mRl_NickName = findViewById<View>(R.id.rl_nickName) as RelativeLayout
        mTv_NickName = findViewById<View>(R.id.tv_nick) as TextView

    }

    fun setListeners(listeners: View.OnClickListener) {
        mBtn_goToChat!!.setOnClickListener(listeners)
        mIv_friendPhoto!!.setOnClickListener(listeners)
        mSetting!!.setOnClickListener(listeners)
        mReturnBtn!!.setOnClickListener(listeners)

    }

    fun setOnChangeListener(onChangeListener: FriendInfoController) {
        mOnChangeListener = onChangeListener
    }


    fun initInfo(userInfo: UserInfo?) {
        if (userInfo != null) {
            if (!TextUtils.isEmpty(userInfo.avatar)) {
                userInfo.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                    override fun gotResult(status: Int, desc: String, bitmap: Bitmap) {
                        if (status == 0) {
                            mIv_friendPhoto!!.setImageBitmap(bitmap)
                        } else {
                            mIv_friendPhoto!!.setImageResource(R.drawable.rc_default_portrait)
                        }
                    }
                })
            } else {
                mIv_friendPhoto!!.setImageResource(R.drawable.rc_default_portrait)
            }
            val noteName = userInfo.notename
            val nickName = userInfo.nickname
            val userName = userInfo.userName
            //有备注 有昵称
            mTv_userName!!.text = userName
            if (!TextUtils.isEmpty(noteName) && !TextUtils.isEmpty(nickName)) {
                mRl_NickName!!.visibility = View.VISIBLE
                mTv_NickName!!.text = nickName
                mTv_noteName!!.text = "备注名: $noteName"
            } else if (TextUtils.isEmpty(noteName) && !TextUtils.isEmpty(nickName)) {
                mRl_NickName!!.visibility = View.GONE
                mTv_noteName!!.text = "昵称: $nickName"
            } else if (!TextUtils.isEmpty(noteName) && TextUtils.isEmpty(nickName)) {
                mRl_NickName!!.visibility = View.VISIBLE
                mTv_NickName!!.text = userInfo.nickname
                mTv_noteName!!.text = "备注名: $noteName"
            } else {
                mRl_NickName!!.visibility = View.GONE
                mTv_noteName!!.text = "用户名: $userName"
            }//没有备注名 没有昵称
            //有备注 没有昵称
            //没有备注 有昵称
            if (userInfo.gender == UserInfo.Gender.male) {
                mTv_gender!!.text = mContext!!.getString(R.string.man)
            } else if (userInfo.gender == UserInfo.Gender.female) {
                mTv_gender!!.text = mContext!!.getString(R.string.woman)
            } else {
                mTv_gender!!.text = mContext!!.getString(R.string.unknown)
            }
            mTv_address!!.text = userInfo.region
            mTv_signature!!.text = userInfo.signature
            mTv_birthday!!.text = getBirthday(userInfo)
        }
    }

    fun getBirthday(info: UserInfo): String {
        val birthday = info.birthday
        if (birthday == 0L) {
            return ""
        } else {
            val date = Date(birthday)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            return dateFormat.format(date)
        }
    }
}
