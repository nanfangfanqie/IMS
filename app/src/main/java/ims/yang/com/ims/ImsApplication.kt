package ims.yang.com.ims

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class ImsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        mApplication = this
        context = applicationContext
    }

    companion object {
        lateinit var mApplication: Application
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }

}
