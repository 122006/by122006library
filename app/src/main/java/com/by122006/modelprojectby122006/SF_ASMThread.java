package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */

public class SF_ASMThread extends SF {
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
                println("//BGThread 可以替换为系统注释中的BgThread", Color.RED);
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

//            sMethod = SMethod.debug();
//        sMethod.setDescrite("ASM使用方法");
//        sMethod.setListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                println("\n===========ASM使用方法============");
//                println("Step 1 : ", Color.RED);
//                println("  请在项目的主build.gradle中的buildscript{dependencies{xxxxx}}增加以下依赖");
//                println("  classpath 'com.hujiang.ASMjx:gradle-android-plugin-ASMjx:1.0.10'");
//                println("Step 2 : ", Color.RED);
//                println("  请在项目的app模块的build.gradle的起始部位增加以下代码");
//                println("  apply plugin: 'com.hujiang.android-ASMjx'");
////                println("Step 3 : ", Color.RED);
////                println("  请在项目的app模块增加以下依赖");
////                println("   compile 'com.safframework:saf-aop:1.1.3'");
//            }
//        });
//        list.add(sMethod);


        sMethod = SMethod.debug();
        sMethod.setDescrite("ASM注意事项");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========ASM注意事项============");
                println("No 1 : ", Color.RED);
                println("  ASM原理是编译时插入，不会带来运行性能的影响");
                println("No 2 : ", Color.RED);
                println("  由于在混淆前修改代码，不会影响混淆。");
                println("No 3 : ", Color.RED);
                println("  请考虑关闭AS的Instant Run功能");
            }
        });
        list.add(sMethod);


        return list;
    }

    @Override
    public String getFunctionEnName() {
        return "ASMThread";
    }

    @Override
    public String getFunctionChName() {
        return "ASM线程切换";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"核心", "拓展", "常用", "框架", "JVM", "字节码", "Gradle插件", "AOP", "ASM", "注解", "非侵入"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "注释任何方法上，可以使方法运行于不同的线程\n使用ASM在编译时对代码进行注入\n不使用反射，不会对运行性能有任何影响\n与AspectThread相比编译时间更短";
    }

    @Override
    public Object getFunctionObject() {
        return Object.class;
    }
}
