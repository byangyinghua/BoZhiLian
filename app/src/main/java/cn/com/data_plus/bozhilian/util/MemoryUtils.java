package cn.com.data_plus.bozhilian.util;

/**
 * Created by Administrator on 2018/5/2 0002.
 */

import java.util.List;
import java.io.FileReader;
import android.os.Process;
import java.io.IOException;
import java.io.BufferedReader;
import android.widget.EditText;
import android.content.Context;
import java.io.InputStreamReader;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;

/**
 * 文档描述:
 * 内存工具类
 *
 * 包括内容:
 * 1 内存清理即杀掉进程的几种方式
 * 2 获取内存总大小及其可用大小
 * 3 判断栈顶Activity名及其所属进程名
 *
 * 注意权限:
 * <uses-permission android:name="android.permission.GET_TASKS"/>
 * <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
 *
 * 文档日期:
 * 2014年5月30日10:01:55
 *
 */
public class MemoryUtils {

    //----------> 以下为杀掉进程的几种方式

    public static void cleanMemory(Context context) {
        long beforeCleanMemory=getAvailMemory(context);
        System.out.println("---> 清理前可用内存大小:"+beforeCleanMemory+"M");
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        RunningAppProcessInfo runningAppProcessInfo = null;
        List<RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcessInfoList.size(); ++i) {
            runningAppProcessInfo = runningAppProcessInfoList.get(i);
            String processName = runningAppProcessInfo.processName;
            //调用杀掉进程的方法
            //System.out.println("---> 开始清理:"+processName);
            killProcessByRestartPackage(context, processName);
        }
        long afterCleanMemory=getAvailMemory(context);
        System.out.println("---> 清理后可用内存大小:"+afterCleanMemory+"M");
//        System.out.println("---> 节约内存大小:"+(afterCleanMemory-beforeCleanMemory)+"M");
        System.out.println("共清理:"+(afterCleanMemory-beforeCleanMemory)+"M");
    }



    //利用activityManager.restartPackage()方法杀死进程
    //该方法实际调用了activityManager.killBackgroundProcesses()方法
    public static void killProcessByRestartPackage(Context context,String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.restartPackage(packageName);
        System.gc();
    }


    //利用Process.killProcess(pid)杀死进程
    //注意事项:
    //1 该方式可自杀,即杀掉本进程
    //2 该方式可杀掉其他普通应用进程
    //3 该方式不可杀掉系统级应用即system/app应用
    public static void killProcessBykillProcess(int pid){
        Process.killProcess(pid);
    }


    //利用adb shell命令杀死进程
    public static void killProcessByAdbShell(int pid) {
        String cmd = "adb shell kill -9 " + pid;
        System.out.println("-------> cmd=" + cmd);
        try {
            java.lang.Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("----> exec shell:" + line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //利用su进程的命令方式杀死进程
    //1 得到su进程(super进程)
    //  Runtime.getRuntime().exec("su");
    //2 利用su进程执行命令
    //  process.getOutputStream().write(cmd.getBytes());
    public static void killProcessBySu(int pid) {
        try {
            java.lang.Process process = Runtime.getRuntime().exec("su");
            String cmd = "kill -9 " + pid;
            System.out.println("-------> cmd = " + cmd);
            process.getOutputStream().write(cmd.getBytes());
            if ((process.waitFor() != 0)) {
                System.out.println("-------> su.waitFor()!= 0");
            } else {
                System.out.println("------->  su.waitFor()==0 ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //----------> 以上为杀掉进程的几种方式





    //获取当前进程名
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningAppProcessInfo runningAppProcessInfo : activityManager.getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == pid) {
                String processName=runningAppProcessInfo.processName;
                return processName;
            }
        }
        return null;
    }


    //获取栈顶Activity名称
    public static String getTopActivityName(Context context) {
        String topActivityName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            String topActivityClassName = f.getClassName();
            String temp[] = topActivityClassName.split("\\.");
            topActivityName = temp[temp.length - 1];
        }
        return topActivityName;
    }



    //获取栈顶Activity所属进程的名称
    public static String getTopActivityProcessName(Context context) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName componentName = runningTaskInfos.get(0).topActivity;
            String topActivityClassName = componentName.getClassName();
            int index = topActivityClassName.lastIndexOf(".");
            processName = topActivityClassName.substring(0, index);
        }
        return processName;
    }



    //获取内存总大小
    public static long getTotalMemory() {
        // 系统的内存信息文件
        String filePath = "/proc/meminfo";
        String lineString;
        String[] stringArray;
        long totalMemory = 0;
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader,1024 * 8);
            // 读取meminfo第一行,获取系统总内存大小
            lineString = bufferedReader.readLine();
            // 按照空格拆分
            stringArray = lineString.split("\\s+");
            // 获得系统总内存,单位KB
            totalMemory = Integer.valueOf(stringArray[1]).intValue();
            bufferedReader.close();
        } catch (IOException e) {
        }
        return totalMemory / 1024;
    }



    //获取可用内存大小
    public static long getAvailMemory(Context context) {
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem / (1024 * 1024);
    }


}