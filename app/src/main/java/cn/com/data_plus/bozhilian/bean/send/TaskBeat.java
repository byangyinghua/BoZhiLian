package cn.com.data_plus.bozhilian.bean.send;

public class TaskBeat {
    private String TaskId;


    public TaskBeat(String TaskId) {
        this.TaskId = TaskId;
    }

    @Override
    public String toString() {
        return "TaskBeat" + '\n' +
                "--------------------" + '\n' +
                "TaskId=" + TaskId + '\n' +
                "--------------------";
    }

}
