package cn.com.data_plus.bozhilian.bean.local;

/**
 * Created by Administrator on 2018/7/5 0005.
 */

public class TaskMsgCode {
    /**
     * code : 1
     * msg :
     */

    private String code;
    private String msg;

      public TaskMsgCode(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
