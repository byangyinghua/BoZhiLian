package cn.com.data_plus.bozhilian.thread;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.bean.send.Download;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.interfaces.StateListener;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;

//定时任务文件下载
public class newDownloadAsync extends AsyncTask<Void, Long, Void> {
    private Socket mSocket;
    private StateListener mDownloadListener;
    private int fileLength;
    private String fileName;
    private String urlStr;
    private newTask mTask;
    private OutputStream output = null;
    private InputStream inStream = null;

    public newDownloadAsync(newTask task, String urlStr, String FileLength, String FileName, @NonNull StateListener downloadListener) {
        this.mTask = task;
        this.urlStr = urlStr;
        this.fileLength = Integer.parseInt(FileLength);


        this.fileName = FileName;
        mDownloadListener = downloadListener;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        LogUtil.error((int) (values[0] / (fileLength + 0.0f) * 100));
        AppMessager.showProgress((int) (values[0] / (fileLength + 0.0f) * 100));
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String str;
            if(mTask==null){
                  str = "任务文件<"
                        + fileName
                        + ">正在传输中";
            }else{
                str = "任务("
                        + mTask.getTaskName()
                        + ")文件<"
                        + fileName
                        + ">正在传输中";
            }

            AppMessager.resetProgress(str);
            // 下载网络文件
            int bytesum = 0;
            int byteread = 0;
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            inStream = conn.getInputStream();
            String pathName = FileUtil.getDirFile("media").getAbsolutePath().concat(File.separator) + fileName;//文件存储路径
            FileOutputStream fs = new FileOutputStream(pathName);

            byte[] buffer = new byte[1024];

            // 进度
            long step = fileLength / 100;
            long process = 0;
            long currentSize = 0;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
                currentSize = currentSize + byteread;
                if (currentSize / step != process) {
                    process = currentSize / step;
                    if (process %1 == 0) {  //每隔%5的进度返回一次
                            //System.out.println(process);
                        AppMessager.showProgress((int) process);
                    }
                }
            }

            if (bytesum == fileLength) {
                //file not exist on server
                mDownloadListener.onSuccess();

            } else {
                //download complete
                mDownloadListener.onFailed();
            }
        } catch (IOException e) {

            mDownloadListener.onFailed();
            LogUtil.error("文件下载连接出错", e);
        } catch (Exception e) {
            mDownloadListener.onFailed();
            LogUtil.error(e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (inStream != null) {
                    inStream.close();
                }


                System.out.println("success");
            } catch (IOException e) {
                System.out.println("fail");
                e.printStackTrace();
            }
        }
        return null;
    }

    private void recvFile(File file, long fileLength) throws Exception {
        FileOutputStream fos = new FileOutputStream(file, true);
        int size = 1024 * 1024;
        byte[] bytes = new byte[size];
        int hasRead;
        long total = 0;
        while (true) {
            if (file.length() < fileLength) {
                hasRead = mSocket.getInputStream().read(bytes);
                fos.write(bytes, 0, hasRead);
                onProgressUpdate(total += hasRead);
            } else {
                fos.close();
                break;
            }
        }
    }

    public void exit() {
        try {
            if (output != null) {
                output.close();
            }
            System.out.println("success");
        } catch (IOException e) {
            System.out.println("fail");
            e.printStackTrace();
        }
    }


    public void downloadNet() throws MalformedURLException {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = new URL("windine.blogdriver.com/logo.gif  ");

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream("c:/abc.gif");

            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
