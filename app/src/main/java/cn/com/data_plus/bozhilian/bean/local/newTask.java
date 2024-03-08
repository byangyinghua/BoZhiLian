package cn.com.data_plus.bozhilian.bean.local;

import com.ly723.db.interfaces.Column;
import com.ly723.db.interfaces.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.recv.newTaskBean;
import cn.com.data_plus.bozhilian.global.Config;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.TimeUtil;

@Table
public class newTask {
    @Column(isPrimaryKey = true)
    private String TaskID;
    @Column
    private String TaskName;
    @Column
    private String TaskDate;
    @Column
    private String TaskTime;
    @Column
    private String endTaskDate;
    @Column
    private String endTaskTime;
    @Column
    private String TaskPlaynumber;
    @Column
    private String TaskContent;
    @Column
    private String TaskType;//0-audio 1-video 2-text
    @Column
    private String TaskCreatTime;
    @Column
    private String TaskLevel;
    @Column
    private String TaskFileID;
    @Column
    private String TaskFileLength;
    @Column
    private String NetUsbType;

    public String getNetUsbType() {
        return NetUsbType;
    }

    public void setNetUsbType(String netUsbType) {
        NetUsbType = netUsbType;
    }

    public void setTaskID(String taskID) {
        TaskID = taskID;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public void setTaskDate(String taskDate) {
        TaskDate = taskDate;
    }

    public void setTaskTime(String taskTime) {
        TaskTime = taskTime;
    }

    public void setEndTaskDate(String endTaskDate) {
        this.endTaskDate = endTaskDate;
    }

    public void setEndTaskTime(String endTaskTime) {
        this.endTaskTime = endTaskTime;
    }

    public void setTaskPlaynumber(String taskPlaynumber) {
        TaskPlaynumber = taskPlaynumber;
    }

    public void setTaskContent(String taskContent) {
        TaskContent = taskContent;
    }

    public void setTaskType(String taskType) {
        TaskType = taskType;
    }

    public void setTaskCreatTime(String taskCreatTime) {
        TaskCreatTime = taskCreatTime;
    }

    public void setTaskLevel(String taskLevel) {
        TaskLevel = taskLevel;
    }

    public void setTaskFileID(String taskFileID) {
        TaskFileID = taskFileID;
    }

    public void setTaskFileLength(String taskFileLength) {
        TaskFileLength = taskFileLength;
    }

    public void setTaskPlayModel(String taskPlayModel) {
        TaskPlayModel = taskPlayModel;
    }

    public void setTaskState(String taskState) {
        TaskState = taskState;
    }

    public void setTaskPlaySate(String taskPlaySate) {
        TaskPlaySate = taskPlaySate;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTaskFileName(String taskFileName) {
        TaskFileName = taskFileName;
    }

    public void setAlarClock(String alarClock) {
        AlarClock = alarClock;
    }

    public void setFilePlayTime(String filePlayTime) {
        this.filePlayTime = filePlayTime;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTaskState() {
        return TaskState;
    }

    public String getTaskPlayModel() {
        return TaskPlayModel;
    }

    public String getTaskFileLength() {
        return TaskFileLength;
    }

    public String getTaskFileID() {
        return TaskFileID;
    }

    public String getTaskLevel() {
        return TaskLevel;
    }

    public String getTaskCreatTime() {
        return TaskCreatTime;
    }

    public String getTaskType() {
        return TaskType;
    }

    public String getTaskContent() {
        return TaskContent;
    }

    public String getTaskPlaynumber() {
        return TaskPlaynumber;
    }

    public String getTaskTime() {
        return TaskTime;
    }

    public String getTaskDate() {
        return TaskDate;
    }

    public String getTaskName() {
        return TaskName;
    }

    public String getTaskID() {
        return TaskID;
    }

    public String getUrl() {
        return url;
    }

    public String getTaskFileName() {
        return TaskFileName;
    }

    @Column
    private String TaskPlayModel;//0-play once 1-loop
    @Column
    private String TaskState;//2-执行   0-停止

    @Column
    private String TaskPlaySate;//1-正在执行   2-暂停   0-未播放
    @Column
    private String url;
    @Column
    private String TaskFileName;

    @Column
    private String AlarClock;//0有  1 没有

    @Column
    private String filePlayTime;

    public String getFilePlayTime() {
        return filePlayTime;
    }


    @Column
    private String day;

    public String getAlarClock() {
        return AlarClock;
    }

    @Override
    public String toString() {
        return "newTask{" +
                "TaskID='" + TaskID + '\'' +
                ", TaskName='" + TaskName + '\'' +
                ", TaskDate='" + TaskDate + '\'' +
                ", TaskTime='" + TaskTime + '\'' +
                ", endTaskDate='" + endTaskDate + '\'' +
                ", endTaskTime='" + endTaskTime + '\'' +
                ", TaskPlaynumber='" + TaskPlaynumber + '\'' +
                ", TaskContent='" + TaskContent + '\'' +
                ", TaskType='" + TaskType + '\'' +
                ", TaskCreatTime='" + TaskCreatTime + '\'' +
                ", TaskLevel='" + TaskLevel + '\'' +
                ", TaskFileID='" + TaskFileID + '\'' +
                ", TaskFileLength='" + TaskFileLength + '\'' +
                ", NetUsbType='" + NetUsbType + '\'' +
                ", TaskPlayModel='" + TaskPlayModel + '\'' +
                ", TaskState='" + TaskState + '\'' +
                ", TaskPlaySate='" + TaskPlaySate + '\'' +
                ", url='" + url + '\'' +
                ", TaskFileName='" + TaskFileName + '\'' +
                ", AlarClock='" + AlarClock + '\'' +
                ", filePlayTime='" + filePlayTime + '\'' +
                ", day='" + day + '\'' +
                '}';
    }

    //default constructor, don't delete
    public newTask() {
    }

    public String getEndTaskDate() {
        return endTaskDate;
    }

    public String getEndTaskTime() {
        return endTaskTime;
    }

    /**
     * @param taskJson
     * @param type     lai'zhi来自网络和usb的数据区分
     */
    public newTask(String taskJson) {
        JSONObject myJsonObject = null;
        try {
            //将字符串转换成jsonObject对象
            myJsonObject = new JSONObject(taskJson);

        } catch (JSONException e) {
            LogUtil.info(">>>>>>>>>>>>" + e.toString());
            String to = taskJson.substring(0, taskJson.length() - 1);
            try {
                //将字符串转换成jsonObject对象
                myJsonObject = new JSONObject(to + "\"}");

            } catch (JSONException es) {
                myJsonObject = null;
            }

        }
        if (myJsonObject == null)
            return;
        try {
            //获取对应的值
            TaskID = myJsonObject.optInt("taskid") + "";
            TaskName = myJsonObject.optString("taskname");
            TaskDate = myJsonObject.optString("begindate");
            TaskTime = myJsonObject.optString("begintime");
            endTaskDate = myJsonObject.optString("enddate");
            endTaskTime = myJsonObject.optString("endtime");

            TaskPlaynumber = myJsonObject.optInt("tasknumber") + "";
            // TaskPlaynumber  =3+"";
            TaskContent = myJsonObject.optString("taskcontent");
            TaskType = myJsonObject.optInt("tasktype") + "";
            TaskLevel = myJsonObject.optInt("tgrade") + "";
            TaskPlayModel = myJsonObject.optInt("taskplaymodel") + "";
            AlarClock = "1";
            TaskState = myJsonObject.optInt("taskstate") + "";
            TaskPlaySate = "0";
            TaskFileID = "";
            url = "";
            TaskFileLength = "";
            TaskFileName = "";
            filePlayTime = myJsonObject.optInt("filePlayTime") + "";
            if (myJsonObject.optString("day") == "" || myJsonObject.optString("day") == null) {
                day = "1,2,3,4,5,6,7";
            } else {
                day = myJsonObject.optString("day");
            }
            String taskFiles = myJsonObject.optString("taskFiles");

            if (taskFiles.equals("")) {
                TaskType = "2";
                endTaskDate = "2099-11-21";
                endTaskTime = "00:00:00";
            } else {
                JSONArray myJsonArray = new JSONArray(taskFiles.toString());
                for (int i = 0; i < myJsonArray.length(); i++) {
                    JSONObject myjObject = myJsonArray.getJSONObject(i);
                    TaskFileID += myjObject.optInt("fileid") + Const.SPLIT;
                    //String [] s = "http://localhost:8080/cms/upload/attachment/testv.mp4".split("localhost");
                    try {
                        String[] str = myjObject.optString("url").toString().split("localhost");
                        String s = str[1];
                        url += "http://" + Config.getInstance().getIP() + str[1] + Const.SPLIT;
                    } catch (Exception e) {
                        url += myjObject.optString("url") + Const.SPLIT;
                    }
                    String u = myjObject.optString("url").toString();
                    u = u.substring(u.lastIndexOf("."), u.length());
                    TaskFileLength += myjObject.optInt("filelength") + Const.SPLIT;
                    TaskFileName += myjObject.optString("filename") + Const.SPLIT;
                }
            }

        } catch (JSONException e) {
            LogUtil.info(">>>>>>>>>>>>" + e.toString());
            e.printStackTrace();
        }
    }


    public void updateAlarClock(String alarClock) {
        this.AlarClock = alarClock;
        App.newtaskDao().updateTask(TaskID, "AlarClock", alarClock);
    }

    public void updateTaskPlayState(String taskPlayState) {
        this.TaskState = taskPlayState;
        App.newtaskDao().updateTask(TaskID, "TaskPlaySate", taskPlayState);
    }

    public void updateTaskState(String taskState) {
        this.TaskState = taskState;
        App.newtaskDao().updateTask(TaskID, "TaskState", taskState);
    }

    public String getTaskPlaySate() {
        return TaskPlaySate;
    }

    public String getDay() {
        return day;
    }
}
