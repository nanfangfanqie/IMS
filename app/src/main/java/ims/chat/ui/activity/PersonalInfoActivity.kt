package ims.chat.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ims.chat.R
import kotlinx.android.synthetic.main.personal_info_activity.*
import kotlinx.android.synthetic.main.toolbar.*

class PersonalInfoActivity : IBaseActivity() {
    /**
     * 初始化
     */
    private fun init() {
        val intent = intent
        val friendId = intent.getStringExtra("param1")
        val nickName = intent.getStringExtra("param2")
        toolbar.title = nickName
        tvFriendInfo.text = "资料页面"
        btnSendMsg.setOnClickListener {
//            ChatActivity.actionStart(
//                this@PersonalInfoActivity,
//                friendId,
//                nickName
////            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_info_activity)
        init()
    }

    companion object {

        fun actionStart(context: Context, vararg dataArr: String) {
            val intent = Intent(context, PersonalInfoActivity::class.java)
            if (dataArr.isNotEmpty()) {
                for (i in dataArr.indices) {
                    intent.putExtra("param" + (i + 1), dataArr[i])
                }
            }
            context.startActivity(intent)
        }
    }
}
