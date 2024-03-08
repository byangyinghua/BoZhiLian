package cn.com.data_plus.bozhilian.ui;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uniwin.Gpio;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPort;
import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.bean.MedioBean;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Config;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.global.Player;
import cn.com.data_plus.bozhilian.global.newAlarm;
import cn.com.data_plus.bozhilian.global.newAudio;
import cn.com.data_plus.bozhilian.interfaces.StateListener;
import cn.com.data_plus.bozhilian.live.BackgroudService1;
import cn.com.data_plus.bozhilian.port.SelectPort;
import cn.com.data_plus.bozhilian.test.TaskTest;
import cn.com.data_plus.bozhilian.thread.Broadcast;
import cn.com.data_plus.bozhilian.thread.DownloadAsyncApp;
import cn.com.data_plus.bozhilian.thread.ReadLogAsync;
import cn.com.data_plus.bozhilian.thread.newDownloader;
import usbdatacopy.util.CopyUsbFileUtil;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.MemoryUtils;
import cn.com.data_plus.bozhilian.util.NetworkUtil;
import cn.com.data_plus.bozhilian.util.StringUtil;
import cn.com.data_plus.bozhilian.util.UtilAudio;
import cn.com.data_plus.bozhilian.util.window.CouponListWindow;
import cn.com.data_plus.bozhilian.widget.StateImageView;
import cn.com.data_plus.bozhilian.widget.TSnackbar;

