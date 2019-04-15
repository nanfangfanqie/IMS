package ims.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ims.chat.R;
import ims.chat.entity.Friend;
import ims.chat.ui.activity.PersonalInfoActivity;

import java.util.List;

/**
 * @author yangchen
 * on 3/15/2019 3:01 PM
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<Friend> friendList;

    public FriendAdapter(List<Friend> friends) {
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

    public void setFriendList(List<Friend> userList) {
        this.friendList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Friend friend = friendList.get(viewHolder.getAdapterPosition());
                //进入个人资料界面
                PersonalInfoActivity.Companion.actionStart(parent.getContext(),friend.getFriendId(),friend.getNickName());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend user = friendList.get(position);
        holder.imageView.setImageResource(R.drawable.qipao);
        holder.textView.setText(user.getNickName());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
