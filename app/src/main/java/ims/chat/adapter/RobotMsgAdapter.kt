package ims.chat.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import ims.chat.R
import ims.chat.entity.Msg

/**机器人消息适配器
 * @author yangchen
 * on 3/15/2019 3:01 PM
 */
class RobotMsgAdapter(internal var msgList: List<Msg>) : RecyclerView.Adapter<RobotMsgAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var recvLayout: LinearLayout
        var sendLayout: LinearLayout

        var leftMsg: TextView
        var rightMsg: TextView

        init {
            recvLayout = view.findViewById(R.id.layoutLeft)
            sendLayout = view.findViewById(R.id.layoutRight)
            leftMsg = view.findViewById(R.id.leftMsg)
            rightMsg = view.findViewById(R.id.rightMsg)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.robot_msg_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = msgList[position]
        if (msg.type == Msg.RECEIVED) {
            holder.sendLayout.visibility = View.GONE
            holder.recvLayout.visibility = View.VISIBLE
            holder.leftMsg.text = msg.content
        } else {
            holder.recvLayout.visibility = View.GONE
            holder.sendLayout.visibility = View.VISIBLE
            holder.rightMsg.text = msg.content
        }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }
}
