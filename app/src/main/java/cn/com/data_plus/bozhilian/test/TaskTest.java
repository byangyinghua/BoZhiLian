package cn.com.data_plus.bozhilian.test;

import java.util.ArrayList;
import java.util.List;

import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.recv.newTaskBean;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.util.TimeUtil;

public class TaskTest {
//    /**
//     * @param mode 0 每天 1 自定义时间
//     */
//    public static String getAudioJson(int taskID, String beginHour, String endhour, int mode) {
//        TaskBean taskBean = new TaskBean();
//        taskBean.TaskID = taskID;
//        taskBean.TaskName = "音乐" + taskID;
//        taskBean.TaskType = 0;
//        taskBean.TaskPlayModel = 1;
//
//        TaskBean.TaskTimeBean taskTimeBean = new TaskBean.TaskTimeBean();
//        taskTimeBean.BeginTime = beginHour + ":00";
//        taskTimeBean.EndTime = endhour + ":30";
//        taskTimeBean.BeginDate = TimeUtil.getDate();
//        taskTimeBean.Model = mode;
//        taskBean.taskTime = taskTimeBean;
//
//        List<TaskBean.FileBean> fileBeanList = new ArrayList<>();
//        TaskBean.FileBean fileBean = new TaskBean.FileBean();
//        fileBean.FileNam = "再别康桥-S.H.E.mp3";
//        fileBean.FileLength = "100";
//        fileBeanList.add(fileBean);
//        taskBean.File = fileBeanList;
//
//        return App.gson().toJson(taskBean);
//    }
//
//    /**
//     * @param mode 0 每天 1 自定义时间
//     */
//    public static String getVideoJson(int taskID, String beginHour, String endHour, int mode) {
//        TaskBean taskBean = new TaskBean();
//        taskBean.TaskID = taskID;
//        taskBean.TaskName = "视频";
//        taskBean.TaskType = 1;
//        taskBean.TaskPlayModel = 1;
//
//        TaskBean.TaskTimeBean taskTimeBean = new TaskBean.TaskTimeBean();
//        taskTimeBean.BeginTime = beginHour + ":00";
//        taskTimeBean.EndTime = endHour + ":30";
//        taskTimeBean.BeginDate = TimeUtil.getDate();
//        taskTimeBean.Model = mode;
//        taskBean.taskTime = taskTimeBean;
//
//        List<TaskBean.FileBean> fileBeanList = new ArrayList<>();
//        TaskBean.FileBean fileBean = new TaskBean.FileBean();
//        fileBean.FileNam = "TARA-day by day.mp4";
//        fileBean.FileLength = "100";
//        fileBeanList.add(fileBean);
//        taskBean.File = fileBeanList;
//
//        return App.gson().toJson(taskBean);
//    }
//    public static String getAlarmVideoJson(int taskID, String beginHour, String endHour, int mode ) {
//        TaskBean taskBean = new TaskBean();
//        taskBean.TaskID = taskID;
//        taskBean.TaskName = "紧急告警";
//        taskBean.TaskContent = "                                                                                                                                                                                                         紧急告警";
//        taskBean.TaskType = 1;
//        taskBean.TaskPlayModel = 1;
//
//        TaskBean.TaskTimeBean taskTimeBean = new TaskBean.TaskTimeBean();
//        taskTimeBean.BeginTime = beginHour ;
//        taskTimeBean.EndTime = endHour ;
//        taskTimeBean.BeginDate = TimeUtil.getDate();
//        taskTimeBean.Model = mode;
//        taskBean.taskTime = taskTimeBean;
//
//        List<TaskBean.FileBean> fileBeanList = new ArrayList<>();
//        TaskBean.FileBean fileBean = new TaskBean.FileBean();
//        fileBean.FileNam = "警报灯.mp4";
//        fileBean.FileLength = "100";
//        fileBeanList.add(fileBean);
//        taskBean.File = fileBeanList;
//
//        return App.gson().toJson(taskBean);
//    }

    public static String getVideoJsons() {
        newTaskBean taskBean = new newTaskBean();
        taskBean.taskid = 1;
        taskBean.taskname = "紧急告警";
        taskBean.taskcontent = "123";
        taskBean.tasktype = 1;
        taskBean.begindate = TimeUtil.getDate();
        taskBean.begintime = TimeUtil.getTime();
        taskBean.taskplaymodel = 2;
        taskBean.tgrade = 1;
        taskBean.taskstate = 2;
        taskBean.taskplaymodel = 1;
        taskBean.AlarClock = "1";
        List<newTaskBean.taskFiles> fileBeanList = new ArrayList<>();
        newTaskBean.taskFiles fileBean = new newTaskBean.taskFiles();
        fileBean.fileid = 12;
        fileBean.filename = "任务文件.mp4";
        fileBean.filelength = 111;
        fileBean.url = "http: //192.168.0.25: 8080/File/socket.mp4";
        fileBeanList.add(fileBean);

        taskBean.TaskFile = fileBeanList;

        return App.gson().toJson(taskBean);
    }

    /**
     * @param mode 0 每天 1 自定义时间
     */
//    public static String getTextJson(int taskID, String beginHour, String endHour, int mode) {
//        TaskBean taskBean = new TaskBean();
//        taskBean.TaskID = taskID;
//        taskBean.TaskName = "文本" + taskID;
//        taskBean.TaskType = 2;
//        taskBean.TaskContent = App.gainString(R.string.test_text);
//
//        TaskBean.TaskTimeBean taskTimeBean = new TaskBean.TaskTimeBean();
//        taskTimeBean.BeginTime = beginHour + ":00";
//        taskTimeBean.EndTime = endHour + ":30";
//        taskTimeBean.BeginDate = TimeUtil.getDate();
//        taskTimeBean.Model = mode;
//        taskBean.taskTime = taskTimeBean;
//
//        return App.gson().toJson(taskBean);
//    }
    public static String al(String Messages) {
        return "{\"taskid\":0,\"taskname\":\"警报\",\"tasktype\":\"1\",\"tgrade\":0.1,\"taskplaymodel\":0,\"taskstate\":1,\"tasknumber\":99,\"begintime\":\"" + TimeUtil.getTime() + "\",\"begindate\":\"" + TimeUtil.getDate() + "\",\"endtime\":\"00:00:00\",\"enddate\":\"2099-08-23\",\"taskcontent\":\"" + Messages + Const.SPLIT + "" + "\",\"day\":\"1,2,3,4,5,6,7\",\"taskTime\":{},\"taskFiles\":[{\"fileid\":0,\"filename\":\"警报灯.mp4\",\"filelength\":1428141,\"url\":\"http://localhost:8080/cms/upload/attachment/3275a539ab9647e0b5fe3785e390065320170813180120.mp4\"}],\"msg_flag\":\"stopTask\"}";
    }
}
