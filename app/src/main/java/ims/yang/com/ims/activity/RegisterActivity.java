package ims.yang.com.ims.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import ims.yang.com.ims.R;

/**
 * 注册活动
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    void init(){
        Intent intent = getIntent();
        String userName = intent.getStringExtra("param1");
        Toast.makeText(this,userName,Toast.LENGTH_SHORT).show();
    }

    public static void actionStart(Context context, String... dataArr) {
        Intent intent = new Intent(context, RegisterActivity.class);
        if (0 != dataArr.length) {
            for (int i = 0; i < dataArr.length; i++) {
                intent.putExtra("param" + (i+1), dataArr[i]);
            }
        }
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //TODO
        }
    }
}
