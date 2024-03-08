package cn.com.data_plus.bozhilian.bean.send;

public class Download {
    private String teNum;
    private int taskID;

    public Download(String teNum, int taskID) {
        this.teNum = teNum;
        this.taskID = taskID;
    }

    @Override
    public String toString() {
        return "Download" + '\n' +
                "--------------------------------------" + '\n' +
                "teNum=" + teNum + '\n' +
                "taskID=" + taskID + '\n' +
                "--------------------------------------";
    }
}
