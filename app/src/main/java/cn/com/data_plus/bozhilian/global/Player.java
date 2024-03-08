package cn.com.data_plus.bozhilian.global;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.SparseArray;
import android.widget.SeekBar;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

import java.io.IOException;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.bean.send.MusicEntity;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener {

    public MediaPlayer mediaPlayer; // 媒体播放器

    private String urlid = "";

    // 初始化播放器
    public Player() {
        super();
        try {
            App.TaskName = "实时广播中";
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次

    }

    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

        Log.e(100 + "% play", percent + " buffer");
    }


    public void play() {
        mediaPlayer.start();
    }

    /**
     * @param url url地址
     */
    public void playUrl(String url, String id) {
        try {
            urlid = id;
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            mediaPlayer.setDataSource(url); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放
        } catch (Exception e) {
            if (urlid != null || urlid.equals("")) {
                MusicEntity heartBeat = new MusicEntity(urlid);
            }
            e.printStackTrace();
        }
    }

    // 暂停
    public void pause() {
        mediaPlayer.pause();
    }

    // 停止
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        App.TaskName = "空闲";
    }

    // 播放准备
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.e("mediaPlayer", "onPrepared");
    }

    // 播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
        if (urlid != null || urlid.equals("")) {
            MusicEntity heartBeat = new MusicEntity(urlid);
            AppMessager.send2Server(Const.SEND_stop_PlayMusic, App.gson().toJson(heartBeat));
        }
        App.TaskName = "空闲";
    }

    private static Player sAudioPlay;


    public static Player getInstance() {
        if (sAudioPlay == null) {
            sAudioPlay = new Player();
        }
        return sAudioPlay;
    }

    public void ins() {
        sAudioPlay = null;
    }
}
