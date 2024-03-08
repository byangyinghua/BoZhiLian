package cn.com.data_plus.bozhilian.thread;

import java.io.File;
import java.util.ArrayList;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.global.newAlarm;
import cn.com.data_plus.bozhilian.interfaces.StateListener;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.TimeUtil;

//下载管理
public class newDownloader implements StateListener {
    private static newDownloader sDownloader;
    private newDownloadAsync downloadAsync;

    private newDownloader() {
        mTasks = new ArrayList<>();
    }

    public static newDownloader getInstance() {
        if (sDownloader == null) {
            sDownloader = new newDownloader();
        }
        return sDownloader;
    }

    private ArrayList<newTask> mTasks;
    private newTask mCurrTask;
    String[] szId;

    //处理下载任务，有三条分支，一个是文本，一个是音视频存在，一个是文件不存在需要下载（即在下载完成后再处理任务）
    public void download(newTask task) {

        App.newTaskDownloader = task;
        SuccessCount = 0;
        //0音频，1视频 需要下载
        if (mCurrTask == null) {
            mTasks.add(task);
            //没有下载中的任务
            mCurrTask = task;
        } else {

            if (downloadAsync != null) {
                downloadAsync.exit();
                downloadAsync = null;
            }
        }
        szId = mCurrTask.getTaskFileID().split(Const.SPLIT);
        String[] szUrl = mCurrTask.getUrl().split(Const.SPLIT);
        String[] szLenght = mCurrTask.getTaskFileLength().split(Const.SPLIT);
        String[] szName = mCurrTask.getTaskFileName().split(Const.SPLIT);
        for (int i = 0; i < szId.length; i++) {
            File file = FileUtil.getFile(FileUtil.SUB_DIR_MEDIA, szName[i]);
            long totalLength = Long.parseLong(szLenght[i]);

            if (totalLength == 0) {
                onFailed();
                return;
            }

            if (file.length() != totalLength) {
                //不完整，需要重新下载
                if (file.delete()) {
                    //请求下载
                    downloadAsync = new newDownloadAsync(mCurrTask, szUrl[i], szLenght[i], szName[i], sDownloader);
                    downloadAsync.execute();
                }
            } else {
                //文件存在且完整
                onSuccess();
            }
        }

    }

    private void handleNext() {
        TimeUtil.delayExe(500);
        mTasks.remove(mCurrTask);
        mCurrTask = null;
        if (mTasks.size() != 0) {
            download(mTasks.get(0));
        } else {

        }
    }

    @Override
    public void onStart() {
        LogUtil.debug("下载开始");
        String str = "任务("
                + mCurrTask.getTaskName()
                + ")文件<"
                + mCurrTask.getTaskFileName()
                + ">正在传输中";
        AppMessager.resetProgress(str);
    }

    int SuccessCount = 0;

    @Override
    public void onSuccess() {
        LogUtil.info("下载成功");
        AppMessager.showProgress(100);
        SuccessCount++;
        if (SuccessCount == szId.length) {
            newAlarm.getInstance().setTaskAlarm(App.newTaskDownloader);
            //AppMessager.sendTaskState(Integer.parseInt( mCurrTask.getTaskID()),0,0);
        }
        handleNext();
    }

    @Override
    public void onFailed() {
        App.newTaskDownloader = null;
        LogUtil.info("下载失败");
        AppMessager.showProgress(100);
        LogUtil.debug("下载失败");
        //AppMessager.sendTaskState(Integer.parseInt( mCurrTask.getTaskID()),0,1);
        handleNext();
    }


}
