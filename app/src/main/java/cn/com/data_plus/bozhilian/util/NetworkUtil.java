package cn.com.data_plus.bozhilian.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import cn.com.data_plus.bozhilian.App;

/**
 * IP和网络判断
 */
public class NetworkUtil {
    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.debug(e);
        }
        return "";
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) App.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            @SuppressWarnings("deprecation")
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo info : infos) {
                NetworkInfo.State state = info.getState();
                if (NetworkInfo.State.CONNECTED == state) {
                    return true;
                }
            }
        }
        return false;
    }
}