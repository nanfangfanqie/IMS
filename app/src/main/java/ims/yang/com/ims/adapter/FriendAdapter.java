package ims.yang.com.ims.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ims.yang.com.ims.R;
import ims.yang.com.ims.entity.Friend;

import java.util.List;

/**
 * @author yangchen
 * on 3/15/2019 3:01 PM
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<Friend> userList;

    public FriendAdapter(List<Friend> friends) {
        this.userList = friends;
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
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        FriendAdapter.ViewHolder viewHolder = new FriendAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend user = userList.get(position);
        holder.imageView.setImageResource(R.drawable.qipao);
        holder.textView.setText(user.getNickName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


}
