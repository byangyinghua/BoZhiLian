package cn.com.data_plus.bozhilian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutdownReceiver extends BroadcastReceiver {
    public ShutdownReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("com.android.settings.action.REQUEST_POWER_ON");
        context.sendBroadcast(i);


    }
}
