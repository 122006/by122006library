package com.by122006library.Functions;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.by122006library.Utils.DebugUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Log工具，类似android.util.Log。 tag自动产生，格式: customTagPrefix:className.methodName(Line:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(Line:lineNumber)。
 */
public class mLog {

    public static final String LOG_PATH = Environment.getExternalStorageDirectory().getPath(); // SD卡中的根目录
    private static final ThreadLocal<ReusableFormatter> thread_local_formatter = new ThreadLocal<ReusableFormatter>() {
        protected ReusableFormatter initialValue() {
            return new ReusableFormatter();
        }
    };
    public static String customTagPrefix = "";    // 自定义Tag的前缀，可以是作者名
    public static boolean isSaveLog = false;    // 是否把保存日志到SD卡中
    /**
     * 自定义的logger
     */
    public static CustomLogger customLogger;
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);

    private mLog() {
    }

    public static String generateTag(StackTraceElement caller) {
        String tag = "%s| %s"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        String str = String.format("%-28s", String.format("(%s:%d)", caller.getFileName(), caller.getLineNumber()));
        String str2 = String.format("%-38s", String.format("%s.%s()", callerClazzName, caller.getMethodName()));
        str = str.replace(" ", "\b");
        str2 = str2.replace(" ", "\b");
        tag = String.format(tag, str, str2); // 替换
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }


    public static void d(String content) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.d(tag, content);
        } else {
            Log.d(tag, content);
        }
    }

    public static void d(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.d(tag, content, e);
        } else {
            Log.d(tag, content, e);
        }
    }

    public static void e(String content) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            Log.e(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void e(String tag, String content) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            Log.e(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void e(Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, "error", e);
        } else {
            Log.e(tag, e.getMessage(), e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, e.getMessage());
        }
    }

    public static void e(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content, e);
        } else {
            Log.e(tag, content, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, e.getMessage());
        }
    }

    public static void i(Object contentObj, Object... data) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }
        if(contentObj instanceof ArrayList) {array((ArrayList) contentObj);return ;}
        if(contentObj instanceof Object[]) {array((Object[]) contentObj);return ;}
        String content = contentObj.toString();
        content = String.format(content, data);
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            Log.i(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }
    public static void array(ArrayList contentObj) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }
        String content = "";
        for(Object object:contentObj){
            content+=object.toString()+" ; ";
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            Log.i(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }
    public static void array(Object... contentObj) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }
        String content = "";
        for(Object object:contentObj){
            content+=String.valueOf(object)+" ; ";
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            Log.i(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }
    public static void i(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content, e);
        } else {
            Log.i(tag, content, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void v(String content) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.v(tag, content);
        } else {
            Log.v(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void v(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.v(tag, content, e);
        } else {
            Log.v(tag, content, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void w(String content) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, content);
        } else {
            Log.w(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void w(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, content, e);
        } else {
            Log.w(tag, content, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void w(Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, e);
        } else {
            Log.w(tag, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, e.toString());
        }
    }

    public static void wtf(String content) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, content);
        } else {
            Log.wtf(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void wtf(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, content, e);
        } else {
            Log.wtf(tag, content, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void wtf(Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, e);
        } else {
            Log.wtf(tag, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, e.toString());
        }
    }

    public static void isNull(Object... objs) {
        if (objs != null) return;
        if (objs.length == 0) return;
        for (Object obj : objs) {
            String content = "This Object is Null!";
            if (!DebugUtils.isDebugBuild()) {
                return;
            }

            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);

            if (customLogger != null) {
                customLogger.e(tag, content);
            } else {
                Log.e(tag, content);
            }
            if (isSaveLog) {
                point(LOG_PATH, tag, content);
            }
        }
    }

    public static void isNoNull(Object obj) {
        if (obj == null) return;
        String content = "This Object is not Null!";
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            Log.e(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    /**
     * 获得调用方法所被调用的父方法的数据
     *
     * @return
     */
    public static String getCallerLocation() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String className = stackTraceElement.getFileName();
        String methodName = stackTraceElement.getMethodName();
        int line = stackTraceElement.getLineNumber();
        return String.format("(%s:%d)", className, line);
    }

    /**
     * 获得调用方法所被调用的父方法的数据
     *
     * @return
     */
    public static String getCallerLocation(int off) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4 + off];
        String className = stackTraceElement.getFileName();
        String methodName = stackTraceElement.getMethodName();
        int line = stackTraceElement.getLineNumber();
        return String.format("(%s:%d)", className, line);
    }


    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void point(String path, String tag, String msg) {
        if (isSDAva()) {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            path = path + "/logs/log-" + time + "-" + timestamp + ".log";

            File file = new File(path);
            if (!file.exists()) {
                createDipPath(path);
            }
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                out.write(time + " " + tag + " " + msg + "\r\n");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据文件路径 递归创建文件
     */
    public static void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String format(String msg, Object... args) {
        ReusableFormatter formatter = thread_local_formatter.get();
        return formatter.format(msg, args);
    }

    private static boolean isSDAva() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || Environment
                .getExternalStorageDirectory().exists();
    }

    public interface CustomLogger {
        void d(String tag, String content);

        void d(String tag, String content, Throwable e);

        void e(String tag, String content);

        void e(String tag, String content, Throwable e);

        void i(String tag, String content);

        void i(String tag, String content, Throwable e);

        void v(String tag, String content);

        void v(String tag, String content, Throwable e);

        void w(String tag, String content);

        void w(String tag, String content, Throwable e);

        void w(String tag, Throwable tr);

        void wtf(String tag, String content);

        void wtf(String tag, String content, Throwable e);

        void wtf(String tag, Throwable tr);
    }

    private static class ReusableFormatter {

        private Formatter formatter;
        private StringBuilder builder;

        public ReusableFormatter() {
            builder = new StringBuilder();
            formatter = new Formatter(builder);
        }

        public String format(String msg, Object... args) {
            formatter.format(msg, args);
            String s = builder.toString();
            builder.setLength(0);
            return s;
        }
    }
}