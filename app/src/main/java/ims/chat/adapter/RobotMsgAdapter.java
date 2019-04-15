package ims.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ims.chat.R;
import ims.chat.entity.Msg;

import java.util.List;

/**机器人消息适配器
 * @author yangchen
 * on 3/15/2019 3:01 PM
 */
public class RobotMsgAdapter extends RecyclerView.Adapter<RobotMsgAdapter.ViewHolder> {

    List<Msg> msgList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout recvLayout;
        LinearLayout sendLayout;

        TextView leftMsg;
        TextView rightMsg;
        public ViewHolder(@NonNull View view) {
            super(view);
            recvLayout = view.findViewById(R.id.layoutLeft);
            sendLayout = view.findViewById(R.id.layoutRight);
            leftMsg = view.findViewById(R.id.leftMsg);
            rightMsg = view.findViewById(R.id.rightMsg);
        }

    }

    public RobotMsgAdapter(List<Msg> msgList) {
        this.msgList = msgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.robot_msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        if (msg.getType() == Msg.RECEIVED) {
            holder.sendLayout.setVisibility(View.GONE);
            holder.recvLayout.setVisibility(View.VISIBLE);
            holder.leftMsg.setText(msg.getContent());
        } else {
            holder.recvLayout.setVisibility(View.GONE);
            holder.sendLayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
