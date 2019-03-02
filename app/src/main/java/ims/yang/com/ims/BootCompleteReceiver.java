package ims.yang.com.ims;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import ims.yang.com.ims.util.MyToast;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MyToast.INSTANCE.toastLong(context,"Boot Complete");
    }
}
