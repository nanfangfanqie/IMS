package ims.yang.com.ims;

import android.app.Application;

public class ImsApplication extends Application {
    public static Application	mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
    }
}
