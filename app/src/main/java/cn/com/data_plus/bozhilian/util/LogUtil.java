package cn.com.data_plus.bozhilian.util;

import android.util.Log;

import java.io.File;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.global.AppMessager;

/**
 * 日志打印
 */
public class LogUtil {
    //控制是否调试程序，在上线时应该关闭
    private static final boolean DEBUG = true;

    private static final String TAG = "-log-";

    public static void debug(Object msg) {
        if (DEBUG) {
            showLogCat(msg, Log.DEBUG);
        }
        saveRunningLog(msg.toString(), App.colorDebug);
    }

    public static void info(String msg) {
        if (DEBUG) {
            showLogCat(msg, Log.INFO);
        }
        AppMessager.toast(msg, App.colorInfo);
        saveRunningLog(msg, App.colorInfo);
    }

    public static void error(Object msg) {
        String content;
        if (msg instanceof Throwable) {
            content = Log.getStackTraceString((Throwable) msg);
        } else {
            content = msg.toString();
        }
        if (DEBUG) {
            showLogCat(content, Log.ERROR);
        }
        saveRunningLog(content, App.colorError);
    }

    public static void error(Object msg, Throwable tr) {
        String content = msg.toString().concat("\n").concat(Log.getStackTraceString(tr));
        if (DEBUG) {
            showLogCat(content, Log.ERROR);
        }
        AppMessager.toast(msg.toString(), App.colorError);
        saveRunningLog(content, App.colorError);
    }

    /**
     * 获取调用此方法的类名
     */
    private static String getCurrentClassTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }

    private static void showLogCat(Object msg, int level) {
        String content;
        switch (level) {
            case Log.DEBUG:
                content = position("【debug】").concat(":\n").concat(msg.toString());
                Log.d(TAG.concat(":").concat(getCurrentClassTag()), content);
                break;
            case Log.INFO:
                content = position("【info】").concat(":\n").concat(msg.toString());
                Log.i(TAG.concat(":").concat(getCurrentClassTag()), content);
                break;
            case Log.ERROR:
                content = position("【error】").concat(":\n").concat(msg.toString());
                Log.e(TAG.concat(":").concat(getCurrentClassTag()), content);
                break;
        }
    }

    /**
     * 超链接，显示调用打印方法的位置
     */
    private static String position(String prefix) {
        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].toString().contains(getCurrentClassTag())) {
                currentIndex = i + 3;
                break;
            }
        }
        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = fullClassName.substring(fullClassName
                .lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();
        String lineNumber = String.valueOf(stackTraceElement[currentIndex].getLineNumber());
        return prefix + " at " + className + "." + methodName + "(" + className + ".java:" + lineNumber + ")";
    }

    private static void saveRunningLog(String content, int color) {
        String dirName = FileUtil.SUB_DIR_LOGS;
        String fileName = getLogFileName();
        File taskFile = FileUtil.getFile(dirName, fileName);

        if (taskFile.length() == 0) {
            String title = "<!DOCTYPE html><html><head><title>"
                    .concat(App.date)
                    .concat("  LOG</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /></head><body>");
            FileUtil.write2File(dirName, fileName, title, true);
        }

        content = TimeUtil.getTime().concat(" ---> ").concat(content);
        content = "\n<font color=\""
                .concat((color == App.colorInfo ? "#48BB31" : (color == App.colorError ? "#8F0005" : "#0070BB")))
                .concat("\">").concat(content).concat("</font>");
        content = content.replaceAll("\n", "<br/>");
        if (!FileUtil.write2File(dirName, fileName, content, true)) {
            LogUtil.error("保存状态信息到文件时出错:\n".concat(content));
        }
    }

    public static String getLogFileName() {
        return "log-" + App.date + ".html";
    }
}
