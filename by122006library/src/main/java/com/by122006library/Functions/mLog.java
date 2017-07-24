package com.by122006library.Functions;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.by122006library.Enum.mLog2;
import com.by122006library.Utils.DebugUtils;
import com.by122006library.Utils.ReflectionUtils;
import com.me.weishu.epic.Hook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

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
    private static final String FLAG_NATIVE_METHOD = "at java.lang.reflect.Method.invoke(Native Method)";
    public static String customTagPrefix = "";    // 自定义Tag的前缀，可以是作者名
    public static boolean isSaveLog = false;    // 是否把保存日志到SD卡中
    /**
     * 自定义的logger
     */
    public static CustomLogger customLogger;
    /**
     * 设置不显示该类内部tag的Log关键字
     */
    static ArrayList<String> takeOutList;
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
    private static int MoreAttCharsNum = 50;

    static {
        setOutLog("");
    }

    private mLog() {
    }

    /**
     * 设置该类的Log不会被显示
     */
    public static void setClassOutLog() {
        setOutLog(ReflectionUtils.getSmallClassName(getCallerStackTraceElement().getClassName()) + ".");
    }

    /**
     * 设置该方法的Log不会被显示
     */
    public static void setMethodOutLog() {
        setOutLog(ReflectionUtils.getSmallClassName(getCallerStackTraceElement().getClassName()) + "." + ReflectionUtils.getSmallClassName(getCallerStackTraceElement().getMethodName()) + "()");
    }


    /**
     * 设置该文件的Log不会被显示
     */
    public static void setFileOutLog() {
        setOutLog("(" + getCallerStackTraceElement().getFileName());
    }

    /**
     * 设置Log的Tag为str的不会被显示<br>
     * 一般为方法名、类名、文件名
     */
    public static void setOutLog(String str) {
        if (takeOutList == null) {
            takeOutList = new ArrayList<>();
            setFileOutLog();
            takeOutList.add("Hook.java");
            takeOutList.add("MyException.java");
            takeOutList.add("Thread.java");
            return;
        }
        takeOutList.add(str);
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

        String tag = getTag();

        if (customLogger != null) {
            customLogger.d(tag, content);
        } else {
            D(tag, content);
        }
    }

    public static void d(String content, String str) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

        if (customLogger != null) {
            customLogger.d(tag, content);
        } else {
            D(tag, content);
        }
    }

    public static void d(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

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

        String tag = getTag();

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            E(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void e(String tag, String content) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        tag = getTag();

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            E(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void e(Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

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

        String tag = getTag();

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
        if (contentObj instanceof ArrayList) {
            array((ArrayList) contentObj);
            return;
        }
        if (contentObj instanceof Object[]) {
            array((Object[]) contentObj);
            return;
        }
        String content = contentObj.toString();
        content = String.format(content, data);
        String tag = getTag();
        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            I(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    @NonNull
    private static String getTag() {
        String tag = "unKnown";
        StackTraceElement[] callers = Thread.currentThread().getStackTrace();
        for (int i = 4; i < callers.length; i++) {
            tag = generateTag(callers[i]);
            boolean canShow = true;
            for (String str : takeOutList) {
                if (tag.contains(str)) {
                    canShow = false;
                    break;
                }
            }
            if (canShow) break;
        }
        return tag;
    }

    public static void array(ArrayList contentObj) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }
        String content = "";
        for (Object object : contentObj) {
            content += object.toString() + " ; ";
        }
        String tag = getTag();

        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            I(tag, content);
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
        for (Object object : contentObj) {
            content += String.valueOf(object) + " ; ";
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            I(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    /**
     * 原生log替换功能开启方法<br>
     * 调用该方法后原生Log.x(String,String)会被重定位到对应mLog方法中<br>
     * 该方法在程序中仅可使用一次，请不要重复调用该方法
     */
    public static void autoReplaceLog() {
        autoReplaceLog("widev");
    }

    /**
     * 原生log替换功能开启方法<br>
     * 调用该方法后原生Log.x(String,String)会被重定位到对应mLog方法中<br>
     * 该方法在程序中仅可使用一次，请不要重复调用该方法
     *
     * @param replageStyle 需要替换的方法名集合字符串 <br>
     *                     eg."widev" 或 "wv" 或 "w,v,i" 或 "w;v;i"<br>
     *                     分隔符会自动忽略<br>顺序无关<br>不可重复
     */
    public static void autoReplaceLog(String replageStyle) {
        replageStyle = replageStyle.toLowerCase();
        for (int i = 0; i < replageStyle.length(); i++) {
            String c = Character.toString(replageStyle.charAt(i));
            if (!"widev".contains(c)) continue;
            try {
                Method m_o = ReflectionUtils.getDeclaredMethod(Log.class, c, String.class, String.class);
                Method m_n = ReflectionUtils.getDeclaredMethod(mLog2.class, c, String.class, String.class);
                Hook.hook(m_o,m_n);
                try {
                    switch (c) {
                        case "i":
                            Log.i("如果看到该tag，说明替换失败", String.format("\"Log.%s()\"方法替换成功", c));
                            break;
                        case "w":
                            Log.w("如果看到该tag，说明替换失败", String.format("\"Log.%s()\"方法替换成功", c));
                            break;
                        case "e":
                            Log.e("如果看到该tag，说明替换失败", String.format("\"Log.%s()\"方法替换成功", c));
                            break;
                        case "v":
                            Log.v("如果看到该tag，说明替换失败", String.format("\"Log.%s()\"方法替换成功", c));
                            break;
                        case "d":
                            Log.d("如果看到该tag，说明替换失败", String.format("\"Log.%s()\"方法替换成功", c));
                            break;
                    }
                } catch (NoSuchMethodError e) {
                    mLog.e(String.format("\"Log.%s()\"方法替换失败", c));

                }
                mLog.e("cc","vv");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //混淆压缩保护
        if (true) return;
        i_ForReplace("", "");
        e_ForReplace("", "");
        w_ForReplace("", "");
        v_ForReplace("", "");
        d_ForReplace("", "");

    }

    public static int i_ForReplace(String tag, String content) {
        i(content);
        return 0;
    }

    public static int e_ForReplace(String tag, String content) {
        e(content);
        return 0;
    }

    public static int w_ForReplace(String tag, String content) {
        w(content);
        return 0;
    }

    public static int v_ForReplace(String tag, String content) {
        v(content);
        return 0;
    }

    public static int d_ForReplace(String tag, String content) {
        d(content);
        return 0;
    }

    public static void i(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

        if (customLogger != null) {
            customLogger.i(tag, content, e);
        } else {
            Log.i(tag, content, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static int W(String tag, String content) {
        return Log.println(Log.WARN, tag, content);
    }

    public static int I(String tag, String content) {
        return Log.println(Log.INFO, tag, content);
    }

    public static int E(String tag, String content) {
        return Log.println(Log.ERROR, tag, content);
    }

    public static int D(String tag, String content) {
        return Log.println(Log.DEBUG, tag, content);
    }

    public static int V(String tag, String content) {
        return Log.println(Log.VERBOSE, tag, content);
    }

    public static void mark() {
        DebugUtils.runningDurtime();
    }

    public static void v(String content) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

        if (customLogger != null) {
            customLogger.v(tag, content);
        } else {
            V(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void v(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

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
        String tag = getTag();

        if (customLogger != null) {
            customLogger.w(tag, content);
        } else {
            W(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void w(String content, Throwable e) {
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

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

        String tag = getTag();

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

        String tag = getTag();

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

        String tag = getTag();

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

        String tag = getTag();

        if (customLogger != null) {
            customLogger.wtf(tag, e);
        } else {
            Log.wtf(tag, e);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, e.toString());
        }
    }

    public static void isNull(Object obj) {
        if (obj != null) return;
        String content = "This Object is Null!";
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            E(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void isNoNull(Object obj) {
        if (obj == null) return;
        String content = "This Object is not Null!";
        if (!DebugUtils.isDebugBuild()) {
            return;
        }

        String tag = getTag();

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            E(tag, content);
        }
        if (isSaveLog) {
            point(LOG_PATH, tag, content);
        }
    }

    public static void more(String main, Object... att) {
        more(Log.INFO, main, att);
    }

    public static void more(int level, String main, Object... objs) {
        String[] att = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            att[i] = String.valueOf(objs[i]);
        }
//        ArrayList<String> arrayList= (ArrayList<String>) Arrays.asList(att);
        int MoreAttCharsNum = mLog.MoreAttCharsNum;
        for (String a : att) {
            MoreAttCharsNum = (int) Math.max(mLog.MoreAttCharsNum * 1.5, a.length());
        }
        //弹性长度判断
        if (MoreAttCharsNum > mLog.MoreAttCharsNum && MoreAttCharsNum < mLog.MoreAttCharsNum * 1.5) {
            MoreAttCharsNum = mLog.MoreAttCharsNum;
        }
        if (MoreAttCharsNum % 2 == 1) MoreAttCharsNum--;
        StringBuilder str = new StringBuilder();
        if (objs != null && objs.length != 0) str.append("✔ ");
        str.append(main);
        if (objs != null && objs.length != 0) {
            str.append("\n╔");
            for (int i = 0; i < MoreAttCharsNum / 2; i++) {
                str.append("═");
            }
            str.append("╗");
            str.append(FLAG_NATIVE_METHOD);
            int blank = 0;//当前前置空格数
            for (int i = 0; i < att.length; i++) {
                att[i] = att[i].replace("\n", "");
                str.append("\n║");
                String show = String.format("%-" + MoreAttCharsNum + "s", att[i]);
                if (blank == 0) {
                    blank = show.indexOf("=") + 1;
                    int blank2 = show.indexOf(":") + 1;
                    if (blank2 != -1 && (blank < blank2 || (blank == -1 && blank2 != -1)))
                        blank = blank2;
                    blank = Math.min(blank, MoreAttCharsNum);
                }
                show = show.substring(0, MoreAttCharsNum);
                str.append(show);
                if (att[i].length() > show.length()) {
                    StringBuilder n = new StringBuilder();
                    for (int j = 0; j < blank; j++) {
                        n.append(" ");
                    }
                    n.append(att[i].replace(show, ""));
                    att[i] = n.toString();
                    i--;
                } else {
                    blank = 0;
                }
                str.append("║");
                str.append(FLAG_NATIVE_METHOD);
            }
            str.append("\n╚");
            for (int i = 0; i < MoreAttCharsNum / 2; i++) {
                str.append("═");
            }
            str.append("╝");
            str.append(FLAG_NATIVE_METHOD);
        }
        switch (level) {
            case Log.INFO:
                mLog.i(str.toString());
                break;
            case Log.VERBOSE:
                mLog.v(str.toString());
                break;
            case Log.DEBUG:
                mLog.d(str.toString());
                break;
            case Log.WARN:
                mLog.w(str.toString());
                break;
            case Log.ERROR:
                mLog.e(str.toString());
                break;
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

    public static StackTraceElement getCallerStackTraceElement(int off) {
        return Thread.currentThread().getStackTrace()[4 + off];
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