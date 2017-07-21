package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.view.View;

import com.by122006library.Functions.SmartRun;
import com.by122006library.Functions.mLog;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */

public class SF_mLog extends SF {
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;

    @Override
    public ArrayList<SMethod> specialMethods() {
        ArrayList<SMethod> list = new ArrayList<SMethod>();
        SMethod sMethod = SMethod.create(mLog.class,"i",Object.class,Object[].class);
        sMethod.setDescrite("调用info级别日志结果使用范例");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========调用info级别日志结果使用范例");
                println("代码中内容：", Color.BLUE);
                println("mLog.i(setContentView());");
                println("AS中logcat输出：", Color.BLUE);
                println("I/(BaseActivity.java:540)\b\b\b\b\b| BaseActivity.setContentView()\b\b\b\b\b\b\b\b\b: " +
                        "setContentView()完成");
                println("由结果可知：该行代码存在于 BaseActivity.java文件中 540行，所在的类名为BaseActivity，所在的方法名为setContentView()", Color.BLUE);
                println("你可以直接点击蓝字部分直接打开对应文件、并定位至指定代码！", Color.RED);
                println("显示内容可以是任意对象，会自动转化为String,你也可以在其后增加参数以支持format格式的内容( int 不需要手动转化为 String )", Color.BLUE);
                println("在正式包中，该log不会显示！", Color.BLUE);

            }
        });
        list.add(sMethod);

        sMethod = SMethod.create(mLog.class,"isNull",Object.class);
        sMethod.setDescrite("判空时输出空值提示");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========判空时输出空值提示 示例");
                println("I/(XX.java:XX)\b\b\b\b\b| XX.XX()\b\b\b\b\b\b\b\b\b: " +
                        "XX is Null");
                println("只有参数为空值时才会显示", Color.BLUE);
                println("在正式包中，该log不会显示！", Color.BLUE);
            }
        });
        list.add(sMethod);
        sMethod = SMethod.create(mLog.class,"isNoNull",Object.class);
        sMethod.setDescrite("判非空时输出非空值提示");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========判非空时输出非空值提示 示例");
                println("I/(XX.java:XX)\b\b\b\b\b| XX.XX()\b\b\b\b\b\b\b\b\b: " +
                        "XX is Not Null");
                println("只有参数为非空值时才会显示", Color.BLUE);
                println("在正式包中，该log不会显示！", Color.BLUE);
            }
        });
        list.add(sMethod);
        sMethod = SMethod.create(mLog.class,"array",Object[].class);
        sMethod.setDescrite("并列输出/数组输出");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========并列输出/数组输出");
                println("可以在同一行代码里输出多个数据或一个数组！");
                println("在正式包中，该log不会显示！");
            }
        });
        list.add(sMethod);
        sMethod = SMethod.create(mLog.class,"mark");
        sMethod.setDescrite("行标记");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========行标记");
                println("快捷标记该行，输出指定内容以供调试");
                println("在正式包中，该log不会显示！");
            }
        });
        list.add(sMethod);
        sMethod = SMethod.create(mLog.class,"getCallerLocation");
        sMethod.setDescrite("获得父方法名");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========获得父方法名");
                println("返回一个父方法名，你可以将该字符串通过其他方法输出以查看所在方法的调用者");
            }
        });
        list.add(sMethod);
        sMethod = SMethod.create(mLog.class,"autoReplaceLog",String.class);
        sMethod.setInitParams("widve");
        sMethod.setDescrite("全局替换Log");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========全局替换Log");
                println("在Application中调用该方法并传入\"widve\"的组合配置");
                println("可以替换原有代码中所有系统Log调用");
            }
        });
        list.add(sMethod);
        sMethod = SMethod.create(mLog.class,"more",String.class,String[].class);
        sMethod.setInitParams("标题",new String[]{"xx=xx","xx=xx"});
        sMethod.setDescrite("折叠型参数");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========折叠型参数");
                println("一个在AS的Logcat中可以折叠显示附加参数代码");
                println("支持长代码换行");
            }
        });
        list.add(sMethod);
        return list;
    }

    @Override
    public String getFunctionEnName() {
        return "mLog";
    }

    @Override
    public String getFunctionChName() {
        return "超级日志";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"常用", "原创", "拓展", "定位", "格式化", "代码分析", "一行代码", "Hook", "折叠"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "强健的日志输出类，不需要额外配置，自动定位代码类名方法名行号、AS中一键抵达，正式包自动隐藏。\n" +
                "含有多种方法帮助使用者判断空值、生成format字符串、显示数组、显示多个数据值、显示所在方法调用来源。\n" +
                "支持一行替换原有系统Log。\n支持折叠式属性日志。";
    }

    @Override
    public Object getFunctionObject() {
        return mLog.class;
    }
}
