package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.view.View;

import com.by122006library.web.Web;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */

public class SF_AspectThread extends SF {
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;

    @Override
    public ArrayList<SMethod> specialMethods() {
        ArrayList<SMethod> list = new ArrayList<SMethod>();
        SMethod sMethod = SMethod.debug();
        sMethod.setDescrite("调用UI线程使用范例");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========调用UI线程使用范例============");
                println(" @com.by122006library.Interface.UIThread", Color.RED);
                println("  public void 任意方法名(参数) {");
                println("      ...//代码主体内容");
                println("  }");
                println("//与SmartRun不同，不需要增加主动切换线程代码，但是两者同时使用不会报错", Color.RED);
                println("//UIThread 可以替换为系统注释中的UiThread", Color.RED);
            }
        });
        list.add(sMethod);
        sMethod = SMethod.debug();
        sMethod.setDescrite("调用后台线程使用范例");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========调用后台线程使用范例============");
                println(" @com.by122006library.Interface.BGThread", Color.RED);
                println("  public void 任意方法名(参数) {");
                println("      ...//代码主体内容");
                println("  }");
                println("//与SmartRun不同，不需要增加主动切换线程代码，但是两者同时使用不会报错", Color.RED);
                println("//BGThread 可以替换为系统注释中的BgThread", Color.RED);
            }
        });
        list.add(sMethod);
        sMethod = SMethod.debug();
        sMethod.setDescrite("不支持默认线程调用");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========不支持默认线程调用============");
                println("//SmartRun用下来感觉这个并没有啥用。。。", Color.RED);

            }
        });
        list.add(sMethod);
        sMethod = SMethod.debug();
        sMethod.setDescrite("使用异步后台线程");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========使用异步后台线程============");
                println(" @com.by122006library.Interface.BGThread");
                println(" @Async", Color.RED);
                println("  public void 任意方法名(参数) {");
                println("      ...//代码主体内容");
                println("  }");

            }
        });
        list.add(sMethod);

        sMethod = SMethod.debug();
        sMethod.setDescrite("框架相关");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========框架相关============");
                println("切换UI线程及当前线程判断需要本框架支持，如果非框架使用，需要完成框架的外部Application注册。");
                println("当然，你现在也可以设置ThreadUtils的thisAct为当前Activity，进行自行维护");


            }
        });
        list.add(sMethod);

        sMethod = SMethod.debug();
        sMethod.setDescrite("Aspect开启方法");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========Aspect开启方法============");
                println("该功能需要开启Aspect");
                println("Step 1 : ", Color.RED);
                println("  请在项目的主build.gradle中的buildscript{dependencies{xxxxx}}增加以下依赖");
                println("  classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:1.0.10'");
                println("Step 2 : ", Color.RED);
                println("  请在项目的app模块的build.gradle的起始部位增加以下代码");
                println("  apply plugin: 'com.hujiang.android-aspectjx'");
//                println("Step 3 : ", Color.RED);
//                println("  请在项目的app模块增加以下依赖");
//                println("   compile 'com.safframework:saf-aop:1.1.3'");
            }
        });
        list.add(sMethod);


        sMethod = SMethod.debug();
        sMethod.setDescrite("Aspect注意事项");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========Aspect注意事项============");
                println("Aspect是aop编程中重要的组成部分");
                println("No 1 : ", Color.RED);
                println("  Aspect原理是编译时插入，不会带来运行性能的影响");
                println("No 2 : ", Color.RED);
                println("  由于在混淆前修改代码，不会影响混淆。但是其他功能会使用反射导致必须反混淆");
                println("No 3 : ", Color.RED);
                println("  会严重影响编译速度，虽然使用了增量更新，但还是会使编译时间增加3s~80s");
                println("No 4 : ", Color.RED);
                println("  请考虑关闭AS的Instant Run功能");
            }
        });
        list.add(sMethod);


        return list;
    }

    @Override
    public String getFunctionEnName() {
        return "AspectThread";
    }

    @Override
    public String getFunctionChName() {
        return "Aspect线程切换";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"核心", "拓展", "常用", "框架", "AOP", "Aspect", "注解", "一行代码", "非侵入"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "注释任何方法上，可以使方法运行于不同的线程\n使用aspect在编译时对代码进行注入\n不使用反射，不会对运行性能有任何影响";
    }

    @Override
    public Object getFunctionObject() {
        return Object.class;
    }
}
