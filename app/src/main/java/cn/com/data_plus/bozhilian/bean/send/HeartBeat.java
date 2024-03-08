package cn.com.data_plus.bozhilian.bean.send;

public class HeartBeat {
    private String num;
    private String serverIP;//ip:port
    private String taskContent;
    private String terminalVolume;

    public HeartBeat(String num, String serverIP, String taskContent,String terminalVolume) {
        this.num = num;
        this.serverIP = serverIP;
        this.taskContent = taskContent;
        this.terminalVolume = terminalVolume;
    }

    @Override
    public String toString() {
        return "HeartBeat" + '\n' +
                "--------------------" + '\n' +
                "num=" + num + '\n' +
                "serverIP=" + serverIP + '\n' +
                "taskContent=" + taskContent + '\n' +
                "--------------------";
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }
}