//192.168.101.254
@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity implements SelectPort {
    private static final String TAG = "TAG";
    public Intent intent_bg;
    //handle order
    private Handler mHandler = new Handler(new HandlerCallback());


    @Override
    public void select(MedioBean item) {

    }

    private class HandlerCallback implements Handler.Callback {

        //102文本任务
        @Override
        public boolean handleMessage(Message msg) {
            int order = msg.what;
            String content = "";
            if (order != -900) {
                content = (String) msg.obj;
            }
            switch (order) {
                case Const.RECV_CONNECT:
                    if (content.equals("")) {
                        LogUtil.info("ip端口配置错误，或者服务器出错，请检查！");
                        showConnectStateforBug();
                        break;
                    }
                    try {
                        AppMessager.sendTerminalInfo();
                        startHeartBeat();
                    } catch (Exception e) {
                        LogUtil.info("连接服务器错误，请重新连接！");
                        showConnectStateforBug();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                        LogUtil.error(df.format(new Date()), e);
                        break;
                    }
                    break;
                case Const.RECV_TASK://添加新的任务队列
//                    try {
                    String[] sb = content.split("L&Q");

                    final newTask task = new newTask(sb[0]);//niu 设置json数据 解析
                    task.setNetUsbType(picFragment.NET_DATA);
                    Log.e("TAG", "!!!!!!!!!！---接收到任务---" + task.toString());
                    Log.e("TAG", "!!!!!!!!!！---接收到任务---" + content.toString());

                    App.newtaskDao().insertOrReplace(task);//插入解析好的json数据
                    // Log.e("TAG",task.getTaskType());
                    if (Integer.parseInt(task.getTaskType()) == 2) {//文本任务
                        newAlarm.getInstance().setTaskAlarm(task);
                    } else {//视频任务和图片任务 需要下载
                        newDownloader.getInstance().download(task);
                    }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        LogUtil.info(" 任务信息出错，正在重试一次！" + e.toString());
//                        try {
//                            newTask task = new newTask(content);
//                            App.newtaskDao().insertOrReplace(task);
//                            newDownloader.getInstance().download(task);
//                        } catch (Exception es) {
//                            es.printStackTrace();
//                            LogUtil.info(" 任务信息出错，已停止执行，请检查！" + es.toString());
//                        }
//
//                    }

                    break;
                case Const.RECV_DELETE_TASK:
//                    try {
                    String[] st = content.split("L&Q");
                    // Log.e("TAG", "!!!!!!!!!！---接收到任务---" + st[0].toString());
                    newTask taskDelete = new newTask(st[0]);
                    App.newtaskDao().delete(taskDelete);
                    newAlarm.getInstance().cancelTaskAlarm(taskDelete);
                    newAlarm.getInstance().stopTasks(taskDelete, 1);
                    List<newTask> s = new ArrayList<newTask>();
                    if (App.newTasks.size() == 1) {
                        App.newTasks = new ArrayList<newTask>();
                    }
                    if (App.newTasks != null) {
                        if (s != null) {
                            for (newTask n : s) {
                                App.newTasks.remove(n);
                            }
                        }
                        if (App.newTasks.size() > 0) {
                            newAlarm.getInstance().startTasks(App.newTasks.get(0));
                        }
                    }

                    String[] szId = taskDelete.getTaskFileID().split(Const.SPLIT);
                    String[] szName = taskDelete.getTaskFileName().split(Const.SPLIT);
                    for (int i = 0; i < szId.length; i++) {
                        boolean bool = FileUtil.deleteTaskFileByFileName(szName[i]);
                        if (bool) {
                            LogUtil.info("删除成功！");
                        } else {
                            LogUtil.info("删除失败！");
                        }
                    }
//
//
//                    } catch (Exception e) {
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//                        LogUtil.error(df.format(new Date()), e);
//                        LogUtil.info("任务已停止！");
//                    }
                    break;
                case Const.RECV_SET_TIME:

                    Log.i("content", content);
                    //设置系统时间。测试版本不能设置，需要权限
                    String[] time = content.split("L&Q");
                    //      setDate(time[0]);
                    try {
                        Process process = Runtime.getRuntime().exec("su");
                        DataOutputStream os = new DataOutputStream(process.getOutputStream());
                        os.writeBytes("/system/bin/date -s " + time[0] + "\n");
                        os.writeBytes("clock -w\n");
                        os.writeBytes("exit\n");
                        os.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case Const.RECV_VIDEO_STREAM://接收到视频回应
                    try {
                        LogUtil.debug("content:" + content);
                        String[] arr = content.split("L&Q");
                        JSONObject myJsonObject = new JSONObject(arr[0]);
                        String address = myJsonObject.optString("address");
                        if (content.isEmpty()) {
                            stopService(intent_bg);
                        } else {
                            if (!address.equals("")) {

                                //intent_bg.putExtra("rtmpUrl", "rtmp://player.daniulive.com:1935/hls/stream333222111");
                                intent_bg.putExtra("rtmpUrl", address);
                                startService(intent_bg);
                            } else {
                                stopService(intent_bg);
                                LogUtil.info("已停止！");
                            }
                        }
                    } catch (JSONException e) {
                        stopService(intent_bg);
                        LogUtil.info("地址错误，推送失败！");
                    }

                    break;
                case Const.RECV_BROADCAST:
                    if (TextUtils.isEmpty(content)) {
                        if (mBroadcast != null) {
                            mBroadcast.stop();
                        }
                    } else {
                        String[] strs = content.split(":");
                        mBroadcast = new Broadcast(strs[0], Integer.parseInt(strs[1]));
                        mBroadcast.start();
                    }
                    break;
                case Const.MSG_CONNECT_FAILED_BY_REMOVE_FROM_SERVER:
                    //LogUtil.info("服务器断开连接,开始尝试重新链接！");
                    App.restartServerMessager();
                    break;
                case Const.MSG_TOAST:
                    showToast(content);
                    break;
                case Const.MSG_PLAY_VIDEO:

                    try {
                        showFragment(VideoFragment.newInstance(content));
                        // Log.e("TAG", "!!!!!!!!!！---MSG_PLAY_VIDEO---" + content);
                        JSONObject js = new JSONObject(content.toString());
                        String c = js.optString("TaskContent");

                        if (!c.equals("")) {
                            showBottomText(c);
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }


                    break;

                case Const.MSG_PLAY_PIC:
                    try {
                        showFragment(picFragment.newInstance(content));

                        // Log.e("TAG", "!!!!!!!!!！---MSG_PLAY_PIC---" + content);
                        JSONObject js = new JSONObject(content.toString());
                        String c = js.optString("TaskContent");
                        if (!c.equals("")) {
                            showBottomText(c);
                        }
                    } catch (JSONException e) {
                        Log.e("Exception", e.toString());
                    }
                    break;
                case Const.MSG_CLOSE_FRAGMENT:
                    closeFragment();
                    hideBottomText();
                    break;
                case Const.MSG_NOTICE_NETWORK_STATE:
                    showNetworkState(content);
                    break;
                case Const.MSG_NOTICE_CONNECT_STATE:
                    showConnectState(content);
                    break;
                case Const.MSG_SHOW_TEXT:
                    showFragment(TextFragment.newInstance(content));
                    break;
                case Const.MSG_SHOW_TOP_TEXT:
                    mTextView.setText(content);
                    break;
                case Const.MSG_SHOW_LARGE_TEXT://文本数据

                    UtilAudio.getInstance().startPlayMusic(MainActivity.this);//这里content中添加了区分net和usb的数据内容
                    showFragment(TextFragment.newInstanceLarge(content));

                    break;
                case Const.MSG_RESET_PROGRESS://任务进度显示
                    setProgressBarText(content);
                    break;
                case Const.MSG_SHOW_PROGRESS:
                    showProgress(content);
                    break;
                case Const.MSG_CLOSE_ACTIVITY:
                    App.restartServerMessager();
                    break;
                case Const.MSG_SHOW_BOTTOM_TEXT:
                    showBottomText(content);
                    break;
                case Const.MSG_HIDE_BOTTOM_TEXT:
                    hideBottomText();
                    break;
                case Const.RECV_ShieldSignal://屏蔽开关

                    String[] acc = content.split("L&Q");
                    JSONObject myJsonObject1 = null;
                    try {
                        myJsonObject1 = new JSONObject(acc[0]);
                        content = myJsonObject1.optString("state");

                        if (content.equals("open")) {
                            writeGpio(true);
                        } else {
                            writeGpio(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case Const.RECV_AlarmVideo://告警开关


                    String[] abb = content.split("L&Q");
                    JSONObject myJsonObject = null;
                    try {
                        myJsonObject = new JSONObject(abb[0]);
                        content = myJsonObject.optString("state");
                        String Messages = myJsonObject.optString("taskinfo");
                        if (content.equals("open")) {
                            newTask nt = new newTask(TaskTest.al(Messages));
                            newAlarm.getInstance().startTask(nt);
                        } else {
                            newTask nt = new newTask(TaskTest.al(Messages));
                            newAlarm.getInstance().stopTask(nt, 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                case Const.RECV_DELETE_FILE://删除任务
                    String json = "{" + content + "}";
                    LogUtil.debug(json);

                    JsonParser parser = new JsonParser();  //创建JSON解析器
                    JsonObject object = (JsonObject) parser.parse(json);  //创建JsonObject对象
                    String fileName = object.get("fileName").getAsString();

                    boolean bool = FileUtil.deleteTaskFileByFileName(fileName);
                    if (bool) {
                        LogUtil.info("服务器执行删除文件，本地删除成功！");
                    } else {
                        LogUtil.info("服务器执行删除文件，本地删除失败！");
                    }

                    break;


                case Const.RECV_down_Music://-音量
                    App.DownCurrent();
                    break;
                case Const.RECV_up_Music://+音量
                    App.UpCurrent();
                    break;
                case Const.RECV_GetVolume://获得音量
                    HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, App.getCurrent() + "");
                    AppMessager.send2Server(Const.SEND_SetVolume, App.gson().toJson(heartBeat));
                    break;

                case Const.RECV_PlayMusic://收到实时广播
                    String[] abb1 = content.split("L&Q");
                    JSONObject myJsonObject2 = null;
                    try {
                        myJsonObject2 = new JSONObject(abb1[0]);
                        content = myJsonObject2.optString("flag");

                        if (content.equals("play")) {
                            Player.getInstance().ins();
                            String url = myJsonObject2.optString("url");
                            String urlid = myJsonObject2.optString("orderId");
                            Player.getInstance().playUrl(url, urlid);
                        } else if (content.equals("stop")) {
                            Player.getInstance().stop();
                        } else if (content.equals("suspend")) {
                            Player.getInstance().pause();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Const.RECV_BOOT://开关机
                    String[] rcve_boot = content.split("L&Q");
                    JSONObject rcve_bootJsonObject2 = null;
                    try {
                        rcve_bootJsonObject2 = new JSONObject(rcve_boot[0]);
                        String timeon = rcve_bootJsonObject2.optString("timeon");
                        String timeoff = rcve_bootJsonObject2.optString("timeoff");
                        String weekArray = rcve_bootJsonObject2.optString("weekArray");
                        String[] sins = weekArray.split(",");

                        int[] ins = new int[sins.length];

                        for (int i = 0; i < sins.length; i++) {
                            ins[i] = Integer.parseInt(sins[i]);
                        }

                        boolean enable = rcve_bootJsonObject2.optBoolean("enable");
                        //LogUtil.info("开机时间:" + timeon + "------关机时间：" + timeoff + "------执行日期:" + weekArray + "------闹钟状态：" + enable);
                        if (Config.getInstance().getTurn().equals("")) {

                        } else {
                            intsent.putExtra("delete_all", true);
                        }

                        Config.getInstance().saveToTurnItOff(timeon, timeoff, weekArray);
                        ToTurnItOff(timeon, timeoff, ins, enable);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case Const.MSG_resetApp://重启

                    if (intent_bg != null)
                        stopService(intent_bg);
                    App.serverMessager().stop();
                    Intent intent = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
                    System.exit(0);
                    break;
                case Const.RECV_BOOT_DELETE:

                    intsent.putExtra("delete_all", true);  //关闭并删除所有外部设置的定时开关机 13885775018
                    sendBroadcast(intsent);

                    break;
                case Const.RECV_updateApp://更新

                    String[] contents = content.split("L&Q");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(contents[0]);
                        String version = jsonObject.optString("version");
                        final String path = jsonObject.optString("path");
                        DownloadAsyncApp downloadAsyncApp = new DownloadAsyncApp(path, new StateListener() {
                            @Override
                            public void onStart() {
                                System.out.println("onStart");
                            }

                            @Override
                            public void onSuccess() {
                                System.out.println("onSuccess");
                                String fileName = path.substring(path.lastIndexOf("/"), path.length());
                                File dir = new File(FileUtil.getDirFile("app").getAbsolutePath().concat(File.separator) + fileName);
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setDataAndType(Uri.fromFile(dir), "application/vnd.android.package-archive");
                                myContext.startActivity(intent);
                            }

                            @Override
                            public void onFailed() {
                                System.out.println("onFailed");
                            }
                        });
                        downloadAsyncApp.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;

                case Const.hideBottomUIMenu://隐藏虚拟按键

                    hideBottomUIMenu();

                    break;
                case Const.RECV_deleteAllTask:


                    if (App.newTasks != null) {
                        for (newTask n : App.newTasks) {
                            newAlarm.getInstance().stopTaskAll(n);
                            App.newTasks.remove(n);
                        }
                    }
                    break;

                case Const.RECV_deleteAllTaskInDatebase:
//                    try {
                    App.newtaskDao().deleteAllInnewTask();
                    AppMessager.send2Activity(Const.MSG_resetApp, "");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        FileUtil.write2File(
//                                FileUtil.SUB_DIR_LOGS,
//                                "crash-".concat(TimeUtil.getTime()).concat(".txt"),
//                                Log.getStackTraceString(e),
//                                false);
//                    }
//

                    break;
                //niu获取视频播放地址
                case Const.RECV_PlayVideoPashUrl:
                    try {
                        Log.e("TAG", "!!!!!!!!!！---MSG_PLAY_PIC---" + content);
                        JSONObject js = new JSONObject(content.toString());
                        final String address = js.optString("address");
                        showFragment(playVideoFragment.newInstance(address));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Exception", e.toString());
                    }
                    break;
                case Const.RECV_STOPPlayVideoPashUrl:
                    closeFragment();
                    break;
                case Const.MSG_STOP_SERVER:
                    Log.e("TAG", "停止推流服务");
                    if (intent_bg != null)
                        stopService(intent_bg);
                    break;
                //niu音频播放
                case Const.MSG_LOOPTASKList:
                    if (App.newTasks.size() != 0) {
                        int fileTypes = Integer.parseInt(App.newTasks.get(0).getTaskType());
                        if (fileTypes == 0) {//audio
                            newAudio.getInstance().startPlayMusic(App.newTasks.get(0));
                        } else if (fileTypes == 1) {//video
                            AppMessager.send2Activity(Const.MSG_PLAY_VIDEO, App.gson().toJson(App.newTasks.get(0)));
                        } else if (fileTypes == 3) {//video
                            AppMessager.send2Activity(Const.MSG_PLAY_PIC, App.gson().toJson(App.newTasks.get(0)));
                            // PlayPic.getInstance().PlayPics(task);
                        } else {//text
                            if (Config.getInstance().isShowFrag()) {
                                AppMessager.send2Activity(Const.MSG_SHOW_BOTTOM_TEXT, App.newTasks.get(0).getTaskContent());
                            } else {
                                Config.getInstance().saveShowFragId(Integer.parseInt(App.newTasks.get(0).getTaskID()));
                                AppMessager.send2Activity(Const.MSG_SHOW_LARGE_TEXT, App.newTasks.get(0).getTaskContent());
                            }
                        }
                    }
                    break;
                //niu 2019年4月25日15:28:38
                case -888://获取到usb中txt文件数据
                    String str = "拷贝完成";
                    AppMessager.resetProgress(str);
                    AppMessager.resetProgress(" ");
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            String pathPatent = App.context.getApplicationContext().getFilesDir().getAbsolutePath() + "/"
                                    + CopyUsbFileUtil.U_DISK_FILE_NAME_PREANT;
                            String pathUrl = pathPatent + CopyUsbFileUtil.U_DISK_FILE_NAME;//得到txt文件路径;
                            //读取txt文件内容
                            String str = "";
                            InputStream is = null;
                            StringBuilder sbstr = new StringBuilder();
                            BufferedReader bufferedReader = null;
                            try {
                                is = new FileInputStream(pathUrl);
                                //读取秘钥中的数据进行匹配
                                bufferedReader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                                String read;
                                List<String> configTask = new ArrayList<>();//把一个任务存入集合
                                while ((read = bufferedReader.readLine()) != null) {
                                    sbstr.append(read);
                                    configTask.add(read);
                                }
                                str = new String(sbstr.toString().getBytes("UTF-8"), "UTF-8");
                                Message msgs = mHandler.obtainMessage();
                                msgs.what = -903;
                                msgs.obj = str;
                                mHandler.sendMessage(msgs);
                                newTask txtTask = new newTask();
                                String fn = "";//文件名称
                                String urls = "";//文件路径
                                for (int j = 0; j < configTask.size(); j++) {
                                    if (configTask.get(j).contains("@")) {
                                        String s = configTask.get(j).split("@")[1];//拆分字符串
                                        String type = configTask.get(j).split("@")[0];//拆分字符串
                                        switch (type) {
                                            case "taskid":
                                                txtTask.setTaskID(s);
                                                break;
                                            case "taskname":
                                                txtTask.setTaskName(s);
                                                break;
                                            case "begday":
                                                txtTask.setTaskDate(s);
                                                break;
                                            case "begantime":
                                                txtTask.setTaskTime(s);
                                                break;
                                            case "endday":
                                                txtTask.setEndTaskDate(s);
                                                break;
                                            case "endtime":
                                                txtTask.setEndTaskTime(s);
                                                break;
                                            case "beganweek":
                                                String[] day = s.split("");
                                                String d = "";
                                                for (int i = 1; i < day.length; i++) {
                                                    d = d + day[i] + ",";
                                                }
                                                txtTask.setDay(d);
                                                break;
                                            case "count":
                                                txtTask.setTaskPlaynumber(s);
                                                break;
                                            case "taskcontent":
                                                txtTask.setTaskContent(s);
                                                break;
                                            case "tgrade":
                                                txtTask.setTaskLevel(s);
                                                break;
                                            case "type":
                                                txtTask.setTaskType(s);
                                                break;
                                            case "interarrivaltime":
                                                txtTask.setFilePlayTime(s);
                                                break;
                                            case "filecount":

                                                break;
                                            case "file"://这里暂时不设置文件id
                                                String[] nam = s.split(":");
                                                for (int i = 0; i < nam.length; i++) {
                                                    fn = nam[i] + Const.SPLIT + fn;
                                                    String urlfile = pathPatent + "/" + nam[i];
                                                    File file = new File(urlfile);
                                                    if (file.exists())//判断文件是否存在
                                                        urls = urlfile + Const.SPLIT + urls;//图片地址拼接
                                                }
                                                txtTask.setTaskFileName(fn);
                                                txtTask.setUrl(urls);
                                                txtTask.setNetUsbType(picFragment.USB_DATA);//usb数据
                                                break;
                                        }
                                    }
                                }
                                txtTask.setAlarClock("1");
                                txtTask.setTaskPlaySate("0");
                                txtTask.setTaskID("999");
                                txtTask.setTaskName("22-1");//不知道是不是这些新

                                if (txtTask.getTaskType().equals("2"))//文本展示时需要复制txt文件数据到setTaskContent中
                                { //读取txt文件内容
                                    String content = "";
                                    InputStream ist = null;
                                    StringBuilder sbstrt = new StringBuilder();
                                    BufferedReader bufferedReadert = null;
                                    try {
                                        ist = new FileInputStream(txtTask.getUrl().replace(Const.SPLIT, ""));
                                        //读取秘钥中的数据进行匹配
                                        bufferedReadert = new BufferedReader(new InputStreamReader(ist));
                                        String readt;
                                        while ((readt = bufferedReadert.readLine()) != null) {
                                            sbstrt.append(readt);
                                        }
                                        content = new String(sbstrt.toString().getBytes("GBK"), "GBK");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            if (bufferedReader != null) {
                                                bufferedReader.close();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    txtTask.setTaskContent(content);
                                }

                                if (App.newTasks != null) {//注意这里多任务需要注意重新处理
                                    for (newTask n : App.newTasks) {
                                        if (n.getTaskID().equals("999")) {//U盘任务id
                                            newAlarm.getInstance().stopTask(n, 1);//删除之前任务
                                        }
                                    }
                                }
                                App.newtaskDao().insertOrReplace(txtTask);//插入解析好的json数据
                                newAlarm.getInstance().setTaskAlarm(txtTask);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (bufferedReader != null) {
                                        bufferedReader.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();

                 /*   DialogUtils.showTwoKeyDialog(MainActivity.mContext, new CommonDialog.ClickCallBack() {
                        @Override
                        public void onConfirm() {
                            cufu.copyFile();
                        }
                    }, "检查到文件是否需要复制文件", "取消", "确定");*/
                    break;
                case -889://给视频音频提示 根据提示播放
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String path = App.context.getFilesDir().getAbsolutePath() + CopyUsbFileUtil.URL_PRANTE;
                            List<MedioBean> made = new ArrayList<>();
                            cufu.traverseFolder2(path, made);
                            if (!made.isEmpty()) {//给出选择框
                                CouponListWindow window = new CouponListWindow(MainActivity.mContext, mHandler);
                                window.setData(made);
                                window.showBottomWindow();
                            }
                        }
                    });
                 /*   DialogUtils.showTwoKeyDialog(MainActivity.mContext, new CommonDialog.ClickCallBack() {
                        @Override
                        public void onConfirm() {
                            showFragment(playVideoFragment.newInstance(cufu.getVoidUrl().get(0)));
                        }
                    }, "是否播放视频", "取消", "确定");*/
                    break;
                case -900:
                    List<MedioBean> md = (List<MedioBean>) msg.obj;
                    if (!md.isEmpty()) {
                        for (int j = 0; j < md.size(); j++) {
                            if (md.get(j).isSelect()) {
                                if (md.get(j).getType().equals("mp3")) {//播放音频
                                    MediaPlayer mediaPlayer = new MediaPlayer();
                                    try {
                                        mediaPlayer.setDataSource(md.get(j).getUrl());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        mediaPlayer.prepare();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    mediaPlayer.start();
                                } else if (md.get(j).getType().equals("mkv")) {//播放视频
                                    showFragment(playVideoFragment.newInstance(md.get(j).getUrl()));
                                }
                            }
                        }
                    }
                    break;
            }
            return true;
        }

    }

    private Timer mTimer;
    private Timer mTimer1;
    private Timer mTimer2;
    private TimerTask mTimerTask;
    private TimerTask mTimerTask1;
    private TimerTask mTimerTask2;
    private RelativeLayout mRootView;
    private TextClock mTextClock;
    private StateImageView mImageView;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private TextView mTvProgress;
    private TextView mTvBottom;
    private boolean showLog;
    private Context myContext;
    Intent intsent;
    boolean aBoolean = true;
    protected SerialPort mSerialPort;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    private TextView text;
    private String prot = "ttyS2";
    private int baudrate = 9600;
    private static int i = 0;
    private StringBuilder sb;

    private Thread receiveThread;

    byte[] b = new byte[48];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册usb广播
        registerUDiskReceiver();
        setContentView(R.layout.activity1);
        //禁止屏幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Gpio.init(MainActivity.this);
        mContext = this;
        myContext = this.getApplicationContext();
        Gpio.writeGpio('e', 2, 0);
        initView();
        initSetting();
        isIpNull();
        GetErrorButton();
        hideBottomUIMenu();
        Toast.makeText(this, "" + App.getHostIP(), Toast.LENGTH_LONG).show();
        intsent = new Intent("android.allwinner.intent.action.setpoweronoff");
        gettime();


        byte[] bOutArray = new byte[26];
        String[] s = "0x00,0x00,0x60,0x00,0x18,0x80,0x00,0x18,0x00,0x30,0x00,0x60,0x00,0x06,0x80,0x18,0x80,0x18,0x80,0x00,0x00,0x00,0x00,0x00".split(",");
        bOutArray[0] = 30;
        bOutArray[1] = 30;
        bOutArray[2] = 20;
        bOutArray[3] = 30;
        // Log.e("TAG",""+hexStr2Str("0x00 00 60 00 18 80 00 18 00 30 00 60 00 06 80 18 80 18 80 00 00 00 00 00"));
        ///bOutArray = HexToByteArr("0000600018800");
        sb = new StringBuilder();
        try {
            mSerialPort = new SerialPort(new File("/dev/ttyS2"), 9600,
                    0);
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();
            receiveThread();
            mOutputStream.write(HexToByteArr("3030203030203630203030203138203830203030203138203030203330203030203630203030203036203830203138203830203138203830203030203030203030203030203030"));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("test",
                    "打开失败");
            e.printStackTrace();
        }
        //测试音频数据
  /*      Message msg = mHandler.obtainMessage();
        msg.what = -889;
        msg.obj = "是否播放";
        mHandler.sendMessage(msg);
*/
        String start = "file@��һ�����.mp3:������.mp3:��������.mp3";
        try {

            String str = new String(start.getBytes("US-ASCII"), "UTF-8");
            String y = URLEncoder.encode(start, "UTF-8");
            String ss = y;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static final byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();

        byte[] b = new byte[hex.length() / 2];

        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {

            String swap = "" + arr[i++] + arr[i];

            int byteint = Integer.parseInt(swap, 16) & 0xFF;

            b[j] = new Integer(byteint).byteValue();

        }

        return b;

    }


    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return bytes;
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;--
            sb.append(chars[bit]);
            // sb.append(' ');

        }
        return sb.toString().trim();
    }

    //转hex字符串转字节数组
    static public byte[] HexToByteArr(String inHex)//hex字符串转字节数组
    {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {//奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {//偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    // 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
    static public int isOdd(int num) {
        return num & 0x1;
    }

    //-------------------------------------------------------
    static public byte HexToByte(String inHex)//Hex字符串转byte
    {
        return (byte) Integer.parseInt(inHex, 16);
    }

    private void receiveThread() {
        // 接收
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    int size;
                    try {
                        byte[] buffer = new byte[1024];
                        if (mInputStream == null)
                            return;
                        size = mInputStream.read(buffer);
                        if (size > 0) {
                            String recinfo = new String(buffer, 0,
                                    size);
                            Log.i("test", "接收到串口信息:" + recinfo);
                            sb.append(recinfo).append(",");
                            // handler.sendEmptyMessage(1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receiveThread.start();
    }

    protected void initView() {
        FileUtil.copyFilesFromRaw(MainActivity.this, R.raw.jbd, "警报灯.mp4", FileUtil.getDirFile("media").getAbsolutePath().concat(File.separator));
        mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mImageView = (StateImageView) findViewById(R.id.iv_activity);
        mTextView = (TextView) findViewById(R.id.tv_activity);
        mTextClock = (TextClock) findViewById(R.id.tc_activity);
        findViewById(R.id.btn_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog();
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.pb_download);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        mTvBottom = (TextView) findViewById(R.id.tv_bottom);
        Button bu = (Button) findViewById(R.id.btn_video_stream);
        bu.setVisibility(View.GONE);
        initPop();
        intent_bg = new Intent(MainActivity.this, BackgroudService1.class);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow.isShowing()) {
                    //stopVideoStream();
                    mPopupWindow.dismiss();
                } else {
                    etIP.setText(Config.getInstance().getIP());
                    etPort.setText(String.valueOf(Config.getInstance().getPort()));
                    etAddr.setText(Config.getInstance().getAddr());
                    mPopupWindow.showAsDropDown(mImageView);
                }
            }
        });
    }

    /**
     * 隐藏虚拟按键
     */
    protected void hideBottomUIMenu() {
        // 隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private void startHeartBeat() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        mTimer.schedule(mTimerTask = new TimerTask() {
            @Override
            public void run() {
                App.getinit();
                HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, App.getCurrent() + "");
                AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
            }
        }, 0, Const.HERAT_BEAT_TIME);
    }

    private void showNetworkState(String state) {
        if (state.equals(Const.STATE_NORMAL)) {
            mImageView.setRightColor(Color.GREEN);
        } else {
            mImageView.setRightColor(Color.RED);
        }
    }

    private void showConnectState(String state) {
        if (state.equals(Const.STATE_NORMAL)) {
            mImageView.setLeftColor(Color.GREEN);
        } else {
            mImageView.setLeftColor(Color.RED);
        }
    }

    private void showConnectStateforBug() {
        mImageView.setLeftColor(Color.GREEN);
        mImageView.setLeftColor(Color.RED);
    }

    private void setProgressBarText(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mTvProgress.setVisibility(View.VISIBLE);
        mTvProgress.setText(content);
        mProgressBar.setProgress(0);
    }

    private void showProgress(String content) {
        int progress = Integer.parseInt(content);
        if (progress == 100) {
            mProgressBar.setVisibility(View.GONE);
            mTvProgress.setVisibility(View.GONE);
        } else {
            mProgressBar.setProgress(progress);
        }
    }


    public void ToTurnItOff(String timeon, String timeoff, int[] weekArray, boolean enable) {
        Intent intsent = new Intent("android.allwinner.intent.action.setpoweronoff");
        intsent.putExtra("timeon", timeon);  //设置开机时间
        intsent.putExtra("timeoff", timeoff); //设置关机时间
        intsent.putExtra("week", weekArray);  //设置星期
        intsent.putExtra("enable", enable); //开关控制
        sendBroadcast(intsent);
    }


    private PopupWindow mPopupWindow;
    private EditText etIP, etPort, etAddr;

    private void initPop() {
        mPopupWindow = new PopupWindow(mContext);
        View view = getLayoutInflater().inflate(R.layout.popup_ip_port, mRootView, false);
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        etIP = (EditText) view.findViewById(R.id.et_ip);
        etPort = (EditText) view.findViewById(R.id.et_port);
        etAddr = (EditText) view.findViewById(R.id.et_addr);
        Button btnModify = (Button) view.findViewById(R.id.btn_ip);

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = etIP.getText().toString();
                String port = etPort.getText().toString();
                String addr = etAddr.getText().toString();
                Toast.makeText(MainActivity.this, "配置中，请稍候！", Toast.LENGTH_SHORT).show();
                if (isIP(ip) == false) {
                    LogUtil.info("IP错误，请重新输入!");
                    return;
                }
                if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port) && !TextUtils.isEmpty(addr)) {
                    if (Config.getInstance().saveSetting(ip, Integer.parseInt(port), addr)) {
                        LogUtil.info("配置成功");
                        mTextView.setText("");
                        mPopupWindow.dismiss();
                        AppMessager.restartApp();
                    }
                } else {
                    LogUtil.info("IP、端口和安装地址不能有空");
                }
            }
        });
    }

    private FragmentManager mManager;
    public static MainActivity mContext;

    @Override
    protected void onDestroy() {
        Log.e("TAG", "onDestroy");


        super.onDestroy();
        App.serverMessager().stop();
        stopService(intent_bg);
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, restartIntent); // 1秒钟后重启应用
        System.exit(0);

    }

    private Broadcast mBroadcast;

    private void initSetting() {
        mManager = getSupportFragmentManager();
        mTimer = new Timer();
        mTimer1 = new Timer();
        mTimer2 = new Timer();
        App.bindMsgHandler(mHandler);
        App.startServerMessager();

        //默认显示钟表
        showFragment(ClockFragment.newInstance());
        checkNetwork();

        newAlarm.getInstance().refreshTaskAlarm(false);
        newAlarm.getInstance().setChongqi();
    }

    private void checkNetwork() {
        if (NetworkUtil.isNetworkConnected()) {
            showNetworkState(Const.STATE_NORMAL);
        } else {
            showNetworkState(Const.STATE_BREAK);
            App.stopServerMessager();
        }
    }

    private void showBottomText(String content) {
        LogUtil.debug(" 任务显示内容：" + content);
        if (content.equals("")) {
            return;
        }
        String[] lens = new String[]{};
        if (content.contains(Const.SPLIT)) {
            lens = content.split(Const.SPLIT);
        }
        if (lens.length > 0) {
            mTvBottom.setTextSize(100);
            mTvBottom.setVisibility(View.VISIBLE);
            mTvBottom.setText(lens[0]);
        } else {
            mTvBottom.setVisibility(View.VISIBLE);
            mTvBottom.setText(content);
        }
    }

    public void hideBottomText() {
        mTvBottom.setVisibility(View.GONE);
    }

    private void showToast(String str) {
        String[] strs = StringUtil.splitByHyphen(str);
        int color = Integer.parseInt(strs[0]);
        String content = strs[1];

        SpannableStringBuilder txtSpan = new SpannableStringBuilder(content);
        txtSpan.setSpan(new ForegroundColorSpan(color), 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TSnackbar.make(mRootView, txtSpan, TSnackbar.LENGTH_SHORT).setMaxWidth(getResources().getDisplayMetrics().widthPixels).show();
    }

    public void showFragment(Fragment fragment) {
        try {
            List<Fragment> frags = mManager.getFragments();
            if (frags != null) {
                for (Fragment frag : frags) {
                    mManager.beginTransaction().remove(frag).commitAllowingStateLoss();
                }
            }
            mManager.beginTransaction().add(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).addToBackStack("").commitAllowingStateLoss();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    public void closeFragment() {
        List<Fragment> frags = mManager.getFragments();
        if (frags != null || frags.size() > 0) {
            mManager.popBackStack();
        }
        showFragment(ClockFragment.newInstance());
    }

    public void setTextClockVisibility(int visibility) {
        mTextClock.setVisibility(visibility);
    }


    private void showLog() {
        if (showLog) {
            closeFragment();
            showLog = false;
        } else {
            new ReadLogAsync(mContext).execute();
            showLog = true;
        }
    }

    public void onClick(View v) {
//        List<Task> taskList;
//        StringBuilder sb;
        switch (v.getId()) {
            case R.id.btn_video_stream:
                //  AppMessager.sendVideoStreamState(Const.STATE_VIDEO_STREAM_DEFALUT);
//                int value = Gpio.readGpio('h', 10);
//                Log.e("TAG","11111"+value);

                //writeGpio();
                break;
//            case R.id.btn:
//                taskList = App.taskDao().queryAllTasks();
//                sb = new StringBuilder();
//                if (taskList != null) {
//                    for (Task tt : taskList) {
//                        sb.append(tt.toString()).append("\n");
//                    }
//                }
//                showFragment(TextFragment.newInstance(sb));
//                break;
//            case R.id.btn1:
//                AppMessager.send2Activity(Const.RECV_TASK, TaskTest.getTextJson(1, "16:00", "22:30", 1));
//                break;
//            case R.id.btn2:
//                AppMessager.send2Activity(Const.RECV_TASK, TaskTest.getTextJson(2, "22:20", "23:00", 1));
//                break;
//            case R.id.btn3:
//                AppMessager.send2Activity(Const.RECV_TASK, TaskTest.getTextJson(3, "18:50", "23:30", 1));
//                break;
//            case R.id.btn4:
//                taskList = App.taskDao().queryTodaySingleTasks();
//                sb = new StringBuilder();
//                if (taskList != null) {
//                    for (Task tt : taskList) {
//                        sb.append(tt.toString()).append("\n");
//                    }
//                }
//                showFragment(TextFragment.newInstance(sb));
//                break;
//            case R.id.btn5:
//                taskList = App.taskDao().queryTodayRepeatTasks();
//                sb = new StringBuilder();
//                if (taskList != null) {
//                    for (Task tt : taskList) {
//                        sb.append(tt.toString()).append("\n");
//                    }
//                }
//                showFragment(TextFragment.newInstance(sb));
//                break;
        }
    }

    private void gettime() {
        if (mTimerTask1 != null) {
            mTimerTask1.cancel();
            mTimerTask1 = null;
        }
        mTimer1.schedule(mTimerTask1 = new TimerTask() {
            @Override
            public void run() {
                AppMessager.send2Activity(Const.hideBottomUIMenu, "");
                MemoryUtils.cleanMemory(mContext);
            }
        }, 0, Const.HERAT_GET_TIME);
    }

    //设置系统时间，不需要任何权限
    public void setDate(String str) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            //os.writeBytes("date -s 20120419.024012; \n");
            os.writeBytes("date -s " + str + "; \n");
        } catch (Exception e) {
            Log.d("设置时间错误！", "error==" + e.toString());
            e.printStackTrace();
        }
    }

    //检查ip 端口和安装地址是否为空提示信息
    public void isIpNull() {
        String IP = Config.getInstance().getIP();
        String Prot = String.valueOf(Config.getInstance().getPort());
        String ADDR = Config.getInstance().getAddr();
        if (!IP.equals("") && !Prot.equals("") && !ADDR.equals("")) {
            LogUtil.info("检测到上次配置信息，共用成功！");
        }
    }

    //判断ip是否正确
    public boolean isIP(String addr) {
        Log.e("TAG", "" + addr);
        if (addr != null && !addr.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (addr.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;

    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        if (!isAppOnForeground()) {
            final Intent intent = new Intent(this, MainActivity.class);

            //app 进入后台 3秒后唤醒
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(intent);
                }
            }, 3000);
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }


    private void GetErrorButton() {
        if (mTimerTask2 != null) {
            mTimerTask2.cancel();
            mTimerTask2 = null;
        }
        mTimer2.schedule(mTimerTask2 = new TimerTask() {
            @Override
            public void run() {
                int value = Gpio.readGpio('h', 10);
                if (value == 0) {
                    Log.e("TAG", value + "   按下了求助按钮");
                    AppMessager.send2Server(Const.SEND_AlarmVideo, "{\"num\":\"" + App.deviceID + "\"}");
                }
            }
        }, 0, 300);

    }

    //屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    //打开屏蔽
    private void writeGpio(boolean bool) {
        Gpio.writeGpio('e', 2, 0);
        if (bool == true) {
            Gpio.writeGpio('e', 2, 1);
            LogUtil.info("屏蔽已打开");
        } else {
            Gpio.writeGpio('e', 2, 0);
            LogUtil.info("屏蔽已关闭");
        }
    }


    /**
     * 根据目录创建文件夹
     *
     * @param context
     * @param cacheDir
     * @return
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        //判断sd卡正常挂载并且拥有权限的时候创建文件
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
            Log.i(TAG, "appCacheDir: " + appCacheDir);
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    /**
     * 检查是否有权限
     *
     * @param context
     * @return
     */
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == 0;
    }


    /*usb数据copy --------------------start---------------2019年4月25日10:02:31*/

    //自定义U盘读写权限
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    //当前处接U盘列表
    private UsbMassStorageDevice[] storageDevices;
    //当前U盘所在文件目录
    private UsbFile cFolder;
    //private final static String U_DISK_FILE_NAME = "汤灿 - 莲美人.mp3";
    private final static String U_DISK_FILE_NAME = "test.mkv";

    /**
     * @description OTG广播注册
     * @author ldm
     * @time 2017/9/1 17:19
     */
    private void registerUDiskReceiver() {
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mOtgReceiver, usbDeviceStateFilter);
        //注册监听自定义广播
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mOtgReceiver, filter);
    }

    /**
     * @description OTG广播，监听U盘的插入及拔出
     * @author ldm
     * @time 2017/9/1 17:20
     * @param
     */
    CopyUsbFileUtil cufu = new CopyUsbFileUtil(mHandler);
    private BroadcastReceiver mOtgReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_USB_PERMISSION://接受到自定义广播
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    //允许权限申请
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbDevice != null) {
                            //用户已授权，可以进行读取操作
                            cufu.readDevice(cufu.getUsbMass(usbDevice));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //  final String path = cufu.readFromUDisk();
                                    cufu.readFromUDisk();//读到数据
                                }
                            }).start();

                        } else {
                            showToastMsg("没有插入U盘");
                        }
                    } else {
                        showToastMsg("未获取到U盘权限");
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到U盘设备插入广播
                    UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    showToastMsg("U盘插入");
                    if (device_add != null) {
                        //接收到U盘插入广播，尝试读取U盘设备数据
                        cufu.redUDiskDevsList();
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED://接收到U盘设设备拔出广播
                    showToastMsg("U盘已拔出");
                    break;
            }
        }
    };

    private void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*usb数据copy --------------------end---------------2019年4月25日10:02:31*/
}
