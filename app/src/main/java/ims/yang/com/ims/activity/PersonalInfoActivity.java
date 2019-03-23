package ims.yang.com.ims.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import ims.yang.com.ims.R;

public class PersonalInfoActivity extends BaseActivity {
    private TextView textView;
    private Button sendButton;

    /**
     * 初始化
     */
    private void init(){
        Intent intent= getIntent();
        final String FRIEND_ID = intent.getStringExtra("param1");
        final String FRIEND_NICK_NICKNAME = intent.getStringExtra("param2");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(FRIEND_NICK_NICKNAME + "资料");
        sendButton = findViewById(R.id.btn_send_msg);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.actionStart(PersonalInfoActivity.this,FRIEND_ID,FRIEND_NICK_NICKNAME);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_activity);
        init();
    }

    public static void actionStart(Context context, String... dataArr) {
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        if (0 != dataArr.length) {
            for (int i = 0; i < dataArr.length; i++) {
                intent.putExtra("param" + (i + 1), dataArr[i]);
            }
        }
        context.startActivity(intent);
    }
}
