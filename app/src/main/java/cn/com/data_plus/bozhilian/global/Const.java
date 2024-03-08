package cn.com.data_plus.bozhilian.global;

@SuppressWarnings("ALL")
public class Const {
    //socket
    public static final int HERAT_BEAT_TIME = 30000;
    //获取时间定时器
    public static final int HERAT_GET_TIME = 300000;
    //server socket
    public static final int SERVER_POST = 8888;

    public static final String SPLIT = "L&Q";

    public static final String DEFAULT_IP = "192.168.1.";
    public static final int DEFAULT_PORT = 8888;

    //-----------------flag and order----------------//
    //message order
    public static final int MSG_CONNECT_FAILED_BY_REMOVE_FROM_SERVER = -1;
    public static final int MSG_TOAST = -2;
    public static final int MSG_PLAY_VIDEO = -3;
    public static final int MSG_PLAY_PIC= -4;
    public static final int MSG_CLOSE_FRAGMENT = -5;
    public static final int MSG_NOTICE_NETWORK_STATE = -6;
    public static final int MSG_NOTICE_CONNECT_STATE = -7;
    public static final int MSG_SHOW_TEXT = -8;
    public static final int MSG_SHOW_TOP_TEXT = -9;
    public static final int MSG_SHOW_LARGE_TEXT = -10;
    public static final int MSG_RESET_PROGRESS = -11;
    public static final int MSG_SHOW_PROGRESS = -12;
    public static final int MSG_CLOSE_ACTIVITY = -13;
    public static final int MSG_SHOW_BOTTOM_TEXT = -14;
    public static final int MSG_HIDE_BOTTOM_TEXT = -15;
    public static final int MSG_resetApp = -16;
    public static final int MSG_PLAYPIC = -17;
    public static final int MSG_LOOPTASKList = -18;

    public static final int hideBottomUIMenu = -26;
    public static final int MSG_STOP_SERVER = -27;
    public static final int MSG_ERROR_BOTTON = 111;

    //receive order 接收命令
    public static final int RECV_CONNECT = 101;//接收到连接请求确认
    public static final int RECV_TASK = 102;//接收到任务
    public static final int RECV_RECOVERTASK = 112;//恢复任务
    public static final int RECV_DELETE_TASK = 103;//接收到任务删除指令
    public static final int RECV_SET_TIME = 104;//时间设置
    public static final int RECV_VIDEO_STREAM = 105;//视频流
    public static final int RECV_BROADCAST = 106;//语音广播
    public static final int RECV_PlayMusic = 116;//实时广播
    public static final int RECV_BOOT = 107;//开关机
    public static final int RECV_BOOT_DELETE = 1070;//删除开关机
    public static final int RECV_ShieldSignal = 108;//屏蔽开关
    public static final int RECV_DELETE_FILE = 109;//删除文件
    public static final int RECV_AlarmVideo = 110;//告警开关
    public static final int RECV_Start_BroadcastLive = 111;//接收到实时广播
    public static final int RECV_Stop_BroadcastLive = 112;//停止实时广播
    public static final int RECV_down_Music= 188;//减小音量
    public static final int RECV_up_Music= 189;//增加音量
    public static final int RECV_GetVolume= 180;//获得音量
    public static final int RECV_setVolume= 185;//获得音量
    public static final int RECV_updateApp= 190;//更新
    public static final int RECV_deleteAllTask= 333;//删除现在全部任务队列
    public static final int RECV_deleteAllTaskInDatebase= 334;//删除数据库
    public static final int RECV_PlayVideoPashUrl= 999;//播放流地址
    public static final int RECV_STOPPlayVideoPashUrl= 998;//停止播放流地址

    //send order   发送命令
    public static final int SEND_CONNECT = 1;//确认连接
    public static final int SEND_TASK_ORDER = 2;//确认任务接收+
    public static final int SEND_TASK_DELETE = 3;//确认任务删除
    public static final int SEND_SET_TIME = 4;//时间设置
    public static final int SEND_VIDEO_STREAM = 5;//视频流
    public static final int SEND_BROADCAST = 6;//语音广播
    public static final int SEND_BOOT = 7;//开关机
    public static final int SEND_DOWNLOAD = 98;//服务器检查心跳
    public static final int SEND_HEART_BEAT = 99;//发送心跳
    public static final int SEND_AlarmVideo = 120;//发送告警信息
    public static final int SEND_Stop_Task = 130;//任务结束提示
    public static final int SEND_SetVolume = 185;//发送音量信息
    public static final int SEND_stop_PlayMusic = 186;//播放完成实时广播
    public static final int SEND_MSG_CODE = 155;//接受任务失败返回

    //-----------------flag and order----------------//
    public static final int STATE_DOWNLOAD_DEFAULT = 0;
    public static final int STATE_DOWNLOAD_BEGIN = 1;
    public static final int STATE_DOWNLOAD_FINISH = 2;
    public static final int STATE_DOWNLOAD_FAILED = 3;

    public static final int STATE_EXECUTE_DEFAULT = 0;
    public static final int STATE_EXECUTE_BEGIN = 1;
    public static final int STATE_EXECUTE_FINISH = 2;
    public static final int STATE_EXECUTE_FAILED = 3;
    public static final int STATE_EXECUTE_WAITING = 4;

    //network and network state
    public static final String STATE_NORMAL = "normal";
    public static final String STATE_BREAK = "break";

    //video stream state
    public static int STATE_VIDEO_STREAM_DEFALUT = 0;
    public static int STATE_VIDEO_STREAM_SUCCESS = 1;
    public static int STATE_VIDEO_STREAM_FAILED = 2;
}