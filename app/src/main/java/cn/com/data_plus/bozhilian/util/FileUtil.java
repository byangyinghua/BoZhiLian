package cn.com.data_plus.bozhilian.util;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.com.data_plus.bozhilian.App;

import static android.os.Environment.getExternalStorageDirectory;

public class FileUtil {

    public static final String ROOT_DIR_APP = "BoZhiLian";

    public static final String SUB_DIR_LOGS = "logs";
    public static final String SUB_DIR_MEDIA = "media";

    public static File getFile(String subDirPath, String fileName) {
        String path = getDirFile(subDirPath).getAbsolutePath().concat(File.separator).concat(fileName);
        File newFile = new File(path);
        if (!newFile.exists()) {
            try {
                if (newFile.createNewFile()) {//avoid the warning
//                    LogUtil.debug("文件 ".concat(fileName).concat(" 创建成功"));
                    return newFile;
                } else {
//                    LogUtil.debug("文件 ".concat(fileName).concat(" 已存在"));
                    return newFile;
                }
            } catch (IOException e) {
                LogUtil.error(e);
            }
        }
        return newFile;
    }

    private static File createDirFile(String dirPath) {
        File dirFile = new File(dirPath);
        if (dirFile.mkdirs()) {//avoid the warning
//            LogUtil.debug("目录 ".concat(dirPath).concat(" 创建成功"));
            return dirFile;
        } else {
//            LogUtil.debug("目录 ".concat(dirPath).concat(" 已存在"));
            return dirFile;
        }
    }

    public static File getDirFile(String subDir) {
        return App.app.getExternalFilesDir(subDir);
    }

    /**
     * 复制asset文件到指定目录
     *
     * @param oldPath asset下的路径
     * @param newPath SD卡下保存路径
     */
    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final String SEPARATOR = File.separator;//路径分隔符

    /*** 复制res/raw中的文件到指定目录
     * @param context 上下文
     * @param id 资源ID
     * @param fileName 文件名
     * @param storagePath 目标文件夹的路径
     */
    public static void copyFilesFromRaw(Context context, int id, String fileName, String storagePath) {
        InputStream inputStream = context.getResources().openRawResource(id);
        File file = new File(storagePath);
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.mkdirs();
        }
        readInputStream(storagePath + SEPARATOR + fileName, inputStream);
    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param storagePath 目标文件路径
     * @param inputStream 输入流
     */
    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static String readFile(File file) {
        FileReader fr = null;
        StringBuilder sb = new StringBuilder();
        try {
            fr = new FileReader(file);
            char[] chars = new char[64];

            int len;
            while ((len = fr.read(chars)) != -1) {
                sb.append(chars, 0, len);
            }
            fr.close();
            return sb.toString();
        } catch (IOException e) {
            LogUtil.error("读取文件时出错");
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static boolean write2File(String subDirPath, String fileName, String content, boolean append) {
        File file = getFile(subDirPath, fileName);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, append);
            fw.write(content);
            fw.flush();
            fw.close();
            return true;
        } catch (IOException e) {
            LogUtil.error("写入文件时出错:\n");
            return false;
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean deleteTaskFileByFileName(String mFilename) {
        String path = getDirFile(SUB_DIR_MEDIA).getAbsolutePath().concat(File.separator).concat(mFilename);
        try {
            File file = new File(path);
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteTaskFileByFilename(String fileName) {
        String path = getDirFile(SUB_DIR_MEDIA).getAbsolutePath().concat(File.separator).concat(fileName);
        File file = new File(path);
        return file.delete();
    }

    //============================================2017-7-15添加=====================================
    private String SDPATH;

    public FileUtil() {

    }

    public String getSDPATH() {
        return SDPATH;
    }

    public FileUtil(String SDPATH) {
        //得到外部存储设备的目录（/SDCARD）
        SDPATH = getDirFile(SUB_DIR_MEDIA).getAbsolutePath().concat(File.separator);
        LogUtil.error(SDPATH);
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws java.io.IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName 目录名字
     * @return 文件目录
     */
    public File createDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;

        try {
            createDir(path);
            file = createSDFile(path + fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            while (input.read(buffer) != -1) {
                output.write(buffer);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public void getlastAccessTime() {
        String pathName = FileUtil.getDirFile("media").getAbsolutePath().concat(File.separator);
        File file = new File(pathName);
        String[] flist = file.list();
        for (int i = 0; i < flist.length; i++) {
            File filrlist = new File(flist[i]);
        }
    }




    public static final String DEFAULT_BIN_DIR = "usb";

    /**
     * 检测SD卡是否存在
     */
    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 从指定文件夹获取文件
     *
     * @return 如果文件不存在则创建, 如果如果无法创建文件或文件名为空则返回null
     */
    public static File getSaveFile(String folderPath, String fileNmae) {
        File file = new File(getSavePath(folderPath) + File.separator
                + fileNmae);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取SD卡下指定文件夹的绝对路径
     *
     * @return 返回SD卡下的指定文件夹的绝对路径
     */
    public static String getSavePath(String folderName) {
        return getSaveFolder(folderName).getAbsolutePath();
    }

    /**
     * 获取文件夹对象
     *
     * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
     */
    public static File getSaveFolder(String folderName) {
        File file = new File(getExternalStorageDirectory()
                .getAbsoluteFile()
                + File.separator
                + folderName
                + File.separator);
        file.mkdirs();
        return file;
    }

    /**
     * 关闭流
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void redFileStream(OutputStream os, InputStream is) throws IOException {
        int bytesRead = 0;
        byte[] buffer = new byte[1024 * 8];
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        is.close();
    }






    /**
     * @description 把本地文件写入到U盘中
     * @author ldm
     * @time 2017/8/22 10:22
     */
    public static void saveSDFile2OTG(final File f, final UsbFile usbFile) {
        UsbFile uFile = null;
        FileInputStream fis = null;
        try {//开始写入
            fis = new FileInputStream(f);//读取选择的文件的
            if (usbFile.isDirectory()) {//如果选择是个文件夹
                UsbFile[] usbFiles = usbFile.listFiles();
                if (usbFiles != null && usbFiles.length > 0) {
                    for (UsbFile file : usbFiles) {
                        if (file.getName().equals(f.getName())) {
                            file.delete();
                        }
                    }
                }
                uFile = usbFile.createFile(f.getName());
                UsbFileOutputStream uos = new UsbFileOutputStream(uFile);
                try {
                    redFileStream(uos, fis);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    public boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

        /* 如果不需要打log，可以使用下面的语句
        if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
            return false;
        }
        */
            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
