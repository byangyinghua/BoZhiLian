package cn.com.data_plus.bozhilian.bean.send;

public class TaskState {
    private int TaskID;
    private String TeNum;//device ID
    private int ReciveState;//0-not 1-success
    private int DownloadState;//0-not download 1-request 2-success 3-failed
    private int ExecuteState;//0-not execute 1-executing 2-success 3-failed

    public TaskState(int taskID, String teNum, int receiveState, int downloadState, int executeState) {
        TaskID = taskID;
        TeNum = teNum;
        ReciveState = receiveState;
        DownloadState = downloadState;
        ExecuteState = executeState;
    }

    @Override
    public String toString() {
        return "TaskState{" +
                "TaskID=" + TaskID +
                ", TeNum='" + TeNum + '\'' +
                ", ReciveState=" + ReciveState +
                ", DownloadState=" + DownloadState +
                ", ExecuteState=" + ExecuteState +
                '}';
    }
}
