package cn.com.data_plus.bozhilian.global;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.bean.send.TaskBeat;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public class PlayPic {
    String[] mPath;
    String[] szName;
    private static int picCount = -1;
    private static int count = 0;
    String filePlayTime;
    private TimerTask mTimerTask;
    private Timer mTimer;

    private int Playnumber;
    private newTask task;

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap bmp = BitmapFactory.decodeFile(mPath[picCount]);
                    imageView.setImageBitmap(bmp);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    Dialog dia;
    ImageView imageView;

    void PlayPics(final newTask task) {
        this.task = task;
        String[] szId = task.getTaskFileID().split(Const.SPLIT);
        Playnumber = Integer.parseInt(task.getTaskPlaynumber());
        mPath = new String[szId.length];
        String[] szUrl = task.getUrl().split(Const.SPLIT);
        String[] szLenght = task.getTaskFileLength().split(Const.SPLIT);

        szName = task.getTaskFileName().split(Const.SPLIT);
        for (int i = 0; i < szId.length; i++) {
            mPath[i] = FileUtil.getFile(FileUtil.SUB_DIR_MEDIA, szName[i]).getAbsolutePath();
        }
        count = mPath.length;
        dia = new Dialog(App.context, R.style.edit_AlertDialog_style);
        dia.setContentView(R.layout.activity_start_dialog);
        //imageView = (ImageView) dia.findViewById(R.id.start_img);
        dia.setCanceledOnTouchOutside(false); // Sets whether this dialog is
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        dia.onWindowAttributesChanged(lp);
        dia.show();
        App.TaskName = task.getTaskName();
        HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, "当前播放的任务：" + task.getTaskName(), App.getCurrent() + "");
        AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
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
                                    //主要是怕任务队列重复
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
        }, 0, Integer.parseInt(filePlayTime) * 1000);
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


        if (dia != null) {
            dia.dismiss();
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private static PlayPic playPic;

    public static PlayPic getInstance() {
        picCount = -1;
        count = 0;
        if (playPic == null) {
            playPic = new PlayPic();
        }
        return playPic;
    }
}
