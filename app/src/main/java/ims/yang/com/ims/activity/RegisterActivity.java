package ims.yang.com.ims.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import ims.yang.com.ims.R;
import ims.yang.com.ims.util.MyToast;
import ims.yang.com.ims.util.ResourceUtil;

/**
 * 注册活动
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    Button btnLogin;
    ImageView imgHead;
    EditText edtPassword;
    EditText edtPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.register);
        setSupportActionBar(toolbar);
        init();
    }

    void init() {
        imgHead = findViewById(R.id.img_head);
        imgHead.setOnClickListener(this);

        edtPasswordConfirm = findViewById(R.id.edt_password_confirm);
        edtPasswordConfirm.setVisibility(View.VISIBLE);
        edtPassword = findViewById(R.id.edt_password);

        btnLogin = findViewById(R.id.btn_login_regist);
        btnLogin.setOnClickListener(this);
        btnLogin.setText(R.string.register);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("param1");
        Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();
    }

    public static void actionStart(Context context, String... dataArr) {
        Intent intent = new Intent(context, RegisterActivity.class);
        if (0 != dataArr.length) {
            for (int i = 0; i < dataArr.length; i++) {
                intent.putExtra("param" + (i + 1), dataArr[i]);
            }
        }
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_head:
                MyToast.INSTANCE.toastShort(this, ResourceUtil.INSTANCE.getString(this, R.string.is_developing));
                break;
            default:
                break;
        }
    }
}
