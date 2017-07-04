package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Functions.CycleTask.DelayTask;
import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */

public class SF_CycleTask extends SF {
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;
    CycleTask cycleTask = new CycleTask(500, 2000, CycleTask.CircleForever) {
        @Override
        @BGThread
        public void doCycleAction(int haveCycleCount) throws MyException {
            //任务主体内容
            SF_CycleTask.this.restTime = System.currentTimeMillis();
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
        }
    };
    long restTime = 0;
    long startTime = 0;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    public ArrayList<SMethod> specialMethods() {

        ArrayList<SMethod> list = new ArrayList<SMethod>();
        SMethod sMethod = SMethod.debug();
        sMethod.setDescrite("循环任务范例-无运行");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                println("\n===========循环任务使用范例============");
                println("已创建延迟500ms、循环2000s、循环次数无限次的循环事件");
                println(" new CycleTask(500,2000,CycleTask.CircleForever) {", Color.RED);
                println("  @UIThread");
                println("      public void doCycleAction(int haveCycleCount) throws MyException {");
                println("      ...//任务主体内容");
                println("      }");
                println(" }.register(this);<font color=\"#888888\">//注册该任务进入序列，绑定的Tag为当前对象</font> ");
            }
        });
        list.add(sMethod);

        sMethod = SMethod.debug();
        sMethod.setDescrite("添加监听");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataObserver dataObserver = new DataObserver() {
                    @Override
                    String observer() {
                        return "运行状态：\n" + cycleTask.getState().name() + (cycleTask.getState() == CycleTask.TaskState
                                .Running ? String.format("\n任务运行：%dms", System.currentTimeMillis()-restTime) : "");
                    }
                }.register();
                dataObserver = new DataObserver() {
                    @Override
                    String observer() {
                        return String.format((cycleTask.getState().ordinal()<= CycleTask.TaskState.Delay.ordinal() ? "延迟时间:\n%d" : "周期剩余时间:\n%d") + "\n循环次数:\n%d",
                                cycleTask.getState().ordinal()<= CycleTask.TaskState.Delay.ordinal() ? cycleTask.delayTime : cycleTask.restTime, cycleTask
                                        .cycleCount);
                    }
                }.register();
            }
        });
        list.add(sMethod);

        sMethod = SMethod.create(cycleTask, "register", Object.class);
        sMethod.setDescrite("注册已创建的循环任务");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cycleTask.getState() == CycleTask.TaskState.Register) {
                    println("该任务已经被注册，重复注册会被自动忽略",Color.BLUE);
                    return;
                }

                startTime=System.currentTimeMillis();
                DataObserver dataObserver = new DataObserver() {
                    @Override
                    String observer() {
                        return "基准差：\n" + (cycleTask.mCurDurtime-cycleTask.cycleTime+"ms");
                    }
                }.register();

                cycleTask.register(SF_CycleTask.this);
                println("\n===========注册已创建的循环任务============");
            }
        });
        list.add(sMethod);

        sMethod = SMethod.create(cycleTask, "unRegister");
        sMethod.setDescrite("结束已创建的循环任务");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cycleTask == null) {
                    println("请先调用指令创建循环任务", Color.RED);
                    return;
                }
                cycleTask.unRegister();
                println("\n===========结束已创建的循环任务============");
            }
        });
        list.add(sMethod);

        sMethod = SMethod.create(CycleTask.class, "unRegister", Object.class);
        sMethod.setDescrite("结束tag为当前界面的循环任务");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cycleTask.getState() == CycleTask.TaskState.Create) {
                    println("请先调用指令注册循环任务", Color.RED);
                    return;
                }
                CycleTask.unRegister(SF_CycleTask.this);
                println("\n===========结束tag为当前界面的循环任务============");
            }
        });
        list.add(sMethod);

        sMethod = SMethod.create(cycleTask, "again");
        sMethod.setDescrite("重试任务");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cycleTask.getState() == CycleTask.TaskState.Create) {
                    println("请先调用指令注册循环任务", Color.RED);
                    return;
                }
                cycleTask.again();
                println("\n===========重试任务============");
                println("本次循环结束后立刻进行下次任务，同时可运行回合数+1(额外运行，不占用原本次数)\n用于出错后重试\n必须等待本周期循环结束之后才会进行重试");

            }
        });
        list.add(sMethod);

        sMethod = SMethod.debug();
        sMethod.setDescrite("延迟任务");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========延迟任务============");
                println("添加了一个3s的延迟任务 具体代码如下",Color.BLUE);
                println(" new DelayTask(3000){");
                println("     @UIThread");
                println("     public void doCycleAction(int haveCycleCount) throws MyException {");
                println("         //输出\"延迟任务生效，UI线程输出文字\"");
                println("     }");
                println(" }.register(SF_CycleTask.this);");
                new DelayTask(3000){
                    @Override
                    @UIThread
                    public void doCycleAction(int haveCycleCount) throws MyException {
                        println("==延迟任务生效，UI线程输出文字",Color.BLUE);
                    }
                }.register(SF_CycleTask.this);

            }
        });
        list.add(sMethod);


        return list;
    }

    @Override
    public String getFunctionEnName() {
        return "CycleTask";
    }

    @Override
    public String getFunctionChName() {
        return "循环任务";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"核心", "原创", "常用", "框架", "注解", "简洁代码", "批量管理", "内存释放"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "库核心内容，提供了循环任务延迟任务的管理功能。\n可以定义任务的开始延迟、循环次数、间隔、所在线程、覆盖模式。\n支持任务的批量管理、自动释放";
    }

    @Override
    public Object getFunctionObject() {
        return cycleTask;
    }
}
