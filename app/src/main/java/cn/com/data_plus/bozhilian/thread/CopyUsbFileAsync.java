package cn.com.data_plus.bozhilian.thread;

import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.interfaces.StateListener;
import cn.com.data_plus.bozhilian.util.LogUtil;

public class CopyUsbFileAsync extends AsyncTask<Void, Void, Boolean> {
    private File srcFile;
    private String destFilePath;
    private StateListener stateListener;

    public CopyUsbFileAsync(File srcFile, String destFilePath, @NonNull StateListener stateListener) {
        this.srcFile = srcFile;
        this.destFilePath = destFilePath;
        this.stateListener = stateListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        stateListener.onStart();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Looper.prepare();
        Looper.loop();
        return copyDirectory(srcFile, destFilePath);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            stateListener.onSuccess();
        } else {
            stateListener.onFailed();
        }
    }

    /**
     * 复制整个目录的内容
     *
     * @param srcDirFile  待复制目录的目录名
     * @param destDirPath 目标目录名
     * @return 如果复制成功返回true，否则返回false
     */
    private boolean copyDirectory(File srcDirFile, String destDirPath) {
        boolean flag = true;
        // 判断源目录是否存在
        if (!srcDirFile.exists()) {
            LogUtil.debug("复制目录失败：源目录" + srcDirFile + "不存在！");
            flag = false;
        } else if (!srcDirFile.isDirectory()) {
            LogUtil.debug("复制目录失败：" + srcDirFile + "不是目录！");
            flag = false;
        }

        destDirPath = destDirPath.concat(File.separator);

        for (File file : srcDirFile.listFiles()) {
            // 复制文件
            if (file.isFile()) {
                flag = copyFile(file, destDirPath + file.getName());
            } else if (file.isDirectory()) {
                flag = copyDirectory(file, destDirPath + file.getName());
            }
        }
        if (!flag) {
            LogUtil.error("复制目录" + srcDirFile + "至" + destDirPath + "失败！");
        }
        return flag;
    }

    /**
     * 复制单个文件
     */
    private boolean copyFile(File srcFile, String destFilePath) {
        boolean flag;
        // 判断目标文件是否存在
        File destFile = new File(destFilePath);
        if (destFile.exists()) {
            flag = destFile.getTotalSpace() >= srcFile.getTotalSpace() || destFile.delete() && copy(srcFile, destFile);
        } else {
            if (destFile.getParentFile().exists()) {
                flag = copy(srcFile, destFile);
            } else {
                flag = destFile.getParentFile().mkdirs() && copy(srcFile, destFile);
            }
        }
        return flag;
    }

    private boolean copy(File srcFile, File destFile) {
        String str = "媒体文件(" + srcFile.getName() + ")正在拷贝中";
        AppMessager.resetProgress(str);
        // 复制文件
        int byteRead; // 读取的字节数
        long totalRead = 0;
        long fileLength = srcFile.length();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteRead);
                totalRead += byteRead;
                AppMessager.showProgress((int) (totalRead / (fileLength + 0.0f) * 100));
            }
            out.close();
            in.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
