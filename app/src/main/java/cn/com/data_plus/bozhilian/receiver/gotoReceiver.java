package cn.com.data_plus.bozhilian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class gotoReceiver extends BroadcastReceiver {
    public gotoReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("com.allwinner.poweroff");
        context.sendBroadcast(i);
    }
}
