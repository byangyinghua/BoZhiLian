package cn.com.data_plus.bozhilian.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.SparseArray;

import java.io.IOException;

import cn.com.data_plus.bozhilian.R;

public class UtilAudio {

    private SparseArray<MediaPlayer> players;

    public void startPlayMusic(Context context) {
        try {
            MediaPlayer mediaPlayer ;
            mediaPlayer = MediaPlayer.create(context ,R.raw.dingdong);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                }
            });
        } catch (Exception e) {
            //LogUtil.error("播放歌曲".concat(task.getFileName()).concat("时出错"));
        }
    }


    private static UtilAudio sAudioPlay;

    private UtilAudio() {
        players = new SparseArray<>();
    }

    public static UtilAudio getInstance() {
        if (sAudioPlay == null) {
            sAudioPlay = new UtilAudio();
        }
        return sAudioPlay;
    }
}
