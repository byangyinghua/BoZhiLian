package cn.com.data_plus.bozhilian.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ijkplayer.media.IjkVideoView;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.bean.send.HeartBeat;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Config;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.util.LogUtil;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class playVideoFragment extends BaseFragment {

    private final static String TASK_JSON = "";
    private static String url1;

    private String Url = "";
    private IjkVideoView mVideoView;
    private boolean mBackPressed;
    private static String mVideoPath;


    //创建一个handler，内部完成处理消息方法
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int order = msg.what;
            String content = (String) msg.obj;
            switch (order) {
                case 147852:
                    mVideoView.setVideoPath(mVideoPath);
                    mVideoView.start();
                    break;

            }
        }
    };//不加这个分号则不能自动添加代码

    private TimerTask mTimerTask;
    private Timer mTimer;

    public static playVideoFragment newInstance(String Url) {
        mVideoPath = Url;
        playVideoFragment videoFragment = new playVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Url, Url);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_video, container, false);
        mVideoView = (IjkVideoView) view.findViewById(R.id.video_view);
        // init player

 /*       if (mVideoView != null) {
            mVideoView.setVideoPath(mVideoPath);

        }
*/
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 147852;
                msg.obj = "";
                handler.sendMessage(msg);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 600);//3秒后执行TimeTask的run方法

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            IjkMediaPlayer.native_profileEnd();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppMessager.send2Activity(Const.MSG_LOOPTASKList, "");

    }
}
