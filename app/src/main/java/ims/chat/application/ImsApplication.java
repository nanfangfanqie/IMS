package ims.chat.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import cn.jpush.im.android.api.JMessageClient;

/**
 * 自定义Application
 */
public class ImsApplication extends Application {
    public static ImsApplication imsApplication;
    private static   Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);
        context = getApplicationContext();

    }

    public static Context getContext(){
        return context;
    }
}
