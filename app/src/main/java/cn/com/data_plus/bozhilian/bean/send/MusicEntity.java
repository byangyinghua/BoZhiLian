package cn.com.data_plus.bozhilian.bean.send;

public class MusicEntity {
    private String orderId;


    public MusicEntity(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "MusicEntity" + '\n' +
                "--------------------" + '\n' +
                "orderId=" + orderId + '\n' +
                "--------------------";
    }

}
