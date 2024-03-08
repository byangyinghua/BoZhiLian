package cn.com.data_plus.bozhilian.database;

import android.support.annotation.Nullable;
import android.util.Log;

import com.ly723.db.helper.DbSQLite;
import com.ly723.db.helper.GenericDao;

import java.util.List;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.util.TimeUtil;

public class newTaskDao extends GenericDao<newTask> {
    public newTaskDao(DbSQLite db) {
        super(db, newTask.class);
    }

    @Override
    public long insertOrReplace(newTask task) {
        delete(task);
        return super.insertOrReplace(task);
    }

    @Nullable
    public List<newTask> queryTasks() {
        return query("TaskState =? and AlarClock=?", new String[]{"2", "1"});
    }

    @Nullable
    public List<newTask> queryTasksByTaskPlaySate() {
        return query("TaskPlaySate =? ", new String[]{"0"});
    }


    @Nullable
    public List<newTask> queryTodayRepeatTasks() {
        return query(null, "mode=0 and endTime>?", new String[]{TimeUtil.getTime()}, " beginTime asc");
    }

    //Query all the tasks in the database
    @Nullable
    public List<newTask> queryAllTasks() {
        return queryAll();
    }

    //delete task by id
    public void delete(newTask task) {
        mDb.getSQLiteDatabase().execSQL("delete from newTask where  TaskID='" + task.getTaskID() + "'");
    }

    //delete task by id
    public void deleteAllInnewTask() {
        mDb.getSQLiteDatabase().delete("newTask", "", null);
    }

    public void updateTask(String taskID, String columnName, String value) {
        mDb.getSQLiteDatabase().execSQL("UPDATE newTask SET " + columnName + " = '" + value + "' WHERE TaskID ='" + taskID + "' ");
    }

    public void updateTask(int taskID, String columnName, int value) {
        mDb.getSQLiteDatabase().execSQL("UPDATE columnName SET " + columnName + " = " + value + " WHERE id ='" + taskID + "' ");
    }

    @Nullable
    public newTask exist(newTask task) {
        return queryFirstRecord("TaskID = " + task.getTaskID());
    }

    @Nullable
    public newTask existByFileName(String fileName) {
        return queryFirstRecord("fileName = '" + fileName + "'");
    }

    private int convertTime2Value(String time) {
        return Integer.parseInt(time.replaceAll(":", ""));
    }
}
