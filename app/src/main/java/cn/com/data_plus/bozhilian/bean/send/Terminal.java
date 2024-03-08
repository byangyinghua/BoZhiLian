package cn.com.data_plus.bozhilian.bean.send;

public class Terminal {

    private String TeServerIP;//ip:port
    private String TeName;
    private String TeNum;
    private String TeAddr;
    private String TeIP;
    private String Turn;
    private String Off;
    private String weekArrays;
    public Terminal(String teName, String teNum, String teAddr, String teServerIP,String TeIP) {
        this.TeName = teName;
        this.TeNum = teNum;
        this.TeAddr = teAddr;
        this.TeServerIP = teServerIP;
        this.TeIP =TeIP;
    }


    public Terminal(String teName, String teNum, String teAddr, String teServerIP,String TeIP,String Turn,String Off,String weekArrays) {
        this.TeName = teName;
        this.TeNum = teNum;
        this.TeAddr = teAddr;
        this.TeServerIP = teServerIP;
        this.TeIP =TeIP;
        this.Turn= Turn;
        this.Off = Off;
        this.weekArrays = weekArrays;
    }
    @Override
    public String toString() {
        return "Terminal{" +
                ", TeServerIP='" + TeServerIP + '\'' +
                ", TeName='" + TeName + '\'' +
                ", TeNum='" + TeNum + '\'' +
                ", TeAddr='" + TeAddr + '\'' +
                '}';
    }
}
