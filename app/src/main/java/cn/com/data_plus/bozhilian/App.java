package cn.com.data_plus.bozhilian;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.ly723.db.helper.DbSQLite;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.global.SDCardSQLiteHelper;
import cn.com.data_plus.bozhilian.database.newTaskDao;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Config;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.thread.ServerMessager;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.NetworkUtil;
import cn.com.data_plus.bozhilian.util.TimeUtil;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class App extends Application implements Thread.UncaughtExceptionHandler {
    // implements Thread.UncaughtExceptionHandler
    private static final String TAG = "TAG";
    public static App app;
    public static Context context;
    public static String TaskName = "空闲";
    public static String deviceID;
    public static String serverAddr;//ip:port
    public static String date;//current date
    public static String addr;//current date

    public static int colorDebug;
    public static int colorInfo;
    public static int colorError;

    public static String downloadIP;
    public static int downloadPort;
    private static AudioManager mAudioManager;
    private static AudioManager audio;
    private static Handler sHandler;
    private static Gson sGson;
    private static ServerMessager sServerMessager;
    private static newTaskDao snewTaskDao;
    public static List<newTask> newTasks = new ArrayList<newTask>();
    public static List<newTask> Ytasks = new ArrayList<newTask>();

    public static newTask newTaskDownloader;

    public static void bindMsgHandler(Handler handler) {
        sHandler = handler;
    }

    public static Handler msgHandler() {
        return sHandler;
    }

    public static Gson gson() {
        return sGson;
    }

    public static ServerMessager serverMessager() {
        return sServerMessager;
    }


    public static newTaskDao newtaskDao() {
        return snewTaskDao;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        sGson = new Gson();
        sServerMessager = new ServerMessager();
        context = getApplicationContext();
        deviceID = Config.getInstance().getDeviceID();
        serverAddr = NetworkUtil.getPhoneIp() + ":" + Const.SERVER_POST;
        date = TimeUtil.getDate();
        addr = Config.getInstance().getAddr();

        colorDebug = gainColor(R.color.log_debug);
        colorInfo = gainColor(R.color.log_info);
        colorError = gainColor(R.color.log_error);

        SDCardSQLiteHelper SDCardSQLiteHelper = new SDCardSQLiteHelper(FileUtil.getDirFile("db").getAbsolutePath());
        DbSQLite dbSQLite = new DbSQLite(this, SDCardSQLiteHelper.getWritableDatabase());

        snewTaskDao = new newTaskDao(dbSQLite);
        snewTaskDao.createTable();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

      //  Thread.setDefaultUncaughtExceptionHandler(this);

    }

    public static void getinit() {
        deviceID = Config.getInstance().getDeviceID();
        serverAddr = NetworkUtil.getPhoneIp() + ":" + Const.SERVER_POST;
        date = TimeUtil.getDate();
        addr = Config.getInstance().getAddr();
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }


    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    //
//    //Handle crash

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LogUtil.debug("出了个未知的状况~~~~" + e.toString());

        e.printStackTrace();
        // LogUtil.error(e);
        if (!(e instanceof StackOverflowError)) {
            FileUtil.write2File(
                    FileUtil.SUB_DIR_LOGS,
                    "crash_".concat(TimeUtil.getTime()).concat(".txt"),
                    Log.getStackTraceString(e),
                    false);
        }
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       // mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
     //   System.exit(0);
    }

    public static void startServerMessager() {
        sServerMessager.start();
    }


    public static int getCurrent() {
        int max1 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current1 = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return current1;
    }


    public static void restartServerMessager() {

        sServerMessager.stop();
        TimeUtil.delayExe(2000);
        sServerMessager.start();
    }


    public static void UpCurrent() {
        audio.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
        HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, getCurrent() + "");
        AppMessager.send2Server(Const.RECV_setVolume, App.gson().toJson(heartBeat));
    }

    public static void DownCurrent() {
        audio.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
        HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, getCurrent() + "");
        AppMessager.send2Server(Const.RECV_setVolume, App.gson().toJson(heartBeat));
    }

    public static void stopServerMessager() {
        sServerMessager.stop();
    }

    public static String gainString(int resID) {
        return app.getResources().getString(resID);
    }

    public static int gainColor(int resID) {
        return ContextCompat.getColor(app, resID);
    }

    public static void resetApp() {

    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }
}
