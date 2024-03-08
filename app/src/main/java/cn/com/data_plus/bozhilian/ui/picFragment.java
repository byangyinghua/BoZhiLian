package cn.com.data_plus.bozhilian.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.bean.send.TaskBeat;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.global.newAlarm;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;

public class picFragment extends BaseFragment {

    private final static String TASK_JSON = "";
    public static String NET_DATA = "net";
    public static String USB_DATA = "usb";
    private final static String TASK_TYPE = "";//区分网咯图片和本地usb图片
    String[] mPath;
    String[] szName;
    private static int picCount = -1;
    private static int count = 0;
    String filePlayTime;
    private TimerTask mTimerTask;
    private Timer mTimer;

    private int Playnumber;

    private int playtime;
    private newTask task;

    Handler myHandler = new Handler() {//设置图片
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap bmp = BitmapFactory.decodeFile(mPath[picCount]);
                    //  Bitmap bmp = BitmapFactory.decodeFile(path+"/dddd.jpg");
                    imageView.setImageBitmap(bmp);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    Dialog dia;
    ImageView imageView;

    /**
     * @param taskJson
     * @return
     */
    public static picFragment newInstance(String taskJson) {
        picFragment videoFragment = new picFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TASK_JSON, taskJson);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout rootView = new RelativeLayout(mActivity);
        try {
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.setBackgroundColor(Color.BLACK);
            rootView.setGravity(Gravity.CENTER);
            imageView = new ImageView(mActivity);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            rootView.addView(imageView);
            String taskJson = getArguments().getString(TASK_JSON);
            task = App.gson().fromJson(taskJson, newTask.class);
            if (task.getNetUsbType().equals(NET_DATA)) {//网络图片
                String[] szId = task.getTaskFileID().split(Const.SPLIT);
                mPath = new String[szId.length];
                szName = task.getTaskFileName().split(Const.SPLIT);
                for (int i = 0; i < szId.length; i++) {
                    mPath[i] = FileUtil.getFile(FileUtil.SUB_DIR_MEDIA, szName[i]).getAbsolutePath();
                }
            } else if (task.getNetUsbType().equals(USB_DATA)) {//usb图片
                mPath = new String[task.getTaskFileName().split(Const.SPLIT).length];
                String[] usls = task.getUrl().split(Const.SPLIT);
                for (int i = 0; i < usls.length; i++) {
                    mPath[i] = usls[i];
                }
            }
            Playnumber = Integer.parseInt(task.getTaskPlaynumber());
            playtime = Integer.parseInt(task.getFilePlayTime());

            count = mPath.length;
            playPic();
        } catch (Exception e) {
            mActivity.closeFragment();
        }


        return rootView;
    }


    public void playPic() {
        App.TaskName = task.getTaskName();
        HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, "当前播放的任务：" + task.getTaskName(), App.getCurrent() + "");
        AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
        if (task.getTaskContent() != null)
            AppMessager.send2Activity(Const.MSG_SHOW_BOTTOM_TEXT, task.getTaskContent());
        filePlayTime = task.getFilePlayTime();
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        mTimer = new Timer();
        mTimer.schedule(mTimerTask = new TimerTask() {
            @Override
            public void run() {
                picCount++;
                if (picCount < count) {
                    Message message = new Message();
                    message.what = 1;
                    myHandler.sendMessage(message);
                } else {
                    Playnumber--;
                    if (Playnumber == 0) {
                        List<newTask> s = new ArrayList<newTask>();
                        if (App.newTasks.size() == 1) {
                            App.newTasks = new ArrayList<newTask>();
                        }
                        if (App.newTasks != null) {
                            for (newTask n : App.newTasks) {
                                if (n.getTaskID().equals(task.getTaskID())) {
                                    s.add(n);
                                }
                            }
                            if (s != null) {
                                for (newTask n : s) {
                                    App.newTasks.remove(n);
                                }
                            }
                            LogUtil.debug("执行完毕，任务队列还有" + App.newTasks.size() + "个任务");
                            if (App.newTasks.size() > 0) {
                                newAlarm.getInstance().startTasks(App.newTasks.get(0));

                            }
                        }
                        StopPic();
                    } else {
                        picCount = 0;
                        Message message = new Message();
                        message.what = 1;
                        myHandler.sendMessage(message);
                    }
                }

            }
        }, 0, playtime * 1000);
    }


    public void StopPic() {
        App.TaskName = "空闲";
        if (App.newTasks == null) {
            TaskBeat heartBeat1 = new TaskBeat(task.getTaskID());
            AppMessager.send2Server(Const.SEND_Stop_Task, App.gson().toJson(heartBeat1));
            HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, App.getCurrent() + "");
            AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
        } else {
            TaskBeat heartBeat1 = new TaskBeat(task.getTaskID());
            AppMessager.send2Server(Const.SEND_Stop_Task, App.gson().toJson(heartBeat1));
        }
        AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        mActivity.closeFragment();
    }


    @Override
    public void onStop() {
        super.onStop();
        StopPic();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
