package ims.yang.com.ims.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import ims.yang.com.ims.R;
import ims.yang.com.ims.util.MyToast;
import ims.yang.com.ims.util.ResourceUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_ui);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_backup);
        navView.setNavigationItemSelectedListener(this);
    }

    public static void actionStart(Context context, String... dataArr) {
        Intent intent = new Intent(context, MainActivity.class);
        if (0 != dataArr.length) {
            for (int i = 0; i < dataArr.length; i++) {
                intent.putExtra("param" + (i + 1), dataArr[i]);
            }
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.backup:
                MyToast.INSTANCE.toastShort(this,ResourceUtil.INSTANCE.getString(this,R.string.is_developing));
                break;
            case R.id.delete:
                MyToast.INSTANCE.toastShort(this,ResourceUtil.INSTANCE.getString(this,R.string.is_developing));
                break;
            case R.id.settings:
                MyToast.INSTANCE.toastShort(this,ResourceUtil.INSTANCE.getString(this,R.string.is_developing));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //TODO
        drawerLayout.closeDrawers();
        return false;
    }
}
