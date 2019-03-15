package ims.yang.com.ims.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ims.yang.com.ims.R;
import ims.yang.com.ims.adapter.MessageAdapter;
import ims.yang.com.ims.entity.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangchen
 * on 3/12/2019 12:03 AM
 */
public class MessageListFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;

    private List<Message> getMsgs() {
        List<Message> messageList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Message message = new Message("很长很长很长很长很长很长很长很长很长的消息" + i);
            messageList.add(message);
        }
        return messageList;
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        List<Message> messageList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Message message = new Message("不那么长的消息" + i);
            messageList.add(message);
        }
        messageAdapter.setMessageList(messageList);
        recyclerView.setAdapter(messageAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_frag, container, false);
        recyclerView = view.findViewById(R.id.msg_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(getMsgs());
        //设置适配器
        recyclerView.setAdapter(messageAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟发送网络请求
                //停止刷新
                swipeRefreshLayout.setRefreshing(false);
                refresh();

            }
        });
        return view;
    }
}
