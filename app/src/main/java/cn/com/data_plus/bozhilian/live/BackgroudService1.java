package cn.com.data_plus.bozhilian.live;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.seu.magicfilter.utils.MagicFilterType;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

public class BackgroudService1 extends Service implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {


    /**
     * 窗口管理者
     */
    private WindowManager mWindowManager;
    private static final String TAG = "intercomTAG";
    private SrsCameraView mSurfaceView = null;
    private SrsPublisher mSrsPublisher = null;
    private String publishURL;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate..");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        try {
            publishURL = intent.getExtras().getString("rtmpUrl");
        }catch (Exception e){
            e.printStackTrace();
            this.onDestroy();
        }
        Log.e("TAG", "publishURL:" + publishURL);


        mWindowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        mSurfaceView = new SrsCameraView(this);
        mSrsPublisher = new SrsPublisher(mSurfaceView);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                15, 15, WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowManager.addView(mSurfaceView, layoutParams);

        mSrsPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mSrsPublisher.setRtmpHandler(new RtmpHandler(this));
        mSrsPublisher.setRecordHandler(new SrsRecordHandler(this));
        mSrsPublisher.switchCameraFilter(MagicFilterType.NONE);
        mSrsPublisher.setPreviewResolution(480, 640);
        mSrsPublisher.setOutputResolution(640, 480);
        mSrsPublisher.setVideoSmoothMode();
        mSrsPublisher.startCamera();
        mSrsPublisher.startPublish(publishURL);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onNetworkWeak() {
        Log.e("Bgpublish","onNetworkWeak");

    }

    @Override
    public void onNetworkResume() {
        Log.e("Bgpublish","onNetworkResume");
    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        Log.e("Bgpublish",""+e.toString());
    }

    @Override
    public void onRecordPause() {
        Log.e("Bgpublish","onRecordPause");
    }

    @Override
    public void onRecordResume() {
        Log.e("Bgpublish","onRecordResume");
    }

    @Override
    public void onRecordStarted(String msg) {
        Log.e("Bgpublish",""+msg);
    }

    @Override
    public void onRecordFinished(String msg) {
        Log.e("Bgpublish",""+msg);
    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        Log.e("Bgpublish",""+e.toString());
    }

    @Override
    public void onRecordIOException(IOException e) {
        Log.e("Bgpublish",""+e.toString());
    }

    @Override
    public void onRtmpConnecting(String msg) {
        Log.e("Bgpublish",""+msg);
    }

    @Override
    public void onRtmpConnected(String msg) {
        Log.e("Bgpublish",""+msg);
    }

    @Override
    public void onRtmpVideoStreaming() {
        Log.e("Bgpublish","onRtmpVideoStreaming");
    }

    @Override
    public void onRtmpAudioStreaming() {
        Log.e("Bgpublish","onRtmpAudioStreaming");
    }

    @Override
    public void onRtmpStopped() {
        Log.e("Bgpublish","onRtmpStopped");
    }

    @Override
    public void onRtmpDisconnected() {
        Log.e("Bgpublish","onRtmpDisconnected");
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        Log.e("Bgpublish",""+fps);
    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
        Log.e("Bgpublish",""+bitrate);
    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {
        Log.e("Bgpublish",""+bitrate);
    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        Log.e("Bgpublish",""+e.toString());
    }

    @Override
    public void onRtmpIOException(IOException e) {
        Log.e("Bgpublish",""+e.toString());
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        Log.e("Bgpublish",""+e.toString());
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        Log.e("Bgpublish",""+e.toString());
    }


    @Override
    public void onDestroy() {
        Log.i("Bgpublish", "activity destory!");
        onstop();

        super.onDestroy();
    }

    void onstop() {

        if (mWindowManager != null) {
            mWindowManager.removeView(mSurfaceView);
        }
        if(mSrsPublisher!=null){
            mSrsPublisher.stopPublish();
        }

    }
}
