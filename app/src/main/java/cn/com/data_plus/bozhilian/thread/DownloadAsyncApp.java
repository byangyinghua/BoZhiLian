package cn.com.data_plus.bozhilian.thread;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import cn.com.data_plus.bozhilian.bean.local.newTask;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.interfaces.StateListener;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;

//更新app文件下载
public class DownloadAsyncApp extends AsyncTask<Void, Long, Void> {

    private StateListener mDownloadListener;

    private String urlStr;

    private OutputStream output = null;
    private InputStream inStream = null;

    public DownloadAsyncApp(String urlStr, @NonNull StateListener downloadListener) {
        this.urlStr = urlStr;
        mDownloadListener = downloadListener;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            mDownloadListener.onStart();
            String fileName = urlStr.substring(urlStr.lastIndexOf("/"), urlStr.length());
            // 下载网络文件
            int bytesum = 0;
            int byteread = 0;
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            inStream = conn.getInputStream();
            String pathName = FileUtil.getDirFile("app").getAbsolutePath().concat(File.separator) + fileName;//文件存储路径
            FileOutputStream fs = new FileOutputStream(pathName);
            byte[] buffer = new byte[1024];

            // 进度
            long process = 0;
            long currentSize = 0;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
                currentSize = currentSize + byteread;
            }


            mDownloadListener.onSuccess();

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


}
