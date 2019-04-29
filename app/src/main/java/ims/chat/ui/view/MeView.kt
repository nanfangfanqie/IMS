package ims.chat.ui.view

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.IntegerCallback
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import ims.chat.R
import ims.chat.utils.DialogCreator
import ims.chat.utils.ToastUtil
import ims.chat.utils.photochoose.SelectableRoundedImageView


class MeView(private val mContext: Context, attrs: AttributeSet) : LinearLayout(mContext, attrs),
    SlipButton.OnChangedListener {
    private var mSignatureTv: TextView? = null
    private var mNickNameTv: TextView? = null
    private var mTakePhotoBtn: SelectableRoundedImageView? = null
    private var mSet_pwd: RelativeLayout? = null
    lateinit var mSet_noDisturb: SlipButton
    private var mExit: RelativeLayout? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mRl_personal: RelativeLayout? = null


    fun initModule(density: Float, width: Int) {
        mTakePhotoBtn = findViewById<View>(R.id.take_photo_iv) as SelectableRoundedImageView?
        mNickNameTv = findViewById<View>(R.id.nickName) as TextView?
        mSignatureTv = findViewById<View>(R.id.signature) as TextView?
        mSet_pwd = findViewById<View>(R.id.setPassword) as RelativeLayout?
        mSet_noDisturb = findViewById<View>(R.id.btn_noDisturb) as SlipButton
        mExit = findViewById<View>(R.id.exit) as RelativeLayout?
        mRl_personal = findViewById<View>(R.id.rl_personal) as RelativeLayout?
        mSet_noDisturb.setOnChangedListener(R.id.btn_noDisturb, this)

        mWidth = width
        mHeight = (190 * density).toInt()


        val dialog = DialogCreator.createLoadingDialog(mContext, mContext.getString(R.string.jmui_loading))
        dialog.show()
        //初始化是否全局免打扰
        JMessageClient.getNoDisturbGlobal(object : IntegerCallback() {
            override fun gotResult(responseCode: Int, responseMessage: String, value: Int?) {
                dialog.dismiss()
                if (responseCode == 0) {
                    mSet_noDisturb.setChecked(value == 1)
                } else {
                    ToastUtil.shortToast(mContext, responseMessage)
                }
            }
        })
    }

    fun setListener(onClickListener: View.OnClickListener) {
        mSet_pwd!!.setOnClickListener(onClickListener)
        mExit!!.setOnClickListener(onClickListener)
        mRl_personal!!.setOnClickListener(onClickListener)


    }

    fun showPhoto(avatarBitmap: Bitmap?) {
        if (avatarBitmap != null) {
            mTakePhotoBtn!!.setImageBitmap(avatarBitmap)
        } else {
            mTakePhotoBtn!!.setImageResource(R.drawable.rc_default_portrait)
        }

    }

    fun showNickName(myInfo: UserInfo) {
        if (!TextUtils.isEmpty(myInfo.nickname.trim { it <= ' ' })) {
            mNickNameTv!!.text = myInfo.nickname
        } else {
            mNickNameTv!!.text = myInfo.userName
        }
        mSignatureTv!!.text = myInfo.signature
    }

    override fun onChanged(id: Int, checkState: Boolean) {
        when (id) {
            R.id.btn_noDisturb -> {
                val loadingDialog = DialogCreator.createLoadingDialog(
                    mContext,
                    mContext.getString(R.string.jmui_loading)
                )
                loadingDialog.show()
                JMessageClient.setNoDisturbGlobal(if (checkState) 1 else 0, object : BasicCallback() {
                    override fun gotResult(status: Int, desc: String) {
                        loadingDialog.dismiss()
                        if (status == 0) {
                        } else {
                            mSet_noDisturb.setChecked(!checkState)
                            ToastUtil.shortToast(mContext, "设置失败")
                        }
                    }
                })
            }
        }
    }

}
