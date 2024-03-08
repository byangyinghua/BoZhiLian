package cn.com.data_plus.bozhilian.bean.send;

public class VideoStream {
    private String teNum;//device ID
    private int state;//0-request 1-success 2-failed

    public VideoStream(String teNum, int state) {
        this.teNum = teNum;
        this.state = state;
    }

    @Override
    public String toString() {
        return "VideoStream" + '\n' +
                "--------------------" + '\n' +
                "state=" + state + '\n' +
                "terminalNum=" + teNum + '\n' +
                "--------------------";
    }
}
