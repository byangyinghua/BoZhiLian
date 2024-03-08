package cn.com.data_plus.bozhilian.util;

import android.content.Intent;

import cn.com.data_plus.bozhilian.App;

public class OtherUtil {

    public static void fireShutDown() {
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.app.startActivity(intent);
    }

    //重启，发送REBOOT广告，典型用法如下：
    public static void reboot() {
        Intent intent = new Intent("android.intent.action.REBOOT");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.app.startActivity(intent);
    }
}
