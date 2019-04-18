package ims.chat.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.LoginStateChangeEvent
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import ims.chat.R
import ims.chat.ui.activity.LoginActivity
import ims.chat.ui.activity.MainActivity
import ims.chat.utils.FileHelper
import ims.chat.utils.SharePreferenceManager

/**
 * @author yangchen
 * on 2019/3/10 22:58
 */
open class BaseFragment : Fragment() {
    private val myInfo: UserInfo? = null
     var mDensity: Float = 0.toFloat()
     var mDensityDpi: Int = 0
     var mWidth: Int = 0
     var mHeight: Int = 0
     var mRatio: Float = 0.toFloat()
     var mAvatarSize: Int = 0
    private var mContext: Context? = null
    lateinit var mActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this.activity

        JMessageClient.registerEventReceiver(this)
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        mDensity = dm.density
        mDensityDpi = dm.densityDpi
        mWidth = dm.widthPixels
        mHeight = dm.heightPixels
        mRatio = Math.min(mWidth.toFloat() / 720, mHeight.toFloat() / 1280)
        mAvatarSize = (50 * mDensity).toInt()
    }

    fun onEventMainThread(event: LoginStateChangeEvent) {
        val reason = event.reason
        val myInfo = event.myInfo
        if (myInfo != null) {
            val path: String
            val avatar = myInfo.avatarFile
            if (avatar != null && avatar.exists()) {
                path = avatar.absolutePath
            } else {
                path = FileHelper.getUserAvatarPath(myInfo.userName)
            }
            SharePreferenceManager.setCachedUsername(myInfo.userName)
            SharePreferenceManager.setCachedAvatarPath(path)
            JMessageClient.logout()
        }
        when (reason) {
            LoginStateChangeEvent.Reason.user_logout -> {
                val listener = View.OnClickListener { v ->
                    when (v.id) {
                        R.id.jmui_cancel_btn -> {
                            val intent = Intent(mContext, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        R.id.jmui_commit_btn -> JMessageClient.login(
                            SharePreferenceManager.getCachedUsername(),
                            SharePreferenceManager.getCachedPsw(),
                            object :
                                BasicCallback() {
                                override fun gotResult(responseCode: Int, responseMessage: String) {
                                    if (responseCode == 0) {
                                        val intent = Intent(mContext, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            })
                    }
                }
            }
            LoginStateChangeEvent.Reason.user_password_change -> {
                val intent = Intent(mContext, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        //注销消息接收
        JMessageClient.unRegisterEventReceiver(this)
        super.onDestroy()
    }

    override fun onAttach(context: Activity?) {
        super.onAttach(context)
        mActivity = context!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.base_fragment, container, false)
    }

    companion object {
        fun newInstance(info: String): BaseFragment {
            val args = Bundle()
            val fragment = BaseFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

}