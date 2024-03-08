package cn.com.data_plus.bozhilian.global;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.util.TimeUtil;

public class Config {
    //SharedPreferences
    private static final String NAME = "save_state";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_IP = "ip";
    private static final String KEY_PORT = "port";
    private static final String KEY_ADDR = "addr";
    private static final String KEY_IS_SHOW_FRAG = "is_show_frag";
    private static final String KEY_SHOW_FRAG_ID = "show_frag_id";
    private SharedPreferences mPreferences;

    /**
     * 获取deviceID MD5编码
     */
    @SuppressLint("HardwareIds")
    private String getMyUUID(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return getMD5(uniqueId);
    }

    public String getDeviceID() {
        SharedPreferences preferences = App.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        String deviceID = preferences.getString(KEY_UUID, "");
        if (TextUtils.isEmpty(deviceID)) {
            deviceID = getMyUUID(App.app);
            preferences.edit().putString(KEY_UUID, deviceID).apply();
        }
        return deviceID;
    }

    private String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuilder strBuf = new StringBuilder();
            for (byte b : encryption) {
                if (Integer.toHexString(0xff & b).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & b));
                } else {
                    strBuf.append(Integer.toHexString(0xff & b));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public boolean saveSetting(String ip, int port, String addr) {
       return mPreferences.edit().putString(KEY_IP, ip).putInt(KEY_PORT, port).putString(KEY_ADDR, addr).commit();


    }

    public String getTurn() {
        return mPreferences.getString("Turn", "");
    }
    public String getOff() {
        return mPreferences.getString("Off", "");
    }
    public String getweekArray() {
        return mPreferences.getString("weekArray", "");
    }


    public void saveToTurnItOff(String Turn, String Off,String weekArray) {
        mPreferences.edit().putString("Turn", Turn).putString("Off", Off).putString("weekArray", weekArray).apply();
    }
    public String getIP() {
        return mPreferences.getString(KEY_IP, Const.DEFAULT_IP);
    }

    public int getPort() {
        return mPreferences.getInt(KEY_PORT, Const.DEFAULT_PORT);
    }

    public String getAddr() {
        return mPreferences.getString(KEY_ADDR, "");
    }

    public boolean isShowFrag() {
        return mPreferences.getBoolean(KEY_IS_SHOW_FRAG, false);
    }

    public int getShowFragId() {
        return mPreferences.getInt(KEY_SHOW_FRAG_ID, 0);
    }

    public void saveShowFragId(int id) {
        mPreferences.edit().putInt(KEY_SHOW_FRAG_ID, id).apply();
    }

    public void saveShowFrag(boolean isShow) {
        mPreferences.edit().putBoolean(KEY_IS_SHOW_FRAG, isShow).apply();
    }

    private Config() {
        mPreferences = App.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private static Config sConfig;

    public static Config getInstance() {
        if (sConfig == null) {
            sConfig = new Config();
        }
        return sConfig;
    }
}
