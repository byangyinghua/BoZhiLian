package cn.com.data_plus.bozhilian.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.bean.send.TaskBeat;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.global.newAlarm;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;
import cn.com.data_plus.bozhilian.util.TimeUtil;

public class VideoFragment extends BaseFragment
        implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {

    private final static String TASK_JSON = "task_json";

    private newTask mTask;
    private String[] mPath;
    String[] szName;
    int videoCount = 0;
    int count = 0;
    private boolean mLooping;
    //private int mPos;
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;

    public static VideoFragment newInstance(String taskJson) {
        VideoFragment videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TASK_JSON, taskJson);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout rootView = new RelativeLayout(mActivity);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setBackgroundColor(Color.BLACK);
        rootView.setGravity(Gravity.CENTER);
        mSurfaceView = new SurfaceView(mActivity);
        mSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.addView(mSurfaceView);
        mSurfaceView.getHolder().addCallback(this);
        return rootView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.info("surfaceCreated");
        String taskJson = getArguments().getString(TASK_JSON);
        mTask = App.gson().fromJson(taskJson, newTask.class);

        if (!TextUtils.isEmpty(taskJson)) {
            String[] szUrl = new String[]{};
            if (mTask.getUrl().contains(Const.SPLIT)) {
                szUrl = mTask.getUrl().split(Const.SPLIT);
            }
            mPath = new String[szUrl.length];
            //    String[] szLenght = mTask.getTaskFileLength().split(Const.SPLIT);
            szName = mTask.getTaskFileName().split(Const.SPLIT);
            if (mTask.getNetUsbType().equals(picFragment.NET_DATA)) {//网络视频
                for (int i = 0; i < szUrl.length; i++) {
                    mPath[i] = FileUtil.getFile(FileUtil.SUB_DIR_MEDIA, szName[i]).getAbsolutePath();
                }
            } else if (mTask.getNetUsbType().equals(picFragment.USB_DATA)) {//usb视频任务
                mPath = mTask.getUrl().split(Const.SPLIT);
            }
            if (mTask.getTaskPlayModel() != null)
                mLooping = Integer.parseInt(mTask.getTaskPlayModel()) == 1;
            count = Integer.parseInt(mTask.getTaskPlaynumber());
        }
        //初始化MediaPlayer
        if (mMediaPlayer != null) {
            return;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(mSurfaceView.getHolder());
        try {

            mMediaPlayer.setDataSource(mPath[videoCount]);//此路径必须是绝对路径
            //开始加载，加载好之后才能播放，对应preparedListener
            mMediaPlayer.prepareAsync();
            HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, "当前播放的任务：" + mTask.getTaskName() + "!当前当前任务文件：" + szName[videoCount], App.getCurrent() + "");
            AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.error("视频格式错误，2秒后自动退出");
            TimeUtil.delayExe(2000);
            mActivity.closeFragment();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setScreenOnWhilePlaying(true);//播放时屏幕常亮

        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);//视频准备监听
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (count == 1 && mPath.length == 1) {
                    LogUtil.info("执行完毕");
                    onStop();
//                    mActivity.closeFragment();
//                    mActivity.hideBottomText();
                    AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");

                    List<newTask> s = new ArrayList<newTask>();
                    if (App.newTasks.size() == 1) {
                        App.newTasks = new ArrayList<newTask>();
                    }
                    if (App.newTasks != null) {
                        for (newTask n : App.newTasks) {
                            if (n.getTaskID().equals(mTask.getTaskID())) {
                                s.add(n);
                            }
                        }
                        if (s != null) {
                            for (newTask n : s) {
                                App.newTasks.remove(n);
                            }
                        }

                        LogUtil.debug("执行完毕，任务队列还有" + App.newTasks.size() + "个任务");
                        if (App.newTasks.size() > 0) {
                            newAlarm.getInstance().startTasks(App.newTasks.get(0));

                        }
                    }

                } else {
                    if (mPath.length == videoCount + 1) {
                        count--;
                        videoCount = 0;
                    } else {
                        videoCount++;
                    }

                    LogUtil.info("当前有" + mPath.length + "个视频，这是第" + (videoCount + 1) + "个，还需播放" + (count) + "次");
                    if (count == 0) {
                        LogUtil.info("执行完毕");
                        onStop();
                        AppMessager.send2Activity(Const.MSG_CLOSE_FRAGMENT, "");
                        List<newTask> s = new ArrayList<newTask>();
                        if (App.newTasks.size() == 1) {
                            App.newTasks = new ArrayList<newTask>();
                        }
                        if (App.newTasks != null) {
                            for (newTask n : App.newTasks) {
                                if (n.getTaskID().equals(mTask.getTaskID())) {
                                    s.add(n);
                                }
                            }
                            if (s != null) {
                                for (newTask n : s) {
                                    App.newTasks.remove(n);
                                }
                            }

                            LogUtil.debug("执行完毕，任务队列还有" + App.newTasks.size() + "个任务");
                            if (App.newTasks.size() > 0) {
                                newAlarm.getInstance().startTasks(App.newTasks.get(0));

                            }
                        }
                    } else {
                        mp.reset();
                        try {
                            if (mPath.length == 1) {
                                videoCount = 0;
                            }

                            mp.setDataSource(mPath[videoCount]);
                            mp.prepare();
                            mp.start();
                            HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, "当前播放的任务：" + mTask.getTaskName() + "!当前当前任务文件：" + szName[videoCount], App.getCurrent() + "");
                            AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
                            if (mPath.length == videoCount) {
                                videoCount = 0;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtil.error("视频格式错误，2秒后自动退出");
                            TimeUtil.delayExe(2000);
                            List<newTask> s = new ArrayList<newTask>();
                            if (App.newTasks.size() == 1) {
                                App.newTasks = new ArrayList<newTask>();
                            }

                            if (App.newTasks != null) {
                                for (newTask n : App.newTasks) {
                                    if (n.getTaskID().equals(mTask.getTaskID())) {
                                        s.add(n);
                                    }
                                }
                                if (s != null) {
                                    for (newTask n : s) {
                                        App.newTasks.remove(n);
                                    }
                                }

                                LogUtil.debug("执行完毕，任务队列还有" + App.newTasks.size() + "个任务");
                                if (App.newTasks.size() > 0) {
                                    newAlarm.getInstance().startTasks(App.newTasks.get(0));

                                }
                            }


                            mActivity.closeFragment();
                        }
                    }
                }

            }
        });//视频结束播放监听
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.info("开始执行");// LogUtil.info("surfaceChanged");\

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.info("执行结束");//LogUtil.info("surfaceDestroyed");
        App.TaskName = "空闲";
        if (App.newTasks == null) {
            TaskBeat heartBeat1 = new TaskBeat(mTask.getTaskID());
            AppMessager.send2Server(Const.SEND_Stop_Task, App.gson().toJson(heartBeat1));
            HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, App.getCurrent() + "");
            AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
        } else {
            TaskBeat heartBeat1 = new TaskBeat(mTask.getTaskID());
            AppMessager.send2Server(Const.SEND_Stop_Task, App.gson().toJson(heartBeat1));
        }
        release();

    }

    @Override
    public void onStop() {
        super.onStop();
        release();
    }

    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();//释放
            mMediaPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mActivity.closeFragment();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();//播放
        //mMediaPlayer.seekTo(mPos);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        //获取屏幕宽高
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        int orientation = getResources().getConfiguration().orientation;

        if (width > height) {//如果宽度大于高度，说明横屏合适
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
                params.width = screenWidth;
                params.height = (int) (screenWidth * height / (width * 1.0));
                mSurfaceView.setLayoutParams(params);
            }
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
                params.width = (int) (screenHeight * width / (height * 1.0));
                params.height = screenHeight;
                mSurfaceView.setLayoutParams(params);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        release();
    }
}
