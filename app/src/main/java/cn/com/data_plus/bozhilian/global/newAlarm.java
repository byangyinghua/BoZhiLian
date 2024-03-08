package cn.com.data_plus.bozhilian.global;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.bean.send.TaskBeat;
import cn.com.data_plus.bozhilian.receiver.ShutdownReceiver;
import cn.com.data_plus.bozhilian.receiver.TaskHandleReceiver;
import cn.com.data_plus.bozhilian.test.TaskTest;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.TimeUtil;

public class newAlarm {
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_TASK_JSON = "task_json";
    public static final int ACTION_START = 0;
    public static final int ACTION_STOP = 1;

    private static final int DEFAULT_NUM = 7772399;

    public void refreshTaskAlarm(boolean flag) {
        List<newTask> all = App.newtaskDao().queryAllTasks();
        if (all == null)
            return;
        for (newTask n : all) {
            try {
                setTaskAlarm(n);
//                if (TimeUtil.isEffectiveDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(TimeUtil.getDate()), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(n.getTaskDate()), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(n.getEndTaskDate()))) {
//                    if (flag) {
//                        setTaskAlarm(n);
//                    } else {
//                        if (TimeUtil.isInTime(n.getTaskTime() + "-" + n.getEndTaskTime(), TimeUtil.getTime())) {
//                            Log.e("TAG", "TaskName>>>" + n.getTaskName());
//                            Log.e("TAG", "TaskDate>>>" + n.getTaskDate());
//                            Log.e("TAG", "TaskTime>>>" + n.getTaskTime());
//                            Log.e("TAG", "EndTaskDate>>>" + n.getEndTaskDate());
//                            Log.e("TAG", "EndTaskTime>>>" + n.getEndTaskTime());
//                            Log.e("TAG", "TaskLevel>>>" + n.getTaskLevel());
//                            setTaskAlarm(n);
//                        }
//                    }
//
//
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //niu  设置定时任务

    public void setTaskAlarm(newTask task) {

//        try {
//            stopTaskFirst(task, 0);
//            cancelTaskAlarm(task);
//        } catch (Exception e) {
//            LogUtil.debug("任务未设置闹钟~");
//        }
        long begin = 0;
        long end = 0;
        if (Integer.parseInt(task.getTaskLevel()) == 0) {
            begin = TimeUtil.getMilliSecond(task.getTaskDate(), task.getTaskTime());
            end = TimeUtil.getMilliSecond(task.getEndTaskDate(), task.getEndTaskTime());
        } else {
            begin = TimeUtil.getMilliSecond(App.date, task.getTaskTime());
            end = TimeUtil.getMilliSecond(App.date, task.getEndTaskTime());
        }//1556550000000
        setAlarm(begin, buildTaskPendingIntent(ACTION_START, task));//开始任务时间
        setAlarm(end, buildTaskPendingIntent(ACTION_STOP, task));//结束任务时间
        //LogUtil.info("任务(" + task.getTaskName() + task.getTaskDate() + " " + task.getTaskTime() + ")设置闹钟成功");
    }

    public void setChongqi() {
        setAlarm(TimeUtil.getMilliSecond(App.date, "23:59:59"), buildTaskPendingIntent(-1, new newTask(TaskTest.al("111"))));
    }

    public void cancelTaskAlarm(newTask task) {
        try {
            App.TaskName = "空闲";

            HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, "空闲", App.getCurrent() + "");
            AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
            task.updateAlarClock("1");
            task.updateTaskState("0");
            task.updateTaskPlayState("0");
            mAlarmManager.cancel(buildTaskPendingIntent(ACTION_START, task));
            mAlarmManager.cancel(buildTaskPendingIntent(ACTION_STOP, task));

            List<newTask> s = new ArrayList<newTask>();


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
            }
            //LogUtil.info("任务(" + task.getTaskName() + task.getTaskDate() + "" + task.getTaskTime() + ")取消闹钟成功");
        } catch (Exception e) {
            LogUtil.error("Exception" + e.toString());
        }

    }


    /**
     * @param //date是为则默认今天日期、可自行设置“2013-06-03”格式的日期
     * @return 返回1是星期日、2是星期一、3是星期二、4是星期三、5是星期四、6是星期五、7是星期六
     */

    public static int getDayofweek(String date) {
        Calendar cal = Calendar.getInstance();
//   cal.setTime(new Date(System.currentTimeMillis()));
        if (date.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            cal.setTime(new Date(getDateByStr2(date).getTime()));
        }
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Date getDateByStr2(String dd) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sd.parse(dd);
        } catch (ParseException e) {
            date = null;
            e.printStackTrace();
        }
        return date;
    }

