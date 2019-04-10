package ims.yang.com.ims

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import ims.yang.com.ims.util.MyToast

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        MyToast.toastLong(context, "Boot Complete")
    }
}
