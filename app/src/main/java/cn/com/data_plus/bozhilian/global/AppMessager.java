package cn.com.data_plus.bozhilian.global;

import android.os.Message;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.bean.send.TaskState;
import cn.com.data_plus.bozhilian.bean.send.Terminal;
import cn.com.data_plus.bozhilian.bean.send.VideoStream;

import static cn.com.data_plus.bozhilian.App.deviceID;
import static cn.com.data_plus.bozhilian.App.serverAddr;

public class AppMessager {

    /*----------------------------发送界面处理逻辑----------------------------*/
    //发送消息，在activity中处理
    public static void send2Activity(int order, String content) {
        if (App.msgHandler() != null) {
            Message msg = Message.obtain();
            msg.what = order;
            msg.obj = content;
            App.msgHandler().sendMessage(msg);
        }
    }

    //重启app
    public static void restartApp() {
        send2Activity(Const.MSG_CLOSE_ACTIVITY, "");
    }

    //重启app
    public static void newrestartApp() {
        send2Activity(Const.MSG_resetApp, "");
    }


    //提示各种状态的改变
    public static void toast(String content, int color) {
        send2Activity(Const.MSG_TOAST, color + "-" + content);
    }

    //重设进度条
    public static void resetProgress(String showText) {
        send2Activity(Const.MSG_RESET_PROGRESS, showText);
    }

    //显示进度
    public static void showProgress(int progress) {
        send2Activity(Const.MSG_SHOW_PROGRESS, String.valueOf(progress));
    }

    /*----------------------------发送界面处理逻辑----------------------------*/
    /*----------------------------发送到服务器信息----------------------------*/
    //向服务器发送消息
    public static void send2Server(int order, String jsonContent) {
        App.serverMessager().send(order, jsonContent);
    }

    //发送验证连接
    public static void sendTerminalInfo() {
        Terminal terminal = new Terminal(serverAddr, deviceID, Config.getInstance().getAddr(), serverAddr,App.getHostIP(),Config.getInstance().getTurn(),Config.getInstance().getOff(),Config.getInstance().getweekArray());
        String jsonStr = App.gson().toJson(terminal);
        send2Server(Const.SEND_CONNECT, jsonStr);
    }


    public static   String st = "";

    public static void sendTaskRealTimeState(String state) {
        HeartBeat heartBeat = new HeartBeat(deviceID, serverAddr, st,App.getCurrent()+"");
        heartBeat.setTaskContent(state);
        send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
    }



    //发送任务状态
    public static void sendTaskState(int taskID,int receiveState, int downloadState) {
        TaskState taskState = new TaskState(taskID, deviceID, receiveState, downloadState,0);
        String jsonStr = App.gson().toJson(taskState);
        send2Server(Const.SEND_TASK_ORDER, jsonStr);
    }

    //发送视频流连接状态
    public static void sendVideoStreamState(int state) {
        VideoStream videoStream = new VideoStream(deviceID, state);
        send2Server(Const.SEND_VIDEO_STREAM, App.gson().toJson(videoStream));
    }
        /*----------------------------发送到服务器信息----------------------------*/
}