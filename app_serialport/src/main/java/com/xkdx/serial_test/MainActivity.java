package com.xkdx.serial_test;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class MainActivity extends Activity {
    protected SerialPort mSerialPort;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    private TextView text;
    private String prot = "ttyS0";
    private int baudrate = 9600;
    private static int i = 0;
    private StringBuilder sb;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                text.setText(text.getText().toString().trim()+sb.toString());
            }
        }
    };
    private Thread receiveThread;
    private Thread sendThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb = new StringBuilder();
        text = (TextView) findViewById(R.id.text_receive);

        Button btn_set = (Button) findViewById(R.id.btn_set);
        btn_set.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText et_prot = (EditText) findViewById(R.id.et_prot);
                EditText et_num = (EditText) findViewById(R.id.et_num);
                prot = TextUtils.isEmpty(et_prot.getText().toString().trim()) ? "ttyS0"
                        : et_prot.getText().toString().trim();
                baudrate = Integer.parseInt(TextUtils.isEmpty(et_num.getText()
                        .toString().trim()) ? "9600" : et_num.getText()
                        .toString().trim());
            }
        });

        Button btn_open = (Button) findViewById(R.id.btn_open);
        btn_open.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 打开
                try {
                    mSerialPort = new SerialPort(new File("/dev/ttyS2"), baudrate,
                            0);
                    mInputStream = mSerialPort.getInputStream();
                    mOutputStream = mSerialPort.getOutputStream();
                    receiveThread();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("test", "打开失败");
                    e.printStackTrace();
                }
            }
        });

        Button btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 发送

                try {
                    mOutputStream.write(("1").getBytes());
                    Log.i("test", "发送成功:1" + i);
                   // Thread.sleep(1000);
                } catch (Exception e) {
                    Log.i("test", "发送失败");
                    e.printStackTrace();
                }
//                sendThread = new Thread() {
//                    @Override
//                    public void run() {
//                        while (true) {
//                            try {
//                                i++;
//                                mOutputStream.write(("1").getBytes());
//                                Log.i("test", "发送成功:1" + i);
//                                Thread.sleep(1000);
//                            } catch (Exception e) {
//                                Log.i("test", "发送失败");
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                };
//                sendThread.start();
            }
        });

        Button btn_receive = (Button) findViewById(R.id.btn_receive);
        btn_receive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSerialPort();
            }
        });

    }

    private void receiveThread() {
        // 接收
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    int size;
                    try {
                        byte[] buffer = new byte[1024];
                        if (mInputStream == null)
                            return;
                        size = mInputStream.read(buffer);
                        if (size > 0) {
                            String recinfo = new String(buffer, 0,
                                    size);
                            Log.i("test", "接收到串口信息:" + recinfo);
                            sb.append(recinfo).append(",");
                            handler.sendEmptyMessage(1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receiveThread.start();
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {

        if (mSerialPort != null) {
            mSerialPort.close();
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
