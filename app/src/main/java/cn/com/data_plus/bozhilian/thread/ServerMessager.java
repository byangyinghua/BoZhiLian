package cn.com.data_plus.bozhilian.thread;

import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.TaskMsgCode;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Config;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.StringUtil;
import cn.com.data_plus.bozhilian.util.TimeUtil;

public class ServerMessager implements Runnable {
    private Socket mSocket;
    private boolean mRunning;
    private boolean isConnected;
    private String mIp;
    private int mPort;

    @Override
    public void run() {
        LogUtil.debug("主通信线程启动");
        mIp = Config.getInstance().getIP();
        mPort = Config.getInstance().getPort();
        if (TextUtils.equals(mIp, Const.DEFAULT_IP)) {
            AppMessager.send2Activity(Const.MSG_SHOW_TOP_TEXT, "初次运行\n请在右上角\n初始化配置");
            return;
        }

        connect();
        while (mRunning) {
            if (isConnected) {
                try {
                    waitReceive();
                } catch (IOException e) {

                    LogUtil.error(e);
                }
            }
        }
        LogUtil.debug("主通信线程结束");
    }

    private void connect() {
        if (!mRunning) {
            return;
        }
        try {
            isConnected = false;
            mIp = Config.getInstance().getIP();
            mPort = Config.getInstance().getPort();
            mSocket = new Socket(mIp, mPort);
            isConnected = true;
            AppMessager.send2Activity(Const.MSG_NOTICE_CONNECT_STATE, Const.STATE_NORMAL);
            LogUtil.debug("成功连接服务器(未验证)");
        } catch (IOException e) {
            isConnected = false;
            AppMessager.send2Activity(Const.MSG_NOTICE_CONNECT_STATE, Const.STATE_BREAK);
            TimeUtil.delayExe(1000);
            LogUtil.error("连接服务器时发生错误");
            reConnect();
        }
    }

    private int reConnectCount;

    private void reConnect() {
        try {
            if (!mRunning) {
                return;
            }
            LogUtil.info("ip端口配置错误，或者服务器出错!30秒后尝试重新连接");
            TimeUtil.delayExe(30000);
            App.restartServerMessager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        mRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        mRunning = false;
        isConnected = false;
        reConnectCount = 0;
        new Thread(this).interrupt();
    }

    private void waitReceive() throws IOException {
        InputStream is = mSocket.getInputStream();
        int size = 4096;
        byte[] bytes = new byte[size];
        int hasRead;
        StringBuilder sb = new StringBuilder();
        boolean b = true;
        while (b && bytes.length > 0) {
            try {
                hasRead = is.read(bytes);
                if (hasRead != -1) {
                    String str = new String(bytes, 0, hasRead, "gbk");
                    sb.append(str);
                }
                if (hasRead < size) {
                    b = false;
                }
            } catch (Exception e) {
                AppMessager.send2Activity(Const.MSG_STOP_SERVER, "");
                e.printStackTrace();
            }

        }
        String recvStr = sb.toString().trim();
        int order;
        String content;
        try {
            if (TextUtils.isEmpty(recvStr)) {//接收字符串为空说明被服务端踢下线
                order = Const.MSG_CONNECT_FAILED_BY_REMOVE_FROM_SERVER;
                content = "";
                isConnected = false;
                AppMessager.send2Activity(Const.MSG_NOTICE_CONNECT_STATE, Const.STATE_BREAK);
                LogUtil.error("服务器断开");
                App.restartServerMessager();
            } else {
                String[] recvStrLQ = recvStr.split("L&Q");
                if (recvStrLQ.length > 1) {
                    for (String i : recvStrLQ) {
                        String[] strs = StringUtil.splitByHyphen(i);
                        order = Integer.parseInt(strs[0]);
                        content = strs[1];
                        if (order != 98) {//心跳不打印日志
                            LogUtil.debug("接收到(" + order + "):" + content);
                        }
                        AppMessager.send2Activity(order, content);
                    }
                } else {
                    String[] strs = StringUtil.splitByHyphen(recvStr);
                    order = Integer.parseInt(strs[0]);
                    content = strs[1];
                    if (order != 98) {//心跳不打印日志
                        LogUtil.debug("接收到(" + order + "):" + content);
                    }
                    AppMessager.send2Activity(order, content);
                }
            }
        } catch (Exception e) {
            LogUtil.info("接受消息错误，请联系管理员！");
            e.printStackTrace();
            TaskMsgCode taskMsgCode = new TaskMsgCode("1", "网络不稳定，接收任务不完整！");
            AppMessager.send2Server(Const.SEND_MSG_CODE, App.gson().toJson(taskMsgCode));
        }
    }

    public void send(int order, String jsonContent) {
        try {
            if (isConnected) {
                BufferedOutputStream mOut = new BufferedOutputStream(mSocket.getOutputStream());
                String content = order + "-" + jsonContent + Const.SPLIT;
                mOut.write(content.getBytes("utf-8"));
                mOut.flush();
                if (order != 99) {//心跳不打印日志
                    LogUtil.debug("发送(" + order + "):" + jsonContent);
                }
            }
        } catch (IOException e) {
            LogUtil.error("向服务器发送消息失败", e);
            e.printStackTrace();
        }
    }
}