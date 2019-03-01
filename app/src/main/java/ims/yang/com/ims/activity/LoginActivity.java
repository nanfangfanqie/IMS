package ims.yang.com.ims.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ims.yang.com.ims.R;
import ims.yang.com.ims.util.MyToast;
import ims.yang.com.ims.util.ResourceUtil;

/**
 * 登录活动
 *
 * @author yangchen
 * on 2019/2/28 23:18
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    EditText edtUserName;
    EditText edtPassword;
    Button btnLogin;
    TextView txtRegister;
    TextView txtForgot;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.login);
        setSupportActionBar(toolbar);
        init();
    }

    /**
     * 初始化
     */
    void init() {
        edtUserName = findViewById(R.id.edt_userName);
        edtUserName.addTextChangedListener(this);
        edtPassword = findViewById(R.id.edt_password);
        edtPassword.addTextChangedListener(this);

        txtRegister = findViewById(R.id.txt_register);
        txtRegister.setOnClickListener(this);
        txtForgot = findViewById(R.id.txt_forgot);
        txtForgot.setOnClickListener(this);

        btnLogin = findViewById(R.id.btn_login_regist);
        btnLogin.setOnClickListener(this);
        btnLogin.setText(R.string.login);


    }

    @Override
    public void onClick(View v) {
        String userName = edtUserName.getText().toString();
        switch (v.getId()) {
            case R.id.txt_register:
                if (!"".equals(userName.trim())) {
                    MyToast.INSTANCE.toastShort(this, userName);
                    RegisterActivity.actionStart(this, userName);
                } else {
                    RegisterActivity.actionStart(this);
                }
                break;
            case R.id.btn_login_regist:
                //启动主活动
                MainActivity.actionStart(this, userName);
                break;
            case R.id.txt_forgot:
                MyToast.INSTANCE.toastShort(this, ResourceUtil.INSTANCE.getString(this, R.string.is_developing));
                break;
            default:
                break;
        }
    }


    public static void actionStart(Context context, String... dataArr) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (0 != dataArr.length) {
            for (int i = 0; i < dataArr.length; i++) {
                intent.putExtra("param" + (i + 1), dataArr[i]);
            }
        }
        context.startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //点击禁用按钮
        btnLogin.setClickable(true);
        btnLogin.setBackgroundColor(ResourceUtil.INSTANCE.getColor(this, R.color.gray));
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(edtUserName.getText()) || TextUtils.isEmpty(edtPassword.getText())) {
            btnLogin.setClickable(false);
            btnLogin.setBackgroundColor(ResourceUtil.INSTANCE.getColor(this, R.color.gray));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!(TextUtils.isEmpty(edtUserName.getText()) || TextUtils.isEmpty(edtPassword.getText()))) {
            btnLogin.setClickable(true);
            btnLogin.setBackgroundColor(ResourceUtil.INSTANCE.getColor(this, R.color.colorPrimary));
        }
    }
}
