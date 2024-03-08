package cn.com.data_plus.bozhilian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.global.newAlarm;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.TimeUtil;

/**
 * 任务开始处理
 */
public class TaskHandleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.debug("response the alarm clock(" + TimeUtil.getTime() + ")");
        int action = intent.getIntExtra(newAlarm.EXTRA_ACTION, -1);
        newTask task = App.gson().fromJson(intent.getStringExtra(newAlarm.EXTRA_TASK_JSON), newTask.class);
        switch (action) {
            case newAlarm.ACTION_START:
                if(newAlarm.TimeIsNow(task)){
                    newAlarm.getInstance().startTask(task);
                }else{
                    LogUtil.error( "任务播放时间已经过去。");
                }
                break;
            case newAlarm.ACTION_STOP:

                    newAlarm.getInstance().stopTask(task,1);


                TimeUtil.delayExe(1000);
               // newAlarm.getInstance().refreshTaskAlarm(task);
                break;

            case  -1:
                newAlarm.getInstance().refreshTaskAlarm(true);
                TimeUtil.delayExe(3000);
                AppMessager.send2Activity(Const.MSG_resetApp, "");

                break;

            case  147852369:
                newAlarm.getInstance().refreshTaskAlarm(true);
                TimeUtil.delayExe(3000);
                AppMessager.send2Activity(Const.MSG_resetApp, "");

                break;
            case 1478523691:
                newAlarm.getInstance().refreshTaskAlarm(true);
                TimeUtil.delayExe(3000);
                AppMessager.send2Activity(Const.MSG_resetApp, "");

                break;
            default:
                break;
        }
    }

}