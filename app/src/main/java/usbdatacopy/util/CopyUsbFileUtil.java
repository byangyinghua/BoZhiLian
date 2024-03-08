package usbdatacopy.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.partition.Partition;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.bean.MedioBean;
import usbdatacopy.util.UsbOtgMananger;

/**
 * 复制usb文件
 */

public class CopyUsbFileUtil {
    public final static String U_DISK_FILE_NAME = "/BY_config.txt";
    public final static String U_DISK_FILE_NAME_PREANT = "BoYaoKeJi";
    //自定义U盘读写权限
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    //当前处接U盘列表
    private UsbMassStorageDevice[] storageDevices;
    //当前U盘所在文件目录
    private UsbFile cFolder;
    private Handler mHandler;

    public CopyUsbFileUtil(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * 复制读取txt中需要复制的文件名称
     *
     * @param fileName
     */
    private void readFromUDiskCopy(String fileName, int type) {
        UsbFile[] usbFiles = new UsbFile[0];
        if (cFolder != null) {
            try {
                usbFiles = cFolder.listFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean isHave = false;
            if (null != usbFiles && usbFiles.length > 0) {
                for (UsbFile usbFile : usbFiles) {
                    if (usbFile.getName().equals(fileName)) {
                        readCopyFile(usbFile, type);
                        isHave = true;
                    }
                }
            }
            if (!isHave) {
                showToastMsg(fileName.substring(fileName.lastIndexOf("\\") + 1) + "文件不存在");
            }
        } else {
            showToastMsg("没有插入u盘");
        }
    }

    //保存mp3类型音频
    private List<String> mp3List;
    //保存mkv类型视频数据
    private List<String> vadioList;
    //保存图片类型数据
    private List<String> imagesList;

    private List<String> mp3Url;
    private List<String> voidUrl;
    private List<String> imageUrl;

    public List<String> getMp3Url() {
        return mp3Url;
    }

    public void setMp3Url(List<String> mp3Url) {
        this.mp3Url = mp3Url;
    }

    public List<String> getVoidUrl() {
        return voidUrl;
    }

    public void setVoidUrl(List<String> voidUrl) {
        this.voidUrl = voidUrl;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    private void readTxtFromUDisk(UsbFile usbFile) {
        String s = "";
        UsbFile descFile = usbFile;
        mp3List = new ArrayList<>();
        vadioList = new ArrayList<>();
        //保存图片类型数据
        imagesList = new ArrayList<>();
        String path = App.context.getFilesDir().getAbsolutePath() + "/BoYaoKeJi";
        deleteDir(path);//删除以前的数据
        UsbOtgMananger.copy(usbFile, path);

        Message msg = mHandler.obtainMessage();
        msg.what = -888;
        msg.obj = "复制完成";
        mHandler.sendMessage(msg);

      /*  //读取txt文件数据
        UsbFile[] testFiles = new UsbFile[0];

        if (cFolder != null) {
            try {
                testFiles = cFolder.listFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != testFiles && testFiles.length > 0) {
                for (UsbFile txt : testFiles) {
                    if (txt.getName().equals(U_DISK_FILE_NAME)) {
                        InputStream is = new UsbFileInputStream(txt);
                        //读取秘钥中的数据进行匹配
                        StringBuilder sb = new StringBuilder();
                        BufferedReader bufferedReader = null;
                        try {
                            bufferedReader = new BufferedReader(new InputStreamReader(is));
                            String read;
                            while ((read = bufferedReader.readLine()) != null) {
                                sb.append(read);
                            }
                            s = new String(sb.toString().getBytes("UTF-8"), "UTF-8");
                            Message msg = mHandler.obtainMessage();
                            msg.what = -888;
                            msg.obj = s;
                            mHandler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (bufferedReader != null) {
                                    bufferedReader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else {
            showToastMsg("没有插入u盘");
        }*/

     /*   //删除以前的音频和视频文件
        deleteDir(path + URL_MP3);
        deleteDir(path + URL_VODIO);
        deleteDir(path + URL_IMAGE);*/

        // UsbOtgMananger.CopySdcardFile(usbFile,path);
        //读取文件内容
  /*      InputStream is = new UsbFileInputStream(descFile);
        //读取秘钥中的数据进行匹配
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            String read;
            while ((read = bufferedReader.readLine()) != null) {
                sb.append(read);
            }
            s = new String(sb.toString().getBytes("UTF-8"), "UTF-8");
            Message msg = mHandler.obtainMessage();
            if (s.contains("!")) {
                String mp3s = s.split("!")[1];
                if (mp3s.contains("%")) {
                    String[] mp3 = mp3s.split("%");
                    if (mp3.length > 0)
                        for (int i = 0; i < mp3.length; i++) {
                            mp3List.add(mp3[i]);
                        }
                    msg.what = -888;
                }
                String vodios = s.split("!")[2];
                if (vodios.contains("%")) {
                    String[] vodi = vodios.split("%");
                    if (vodi.length > 0)
                        for (int i = 0; i < vodi.length; i++) {
                            vadioList.add(vodi[i]);
                        }
                    msg.what = -888;
                } else {
                    vadioList.add(vodios);//只有一条数据
                    msg.what = -888;
                }
                msg.obj = s;
                mHandler.sendMessage(msg);
            } else {
                showToastMsg("test.txt文件格式不正确");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

    }

    public void readFromUDisk() {
        UsbFile[] usbFiles = new UsbFile[0];
        if (cFolder != null) {
            try {
                usbFiles = cFolder.listFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != usbFiles && usbFiles.length > 0) {
                for (UsbFile usbFile : usbFiles) {
                    if (usbFile.getName().equals(U_DISK_FILE_NAME_PREANT)) {
                        readTxtFromUDisk(usbFile);
                    }
                }
            }
        } else {
            showToastMsg("没有插入u盘");
        }
    }

    /**
     * @description U盘设备读取
     * @author ldm
     * @time 2017/9/1 17:20
     */
    public void redUDiskDevsList() {
        //设备管理器
        UsbManager usbManager = (UsbManager) App.context.getSystemService(Context.USB_SERVICE);
        //获取U盘存储设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(App.context);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //一般手机只有1个OTG插口
        for (UsbMassStorageDevice device : storageDevices) {
            //读取设备是否有权限
            if (usbManager.hasPermission(device.getUsbDevice())) {
                readDevice(device);
            } else {
                //没有权限，进行申请
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent);
            }
        }
        if (storageDevices.length == 0) {
            showToastMsg("请插入可用的U盘");
        }
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 复制文件
     *
     * @param usbFile
     * @param type    图片1 音频2 视频3
     */
    public static final String URL_IMAGE = "/images";
    public static final String URL_MP3 = "/mp3";
    public static final String URL_VODIO = "/vodio";
    public static final String URL_PRANTE = "/media";

    private void readCopyFile(UsbFile usbFile, int type) {
        UsbFile descFile = usbFile;
        String s = URL_IMAGE;
        String path = App.context.getApplicationContext().getFilesDir().getAbsolutePath();
        switch (type) {//分别创建不同类型文件
            case 1:
                s = path + URL_PRANTE + URL_IMAGE;
                imageUrl.add(s + "/" + usbFile.getName());
                break;
            case 2:
                s = path + URL_PRANTE + URL_MP3;
                mp3Url.add(s + "/" + usbFile.getName());
                break;
            case 3:
                s = path + URL_PRANTE + URL_VODIO;
                voidUrl.add(s + "/" + usbFile.getName());
                break;
        }
        UsbOtgMananger.copy(usbFile, s);
        // UsbOtgMananger.CopySdcardFile(usbFile,path);
    }

    public UsbMassStorageDevice getUsbMass(UsbDevice usbDevice) {
        for (UsbMassStorageDevice device : storageDevices) {
            if (usbDevice.equals(device.getUsbDevice())) {
                return device;
            }
        }
        return null;
    }

    public void readDevice(UsbMassStorageDevice device) {
        try {
            device.init();//初始化
            //设备分区
            Partition partition = device.getPartitions().get(0);
            //文件系统
            FileSystem currentFs = partition.getFileSystem();
            currentFs.getVolumeLabel();//可以获取到设备的标识
            //通过FileSystem可以获取当前U盘的一些存储信息，包括剩余空间大小，容量等等
            Log.e("Capacity: ", currentFs.getCapacity() + "");
            Log.e("Occupied Space: ", currentFs.getOccupiedSpace() + "");
            Log.e("Free Space: ", currentFs.getFreeSpace() + "");
            Log.e("Chunk size: ", currentFs.getChunkSize() + "");
            cFolder = currentFs.getRootDirectory();//设置当前文件对象为根目录
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToastMsg(String msg) {
        Toast.makeText(App.context, msg, Toast.LENGTH_SHORT).show();
    }

    public void copyFile() {
        voidUrl = new ArrayList<>();
        mp3Url = new ArrayList<>();
        imageUrl = new ArrayList<>();
        new Thread(new Runnable() {//复制具体文件
            @Override
            public void run() {
                if (!mp3List.isEmpty())
                    for (int i = 0; i < mp3List.size(); i++) {
                        readFromUDiskCopy(mp3List.get(i), 2);
                    }
                if (!vadioList.isEmpty())
                    for (int i = 0; i < vadioList.size(); i++) {
                        readFromUDiskCopy(vadioList.get(i), 3);
                    }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = mHandler.obtainMessage();
                        msg.what = -889;
                        showToastMsg("文件复制完成");
                        msg.obj = "是否播放";
                        mHandler.sendMessage(msg);
                    }
                });
            }
        }).start();
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 遍历文件夹的数据
     *
     * @param path
     */
    public List<MedioBean> traverseFolder2(String path, List<MedioBean> mData) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");
                return mData;
            } else {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        System.out.println("文件夹:" + file1.getAbsolutePath());
                        traverseFolder2(file1.getAbsolutePath(), mData);
                    } else {
                        System.out.println("文件:" + file1.getAbsolutePath());
                        MedioBean bean = new MedioBean();
                        bean.setName(file1.getName());
                        bean.setType(file1.getName().substring(file1.getName().lastIndexOf(".") + 1));
                        bean.setUrl(file1.getAbsolutePath());
                        mData.add(bean);
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        return mData;
    }
}
