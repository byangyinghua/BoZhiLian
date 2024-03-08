package cn.com.data_plus.bozhilian.global;

import android.media.MediaPlayer;
import android.util.SparseArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.bean.send.TaskBeat;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;

public class newAudio {

    private SparseArray<MediaPlayer> players;
    int videoCount = 0;
    int count = 0;
    String[] mPath;
    String[] szName;

    public void startPlayMusic(final newTask task) {
        try {
            MediaPlayer oldPlayer = players.get(Integer.parseInt(task.getTaskID()));
            if (oldPlayer != null) {
                oldPlayer.stop();
            }
            count = Integer.parseInt(task.getTaskPlaynumber());
            String[] szUrl = task.getUrl().split(Const.SPLIT);
            mPath = new String[szUrl.length];
            szName = task.getTaskFileName().split(Const.SPLIT);
            if (task.getNetUsbType().equals("usb")) {
                mPath = szUrl;
            } else {
                for (int i = 0; i < szUrl.length; i++) {
                    mPath[i] = FileUtil.getFile(FileUtil.SUB_DIR_MEDIA, szName[i]).getAbsolutePath();
                }
            }
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(mPath[videoCount]);
            mediaPlayer.prepare();
            mediaPlayer.start();
            App.TaskName = task.getTaskName();
            HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, "当前播放的任务：" + task.getTaskName() + "!当前当前任务文件：" + szName[videoCount], App.getCurrent() + "");
            AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));

            AppMessager.send2Activity(Const.MSG_SHOW_BOTTOM_TEXT, task.getTaskContent());

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    if (count == 1 && mPath.length == 1) {
                        LogUtil.info("执行完毕");
                        stopPlayingMusic(task);

                        List<newTask> s = new ArrayList<newTask>();
                        if (App.newTasks.size() == 1) {
                            App.newTasks = new ArrayList<newTask>();
                        }
                        if (App.newTasks != null) {
                            for (newTask n : App.newTasks) {
                                if (n.getTaskID().equals(task.getTaskID())) {
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

                        LogUtil.info("当前有" + mPath.length + "个音频，这是第" + (videoCount + 1) + "个，还需播放" + (count) + "次");
                        if (count == 0) {
                            LogUtil.info("执行完毕");
                            stopPlayingMusic(task);
                            List<newTask> s = new ArrayList<newTask>();
                            if (App.newTasks.size() == 1) {
                                App.newTasks = new ArrayList<newTask>();
                            }
                            if (App.newTasks != null) {
                                for (newTask n : App.newTasks) {
                                    if (n.getTaskID().equals(task.getTaskID())) {
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
                            try {
                                if (mPath.length == 1) {
                                    videoCount = 0;
                                }
                                mp.reset();
                                mp.setDataSource(mPath[videoCount]);
                                mp.prepare();
                                mp.start();
                                HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, "当前播放的任务：" + task.getTaskName() + "!当前当前任务文件：" + szName[videoCount], App.getCurrent() + "");
                                AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
                                if (mPath.length == videoCount) {
                                    videoCount = 0;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                LogUtil.error("音频播放错误，已关闭！" + e);
                                stopPlayingMusic(task);
                                List<newTask> s = new ArrayList<newTask>();
                                if (App.newTasks.size() == 1) {
                                    App.newTasks = new ArrayList<newTask>();
                                }
                                if (App.newTasks != null) {
                                    for (newTask n : App.newTasks) {
                                        if (n.getTaskID().equals(task.getTaskID())) {
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
                            }
                        }
                    }

                    LogUtil.debug(task.getTaskID() + " onCompletion: ");
                }
            });
            players.put(Integer.parseInt(task.getTaskID()), mediaPlayer);


        } catch (Exception e) {
            LogUtil.error("播放歌曲".concat(task.getTaskFileName()).concat("时出错"));
        }
    }

    void stopPlayingMusic(newTask task) {
        App.TaskName = "空闲";
        if (App.newTasks == null) {
            TaskBeat heartBeat1 = new TaskBeat(task.getTaskID());
            AppMessager.send2Server(Const.SEND_Stop_Task, App.gson().toJson(heartBeat1));
            HeartBeat heartBeat = new HeartBeat(App.deviceID, App.serverAddr, App.TaskName, App.getCurrent() + "");
            AppMessager.send2Server(Const.SEND_HEART_BEAT, App.gson().toJson(heartBeat));
        } else {
            TaskBeat heartBeat1 = new TaskBeat(task.getTaskID());
            AppMessager.send2Server(Const.SEND_Stop_Task, App.gson().toJson(heartBeat1));
        }

        AppMessager.send2Activity(Const.MSG_HIDE_BOTTOM_TEXT, "");


        if (players != null) {
            MediaPlayer mediaPlayer = players.get(Integer.parseInt(task.getTaskID()));
            if (mediaPlayer != null) {
                // task.updatePlayPosition(mediaPlayer.getCurrentPosition());
                mediaPlayer.stop();
                mediaPlayer.release();
                players.delete(Integer.parseInt(task.getTaskID()));
            }
        }
    }

    private static newAudio sAudioPlay;

    private newAudio() {
        players = new SparseArray<>();
    }

    public static newAudio getInstance() {
        if (sAudioPlay == null) {
            sAudioPlay = new newAudio();
        }
        return sAudioPlay;
    }
}
