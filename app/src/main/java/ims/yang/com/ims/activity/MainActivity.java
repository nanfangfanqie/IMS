package ims.yang.com.ims.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import ims.yang.com.ims.R;
import ims.yang.com.ims.adapter.ViewPagerAdapter;
import ims.yang.com.ims.fragment.BaseFragment;
import ims.yang.com.ims.util.MyToast;
import ims.yang.com.ims.util.ResourceUtil;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;

    void init() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_ui);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();

        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_backup);
        navView.setNavigationItemSelectedListener(this);
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.message:
                        viewPager.setCurrentItem(0);
                        toolbar.setTitle("消息");
                        break;
                    case R.id.contacts:
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle("联系人");
                        break;
                    case R.id.moments:
                        viewPager.setCurrentItem(2);
                        toolbar.setTitle("动态");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        toolbar.setTitle("消息");
                        break;
                    case 1:
                        toolbar.setTitle("联系人");
                        break;
                    case 2:
                        toolbar.setTitle("动态");
                    default:
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        setupViewPager(viewPager);
    }

    void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(BaseFragment.newInstance("消息"));
        adapter.addFragment(BaseFragment.newInstance("联系人"));
        adapter.addFragment(BaseFragment.newInstance("动态"));
        viewPager.setAdapter(adapter);
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
        setContentView(R.layout.main_activity);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.settings:
                MyToast.INSTANCE.toastShort(this, ResourceUtil.INSTANCE.getString(this, R.string.is_developing));
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
