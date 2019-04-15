package ims.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ims.chat.R;
import ims.chat.entity.Message;

import java.util.List;

/**
 * @author yangchen
 * on 3/11/2019 11:19 PM
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView messageText;
        public ViewHolder(@NonNull View view) {
            super(view);
            userImage = view.findViewById(R.id.img_user_header);
            messageText = view.findViewById(R.id.txt_message);
        }

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = messageList.get(viewHolder.getAdapterPosition());

//                Snackbar.make(v,"Message:"+ message.content,Snackbar.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Message message = messageList.get(i);
        holder.userImage.setImageResource(R.drawable.qipao);
        holder.messageText.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
