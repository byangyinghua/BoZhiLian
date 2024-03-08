package cn.com.data_plus.bozhilian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.NetworkUtil;

/**
 * Network monitoring
 */
public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtil.isNetworkConnected()) {
            LogUtil.info("网络恢复");
            AppMessager.send2Activity(Const.MSG_NOTICE_NETWORK_STATE, Const.STATE_NORMAL);
            App.restartServerMessager();
            AppMessager.send2Activity(Const.MSG_CLOSE_ACTIVITY, "");
        } else {
            LogUtil.error("网络断开");
            AppMessager.send2Activity(Const.MSG_NOTICE_NETWORK_STATE, Const.STATE_BREAK);
            AppMessager.send2Activity(Const.MSG_NOTICE_CONNECT_STATE, Const.STATE_BREAK);
            App.stopServerMessager();
        }
    }
}
