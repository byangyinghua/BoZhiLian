package cn.com.data_plus.bozhilian.bean.recv;

import java.util.List;

public class newTaskBean {
//    任务id TaskID
//    任务名 TaskName
//    任务开始日期 TaskDate
//    任务开始时间 TaskTime
//    任务执行次数 TaskPlaynumber
//    任务显示内容 TaskContent
//    任务类型 TaskType
//    任务级别 TaskLevel
//    任务文件id TaskFileID
//    任务文件名 TaskFileName
//    任务文件长度 TaskFileLength
//    任务执行模式  TaskPlayModel
    public int taskid;
    public String taskname;
    public String begindate;
    public String begintime;
    public int tasknumber;
    public String taskcontent;
    public int tasktype;//0-audio 1-video 2-text
    public int tgrade;
    public int taskplaymodel;//0-play once 1-loop
    public int taskstate;
    public List<taskFiles> TaskFile ;
    public List<taskTime> taskTimes ;
    public String msg_flag;
    public String AlarClock;
    public String filePlayTime;
    public String day;


    public static class taskFiles {
        public int fileid;

        public String filename;

        public int filelength;

        public String url;

    }
    public static class taskTime {

    }
}
