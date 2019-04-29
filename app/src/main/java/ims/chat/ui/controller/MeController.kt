package ims.chat.ui.controller

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.view.WindowManager
import ims.chat.R
import ims.chat.ui.activity.PersonalActivity
import ims.chat.ui.activity.ResetPasswordActivity
import ims.chat.ui.fragment.MeFragment
import ims.chat.utils.DialogCreator



class MeController(private val mContext: MeFragment, private val mWidth: Int) : View.OnClickListener {
    private var mDialog: Dialog? = null
    private var mBitmap: Bitmap? = null

    fun setBitmap(bitmap: Bitmap) {
        this.mBitmap = bitmap
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.setPassword -> mContext.startActivity(Intent(mContext.context, ResetPasswordActivity::class.java))
            R.id.exit -> {
                val listener = View.OnClickListener { v ->
                    when (v.id) {
                        R.id.jmui_cancel_btn -> mDialog!!.cancel()
                        R.id.jmui_commit_btn -> {
                            mContext.logOut()
                            mContext.cancelNotification()
                            mContext.activity!!.finish()
                            mDialog!!.cancel()
                        }
                    }
                }
                mDialog = DialogCreator.createLogoutDialog(mContext.activity, listener)
                mDialog!!.window!!.setLayout((0.8 * mWidth).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
                mDialog!!.show()
            }
            R.id.rl_personal -> {
                val intent = Intent(mContext.context, PersonalActivity::class.java)
                intent.putExtra(PERSONAL_PHOTO, mBitmap)
                mContext.startActivity(intent)
            }
        }
    }

    companion object {
        const val PERSONAL_PHOTO = "personal_photo"
    }
}
