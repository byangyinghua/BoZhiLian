package cn.com.data_plus.bozhilian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.global.AppMessager;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.interfaces.StateListener;
import cn.com.data_plus.bozhilian.thread.CopyUsbFileAsync;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;

public class ExternalStorageReceiver extends BroadcastReceiver implements StateListener {
    private String usbPath;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {//usb插入

            usbPath = intent.getData().getPath();

            String versionName = getPackageInfo(context).versionName;
            LogUtil.info( "U盘已插入"  );
//            String appName = getFile(versionName);
//            Log.e("TAG","appName:"+appName);
//            if (!appName.equals("")) {
//                installApk(appName, context);
//            }
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)) {//usb拔出
            LogUtil.info("U盘已拔出");
            AppMessager.send2Activity(Const.MSG_SHOW_TOP_TEXT, "");
        }
    }

    private String getFile(String versionName) {
        try {
            File dir = new File(usbPath.concat(File.separator).concat(FileUtil.ROOT_DIR_APP)+ "/app/" );
            if (!dir.exists()) {
                return "";
            }
            String[] fileList = dir.list();
            if (fileList.length > 0) {
                for (int i = 0; i < fileList.length; i++) {
                    String app = fileList[i];
                    String appNameOne = app.substring(0,app.lastIndexOf("."));
                    String[] version = appNameOne.split("_");
                    if (Double.parseDouble(versionName) < Double.parseDouble(version[1])) {
                        return app;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return "";
    }

    private void installApk(String appName, Context context) {
        File dir = new File(usbPath.concat(File.separator).concat(FileUtil.ROOT_DIR_APP)+ "/app/" + appName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(dir), "application/vnd.android.package-archive");
        context.startActivity(intent);

    }

    private PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    private void copyUsbTask() {
        if (TextUtils.isEmpty(usbPath)) {
            AppMessager.send2Activity(Const.MSG_SHOW_TOP_TEXT, App.gainString(R.string.usb_failed_path));
            return;
        }

        String usbAppPath = usbPath.concat(File.separator).concat(FileUtil.ROOT_DIR_APP);
        File file = new File(usbAppPath);
        if (!file.exists()) {
            AppMessager.send2Activity(Const.MSG_SHOW_TOP_TEXT, App.gainString(R.string.usb_no_task_file));
            return;
        }

        //copy media files
        String srcDirPath = usbAppPath.concat(File.separator).concat(FileUtil.SUB_DIR_MEDIA);
        file = new File(srcDirPath);
        if (!file.exists()) {
            onSuccess();
            return;
        }
        String destDirPath = FileUtil.getDirFile(FileUtil.SUB_DIR_MEDIA).getAbsolutePath();
        new CopyUsbFileAsync(file, destDirPath, this).execute();
    }

    private void handleUsbTask() {
        AppMessager.send2Activity(Const.MSG_SHOW_TOP_TEXT, App.gainString(R.string.usb_handle_task));
        String usbAppPath = usbPath.concat(File.separator).concat(FileUtil.ROOT_DIR_APP);
        File rootDirFile = new File(usbAppPath);
        for (File f : rootDirFile.listFiles()) {
            if (f.getAbsolutePath().endsWith("json")) {
                String taskJson = FileUtil.readFile(f);
                AppMessager.send2Activity(Const.RECV_TASK, taskJson);
            }
        }
    }

    @Override
    public void onStart() {
        AppMessager.send2Activity(Const.MSG_SHOW_TOP_TEXT, App.gainString(R.string.usb_copy_media));
    }

    @Override
    public void onSuccess() {
        handleUsbTask();
        AppMessager.send2Activity(Const.MSG_SHOW_TOP_TEXT, App.gainString(R.string.usb_task_success));
    }

    @Override
    public void onFailed() {
        AppMessager.send2Activity(Const.MSG_SHOW_TOP_TEXT, App.gainString(R.string.usb_task_failed));
    }
}
