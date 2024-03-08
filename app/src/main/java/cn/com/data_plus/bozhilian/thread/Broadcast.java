package cn.com.data_plus.bozhilian.thread;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.util.LogUtil;

//广播喊话
public class Broadcast implements Runnable {
    private AudioTrack mAudioTrack;
    private WifiManager.MulticastLock mLock;
    private int mLength;
    private boolean mRunning;

    private String mIP;
    private int mPort;

    public Broadcast(String ip, int port) {
        mIP = ip;
        mPort = port;
    }

    @Override
    public void run() {
        try {
            MulticastSocket socket = new MulticastSocket(mPort);
            socket.setLoopbackMode(true);
            socket.joinGroup(InetAddress.getByName(mIP));
            byte[] bytes;
            while (mRunning) {
                bytes = new byte[mLength];
                DatagramPacket datagramPacket = new DatagramPacket(bytes, mLength);
                socket.receive(datagramPacket);
                mAudioTrack.write(datagramPacket.getData(), 0, datagramPacket.getData().length);
            }
            mAudioTrack.stop();
            mAudioTrack = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        mRunning = true;

        WifiManager manager = (WifiManager) App.app.getSystemService(Context.WIFI_SERVICE);
        mLock = manager.createMulticastLock("MulticastTest");
        mLock.acquire();

        mLength = AudioTrack.getMinBufferSize(48000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                48000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                mLength,
                AudioTrack.MODE_STREAM);

        mAudioTrack.play();

        new Thread(this).start();
    }

    public void stop() {
        mRunning = false;
        mLock.release();
    }
}
