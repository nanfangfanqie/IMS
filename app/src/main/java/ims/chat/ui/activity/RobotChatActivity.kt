package ims.chat.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import ims.chat.application.ImsApplication
import ims.chat.R
import ims.chat.adapter.RobotMsgAdapter
import ims.chat.constant.API
import ims.chat.entity.Msg
import ims.chat.entity.User
import ims.chat.listener.Listener
import ims.chat.task.RobotTask
import ims.chat.utils.MyToast
import ims.chat.utils.RobotConfig
import ims.chat.vo.MessageResult
import kotlinx.android.synthetic.main.robot_chat_activity.*
import kotlinx.android.synthetic.main.toolbar.*
import java.net.SocketTimeoutException
import java.util.*

class RobotChatActivity : AppCompatActivity() {
    private val key = RobotConfig.KEYS[2]
    private val msgList = ArrayList<Msg>()
    private lateinit var msgAdapter: RobotMsgAdapter
    private val robotListener:Listener<Msg> = object :Listener<Msg>{
        override fun onSuccess(t: Msg) {
            addMsg(t)
        }
        override fun onError(messageResult: MessageResult<*>) {
            if(messageResult.data is String){
                MyToast.toastShort(ImsApplication.getContext()!!,messageResult.data.toString())
            }else{
                var t = messageResult.data as Throwable
                if (t is SocketTimeoutException){
                    MyToast.toastShort(ImsApplication.getContext()!!,"连接超时")
                }else{
                    MyToast.toastShort(ImsApplication.getContext()!!,"其他异常")
                }
            }
        }
    }

    override fun onBackPressed() {
        finish();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.robot_chat_activity)
        initMsg()
        init()
    }

    private fun init(){
        val user:User = intent.getSerializableExtra("user") as User
        var linearLayoutManager = LinearLayoutManager(this)
        toolbar.title = "人工智障"
        setSupportActionBar(toolbar)
        rvRobotChat.layoutManager = linearLayoutManager
        msgAdapter = RobotMsgAdapter(msgList)
        rvRobotChat.adapter = msgAdapter
        btnSend.setOnClickListener {
            val content = edtMessage.text.toString()
            if (content.isNotEmpty()){
                addMsg(Msg(content, Msg.SEND))
                //清空内容
                edtMessage.setText("")
            }
            //启动机器人任务
            RobotTask(robotListener).execute("${API.TURNING.url}key=$key&info=$content&userid=${user.nickName}")
        }
    }

    /**
     * 添加消息
     * @param msg 要添加的消息
     */
    private fun addMsg(msg: Msg){
        msgList.add(msg)
        //当有新消息时，刷新ListView中的显示
        msgAdapter.notifyItemInserted(msgList.size - 1)
        //定位到最后一行
        rvRobotChat.scrollToPosition(msgList.size - 1);
    }

    private fun initMsg(){
         var c = Calendar.getInstance()
         var hour = c.get(Calendar.HOUR_OF_DAY)
         var msg:String
         when(hour){
             in 6..11 -> msg = "早上好，见到你很高兴！"
             12 -> msg = "中午好，可以开始午睡啦！"
             in 13..18 -> msg = "下午好，你能来我很开心!"
             in 19..23 ->msg = "晚上好，今天过得怎么样?"
             else -> msg = "夜深啦，请注意休息！！"
         }
        msgList.add(Msg(msg, Msg.RECEIVED));
    }
}