    private Timer mTimer;

    private TimerTask mTimerTask;

    private void mTimerStopTask(final newTask task) {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        mTimer.schedule(mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("run", "run");
                //heartBeat.setTaskContent(Alarm.getInstance().getTaskState());

                stopTask(task, 1);
                mTimer.cancel();

            }
        }, 60000);
    }

    public static boolean TimeIsNow(newTask task) {
//        LogUtil.debug("TaskID>>>" + task.getTaskID());
//        LogUtil.debug("TaskName>>>" + task.getTaskName());
//        LogUtil.debug( "TaskDate>>>" + task.getTaskDate());
//        LogUtil.debug("TaskTime>>>" + task.getTaskTime());
//        LogUtil.debug("EndTaskDate>>>" + task.getEndTaskDate());
//        LogUtil.debug( "EndTaskTime>>>" + task.getEndTaskTime());
//        LogUtil.debug( "TaskLevel>>>" + task.getTaskLevel());
        try {
            if (task != null && task.getTaskDate() != null)
                if (TimeUtil.isEffectiveDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(TimeUtil.getDate()), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.getTaskDate()), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.getEndTaskDate()))) {
                    LogUtil.debug(">>>false");
                    if (TimeUtil.isInTime(task.getTaskTime() + "-" + task.getEndTaskTime(), TimeUtil.getTime())) {
                        return true;
                    }

                }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    //start execute task
    public void startTask(newTask task) {
        if (task.getTaskLevel() == "0.01") {
            App.newTasks.add(task);
        }


        mTimer = new Timer();
        App.TaskName = task.getTaskName();
        HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, App.getCurrent() + "");
        AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));

        // LogUtil.debug("任务级别！" + task.getTaskLevel());
        if (Integer.parseInt(task.getTaskLevel()) == 0) {
            if (App.newTasks.size() > 0) {
                newTask ns = App.newTasks.get(0);
                int fileType = Integer.parseInt(ns.getTaskType());
                if (fileType == 0) {//Audio
                    newAudio.getInstance().stopPlayingMusic(ns);
                } else if (fileType == 1) {//video
                    AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
                } else if (fileType == 3) {//video
                    AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
                } else {//text
                    if (Integer.parseInt(ns.getTaskID()) == Config.getInstance().getShowFragId()) {
                        AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
                    } else {
                        AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
                    }
                }

                AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
            }

            AppMessager.st = "正在执行考试预案" + task.getTaskName();
            task.updateAlarClock("1");
            int fileType = Integer.parseInt(task.getTaskType());
            LogUtil.debug(" ， 任务类型！" + fileType);
            if (fileType == 0) {//audio 音频
                newAudio.getInstance().startPlayMusic(task);
            } else if (fileType == 1) {//video
                AppMessager.send2Activity(Const.MSG_PLAY_VIDEO, App.gson().toJson(task));
            } else if (fileType == 3) {//video
                AppMessager.send2Activity(Const.MSG_PLAY_PIC, App.gson().toJson(task));
                //PlayPic.getInstance().PlayPics(task);
            } else {//text
                if (Config.getInstance().isShowFrag()) {
                    AppMessager.send2Activity(Const.MSG_SHOW_BOTTOM_TEXT, task.getTaskContent() + Const.SPLIT);
                } else {
                    Config.getInstance().saveShowFragId(Integer.parseInt(task.getTaskID()));
                    AppMessager.send2Activity(Const.MSG_SHOW_LARGE_TEXT, task.getTaskContent());
                    mTimerStopTask(task);
                }
            }
            return;
        }
        for (newTask n : App.newTasks) {
            if (n.getTaskLevel() == "0") {
                LogUtil.debug("有一个预案正在执行中，所有任务暂停播放！");
                return;
            }
        }
        LogUtil.debug("day>>>>>" + task.getDay());
        //返回1是星期日、2是星期一、3是星期二、4是星期三、5是星期四、6是星期五、7是星期六
        int day = getDayofweek("");
        String[] days = task.getDay().split(",");
        int newday = 0;
        if (day == 2) {
            newday = 1;
        } else if (day == 3) {
            newday = 2;
        } else if (day == 4) {
            newday = 3;
        } else if (day == 5) {
            newday = 4;
        } else if (day == 6) {
            newday = 5;
        } else if (day == 7) {
            newday = 6;
        } else if (day == 1) {
            newday = 7;
        }
        if (task.getDay().indexOf(newday + "") == -1) {

            LogUtil.debug("任务不可以执行");
            return;
        }
        //  LogUtil.debug("来了一个新任务，开始检查是否有任务列表");
        if (App.newTasks.size() > 0) {
            // LogUtil.debug("任务列表里有正在执行的任务或者待执行任务，默认取得第一个");
            newTask n = App.newTasks.get(0);

            if (n == task) {
                App.newTasks.remove(0);
                n = null;
                n = App.newTasks.get(0);
            }
//            for (newTask s :App.newTasks){
//            }
            //级别1-5  1最高  此处只是判断1 - 5 的实际大小 n 老任务 task新进来的任务
            // 判断级别  如果正在执行得任务级别大小大于新进来得任务  那么正在执行的任务级别就小于新进来得任务 那么就加入列表，等待前任务执行完毕
            //LogUtil.debug(n.getTaskLevel() + "<<正在播放级别<<判断级别>>>>新进任务级别>>>" + task.getTaskLevel());
            if (Integer.parseInt(n.getTaskLevel()) <= Integer.parseInt(task.getTaskLevel())) {
                //LogUtil.debug("正在执行的任务级别大于新进来的任务，新进来的任务添加任务队列");

                newTask a = null;
                for (newTask s : App.newTasks) {
                    if (task.getTaskID() == s.getTaskID()) {
                        a = null;
                    }
                }

                if (a == null) {
                    App.newTasks.add(task);
                }

            } else {
                // LogUtil.debug("新进来的任务级别大于正在执行的任务，正在的任务添加任务队列");
                //如果任务级别大小小于新进来的任务  那么就代表新任务级别比正在执行的级别大  那就先把老任务加入列表 执行 新任务

                newTask a = null;
                for (newTask s : App.newTasks) {
                    if (task.getTaskID() == s.getTaskID()) {
                        a = null;
                    }
                }

                if (a == null) {
                    App.newTasks.add(0, task);
                }

                //先停止老任务
                // LogUtil.debug("停止老任务");
                int fileType = Integer.parseInt(n.getTaskType());
                if (fileType == 0) {//Audio
                    newAudio.getInstance().stopPlayingMusic(n);
                } else if (fileType == 1) {//video
                    AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
                } else if (fileType == 3) {//video
                    //PlayPic.getInstance().PlayPics(task);
                    AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
                } else {//text
                    if (Integer.parseInt(n.getTaskID()) == Config.getInstance().getShowFragId()) {
                        AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
                    } else {
                        AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
                    }
                }
                //LogUtil.debug("停止成功，执行新任务");
                //执行新任务
                AppMessager.st = "正在执行" + task.getTaskName();
                task.updateAlarClock("1");
                int fileTypes = Integer.parseInt(task.getTaskType());
                if (fileTypes == 0) {//audio
                    newAudio.getInstance().startPlayMusic(task);
                } else if (fileTypes == 1) {//video
                    AppMessager.send2Activity(Const.MSG_PLAY_VIDEO, App.gson().toJson(task));
                } else if (fileType == 3) {//video
                    AppMessager.send2Activity(Const.MSG_PLAY_PIC, App.gson().toJson(task));
                    // PlayPic.getInstance().PlayPics(task);
                } else {//text
                    if (Config.getInstance().isShowFrag()) {
                        AppMessager.send2Activity(Const.MSG_SHOW_BOTTOM_TEXT, task.getTaskContent());
                    } else {
                        Config.getInstance().saveShowFragId(Integer.parseInt(task.getTaskID()));
                        AppMessager.send2Activity(Const.MSG_SHOW_LARGE_TEXT, task.getTaskContent());
                    }
                }
            }
        } else {
            //LogUtil.debug("任务列表里面没有没有任务执行新进任务开始");
            newTask a = null;
            for (newTask s : App.newTasks) {
                if (task.getTaskID() == s.getTaskID()) {
                    a = null;
                }
            }

            if (a == null) {
                App.newTasks.add(task);
            }
            AppMessager.st = "正在执行" + task.getTaskName();
            task.updateAlarClock("1");
            int fileType = Integer.parseInt(task.getTaskType());

            if (fileType == 0) {//audio
                newAudio.getInstance().startPlayMusic(task);
            } else if (fileType == 1) {//video
                AppMessager.send2Activity(Const.MSG_PLAY_VIDEO, App.gson().toJson(task));
            } else if (fileType == 3) {//video
                AppMessager.send2Activity(Const.MSG_PLAY_PIC, App.gson().toJson(task));
                //PlayPic.getInstance().PlayPics(task);
            } else {//text
                if (Config.getInstance().isShowFrag()) {
                    AppMessager.send2Activity(Const.MSG_SHOW_BOTTOM_TEXT, task.getTaskContent());
                } else {
                    Config.getInstance().saveShowFragId(Integer.parseInt(task.getTaskID()));
                    AppMessager.send2Activity(Const.MSG_SHOW_LARGE_TEXT, task.getTaskContent());
                }
            }
        }
        LogUtil.debug("任务执行开始");
    }

    public void startTasks(newTask task) {
        if (task.getTaskLevel() == "0.01") {
            App.newTasks.add(task);
        }
        try {
            if (TimeUtil.isEffectiveDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(TimeUtil.getDate()), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.getTaskDate()), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.getEndTaskDate()))) {

            } else {
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AppMessager.st = "正在执行" + task.getTaskName();
        App.TaskName = task.getTaskName();
        HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, App.getCurrent() + "");
        AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
        task.updateAlarClock("1");
        int fileType = Integer.parseInt(task.getTaskType());

        if (fileType == 0) {//audio
            newAudio.getInstance().startPlayMusic(task);
        } else if (fileType == 1) {//video
            AppMessager.send2Activity(Const.MSG_PLAY_VIDEO, App.gson().toJson(task));
        } else if (fileType == 3) {//video
            AppMessager.send2Activity(Const.MSG_PLAY_PIC, App.gson().toJson(task));
            //PlayPic.getInstance().PlayPics(task);
        } else {//text
            if (Config.getInstance().isShowFrag()) {
                AppMessager.send2Activity(Const.MSG_SHOW_BOTTOM_TEXT, task.getTaskContent());
            } else {
                Config.getInstance().saveShowFragId(Integer.parseInt(task.getTaskID()));
                AppMessager.send2Activity(Const.MSG_SHOW_LARGE_TEXT, task.getTaskContent());
            }
        }
    }

    //stop execution
    public void stopTaskFirst(newTask task, int i) {
        List<newTask> s = new ArrayList<newTask>();
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

        }
        int fileType = Integer.parseInt(task.getTaskType());
        if (fileType == 0) {//Audio
            newAudio.getInstance().stopPlayingMusic(task);
        } else if (fileType == 1) {//video
            AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
        } else if (fileType == 3) {//video
            AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
        } else {//text
            if (Integer.parseInt(task.getTaskID()) == Config.getInstance().getShowFragId()) {
                AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
            } else {
                AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
            }
        }

        LogUtil.debug("任务执行结束");
    }

    //stop execution
    public void stopTask(newTask task, int i) {
        List<newTask> s = new ArrayList<newTask>();
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

        }
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
        task.updateAlarClock("1");
        task.updateTaskState("0");
        task.updateTaskPlayState("2");
        AppMessager.st = "空闲";
        int fileType = Integer.parseInt(task.getTaskType());
        if (fileType == 0) {//Audio
            newAudio.getInstance().stopPlayingMusic(task);
        } else if (fileType == 1) {//video
            AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
        } else if (fileType == 3) {//video
            AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
        } else {//text
            if (Integer.parseInt(task.getTaskID()) == Config.getInstance().getShowFragId()) {
                AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
            } else {
                AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
            }
        }

        AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
        LogUtil.debug("任务执行结束");
        if (i == 1) {
            try {
                //refreshTaskAlarm(false);
                if (TimeUtil.isSameDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.getEndTaskDate()), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(TimeUtil.getDate()))) {
                    cancelTaskAlarm(task);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopTasks(newTask task, int i) {

        List<newTask> s = new ArrayList<newTask>();

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

        }
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
        task.updateAlarClock("1");
        task.updateTaskState("0");
        task.updateTaskPlayState("2");
        AppMessager.st = "空闲";
        int fileType = Integer.parseInt(task.getTaskType());
        if (fileType == 0) {//Audio
            newAudio.getInstance().stopPlayingMusic(task);
        } else if (fileType == 1) {//video
            AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
        } else if (fileType == 3) {//video
            AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
        } else {//text
            if (Integer.parseInt(task.getTaskID()) == Config.getInstance().getShowFragId()) {
                AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
            } else {
                AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
            }
        }


        LogUtil.debug("任务执行结束");
        if (i == 1) {
            try {
                //refreshTaskAlarm(false);
                if (TimeUtil.isSameDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.getEndTaskDate()), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(TimeUtil.getDate()))) {
                    cancelTaskAlarm(task);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    //stop execution
    public void stopTaskAll(newTask task) {
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
        task.updateAlarClock("1");
        task.updateTaskState("0");
        task.updateTaskPlayState("2");
        AppMessager.st = "空闲";
        int fileType = Integer.parseInt(task.getTaskType());
        if (fileType == 0) {//Audio
            newAudio.getInstance().stopPlayingMusic(task);
        } else if (fileType == 1) {//video
            AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
        } else if (fileType == 3) {//video
            AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
        } else {//text
            if (Integer.parseInt(task.getTaskID()) == Config.getInstance().getShowFragId()) {
                AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
            } else {
                AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
            }
        }

        AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");
        LogUtil.debug("任务执行结束");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setAlarm(long time, PendingIntent pendingIntent) {
        ////AlarmManager.RTC_WAKEUP 在系统精确时间触发，会唤醒cpu
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

    }

    //set the time of this machine
    public void setMachineTime(long millis) {
        mAlarmManager.setTime(millis);
    }

    /**
     * 设置自动关机闹钟
     *
     * @param time eg."18:01:00"
     */
    public void setShutdownAlarm(String time) {
        long milliSecond = TimeUtil.getMilliSecond(TimeUtil.getDate(), time);//相对与当前的时间
        Toast.makeText(App.app, "设置自动关机时间:" + TimeUtil.getDateAndTime(milliSecond), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(App.app, ShutdownReceiver.class);//XXX为处理时间到之后执行关机的类名
        PendingIntent powerOffSender = PendingIntent.getBroadcast(App.app, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        setAlarm(milliSecond, powerOffSender);
    }

    /**
     * 设置自动开机闹钟
     *
     * @param time eg."08:00:00"
     */
    public void setBootAlarm(String time) {
        long milliSecond = TimeUtil.getMilliSecond(TimeUtil.getDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000), time);//相对与当前的时间
        Toast.makeText(App.app, "设置自动开机时间:" + TimeUtil.getDateAndTime(milliSecond), Toast.LENGTH_SHORT).show();
    }

    private PendingIntent buildTaskPendingIntent(int action, newTask task) {
        Intent intent = new Intent(App.app, TaskHandleReceiver.class);
        intent.putExtra(EXTRA_ACTION, action);
        intent.putExtra(EXTRA_TASK_JSON, App.gson().toJson(task));
        PendingIntent pi;
        if (action == ACTION_START) {
            pi = PendingIntent.getBroadcast(
                    App.app,
                    Integer.parseInt(task.getTaskID()),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pi = PendingIntent.getBroadcast(
                    App.app,
                    Integer.parseInt(task.getTaskID()) - DEFAULT_NUM,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }


        return pi;
    }

    private static newAlarm sAlarm;
    private AlarmManager mAlarmManager;//实现定时间隔功能

    private newAlarm() {
        mAlarmManager = (AlarmManager) App.app.getSystemService(Context.ALARM_SERVICE);
    }

    public static newAlarm getInstance() {
        if (sAlarm == null) {
            sAlarm = new newAlarm();
        }
        return sAlarm;
    }
}