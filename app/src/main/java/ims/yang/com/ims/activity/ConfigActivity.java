package ims.yang.com.ims.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import ims.yang.com.ims.R;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);
        EditText editIP = findViewById(R.id.edt_ip);
        EditText editPort = findViewById(R.id.edt_port);
        Button btnSubmit = findViewById(R.id.btn_submit);
    }
}
