package cn.com.data_plus.bozhilian.bean.recv;

import java.util.List;

public class newTaskFileBean {
    public int TaskID;
    public String TaskName;
    public String TaskContent;
    public int TaskType;//0-audio 1-video 2-text
    public int TaskLevel;
    public int TaskPlayModel;//0-play once 1-loop
    public int TaskState;
    public String TaskCreatTime;
    public int IsDelete;
    public TaskTimeBean taskTime;
    public List<FileBean> File;

    public static class TaskTimeBean {
        public int TTimeID;
        public int TaskID;
        public int Model;
        public String week;

    }

    public static class FileBean {
        public int FileID;
        public String FileShowName;
        public String FileNam;
        public String FilePath;
        public int FileType;
        public String FileUpDateTime;
        public int IsDelete;
        public int OrderNum;
        public String FileLength;
    }
}
