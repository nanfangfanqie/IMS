package ims.chat.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ims.chat.R;
import ims.chat.application.ImsApplication;
import ims.chat.database.FriendEntry;
import ims.chat.ui.activity.FriendInfoActivity;
import java.util.List;
/**
 * @author yangchen
 * on 3/15/2019 3:01 PM
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<FriendEntry> friendList;
    public FriendAdapter(List<FriendEntry> friends) {
        this.friendList = friends;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.img_friend_header);
            textView = view.findViewById(R.id.txt_username);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendEntry user = friendList.get(viewHolder.getAdapterPosition());
                //进入个人资料界面
                Intent intent = new Intent(ImsApplication.getContext(),FriendInfoActivity.class);
                intent.putExtra("fromContact", true);
                intent.putExtra(ImsApplication.TARGET_ID, user.username);
                intent.putExtra(ImsApplication.TARGET_APP_KEY, user.appKey);
                ImsApplication.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendEntry user = friendList.get(position);
        holder.imageView.setImageResource(R.drawable.qipao);
        holder.textView.setText(user.username);
        if(!user.noteName.isEmpty())  holder.textView.setText(user.noteName);
        if (!user.nickName.isEmpty()) holder.textView.setText(user.nickName);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
