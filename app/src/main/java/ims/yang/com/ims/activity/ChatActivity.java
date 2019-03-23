package ims.yang.com.ims.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import ims.yang.com.ims.R;

public class ChatActivity extends BaseActivity {
    //初始化功能
    private void init(){
        Intent intent = getIntent();
        final String FRIEND_ID = intent.getStringExtra("param1");
        final String FRIEND_NICKNAME = intent.getStringExtra("param2");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(FRIEND_NICKNAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        init();
    }

    public static void actionStart(Context context, String... dataArr) {
        Intent intent = new Intent(context, ChatActivity.class);
        if (0 != dataArr.length) {
            for (int i = 0; i < dataArr.length; i++) {
                intent.putExtra("param" + (i + 1), dataArr[i]);
            }
        }
        context.startActivity(intent);
    }
}
