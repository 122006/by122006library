package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.view.View;

import com.by122006library.Functions.SmartRun;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */

public class SF_SmartRun extends SF {
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
                println("      if(SmartRun.sPrepare(this[,参数])) return;//首行必填", Color.RED);
                println("      ...//代码主体内容");
                println("  }");

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
                println("      if(SmartRun.sPrepare(this[,参数])) return;//首行必填", Color.RED);
                println("      ...//代码主体内容");
                println("  }");

            }
        });
        list.add(sMethod);
        sMethod = SMethod.debug();
        sMethod.setDescrite("调用默认线程使用范例");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========调用默认线程使用范例============");
                println(" @DefaultThread", Color.RED);
                println("  public void 任意方法名(参数) {");
                println("      if(SmartRun.sPrepare(this[,参数])) return;//首行必填", Color.RED);
                println("      ...//代码主体内容");
                println("  }");

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
                println("      if(SmartRun.sPrepare(this[,参数])) return;//首行必填", Color.RED);
                println("      ...//代码主体内容");
                println("  }");

            }
        });
        list.add(sMethod);
        return list;
    }

    @Override
    public String getFunctionEnName() {
        return "SmartRun";
    }

    @Override
    public String getFunctionChName() {
        return "智能线程";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"核心", "原创", "常用","框架", "智能", "代码修复", "注解", "堆栈回调", "一行代码"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "库核心内容，帮助使用者快捷切换方法运行线程。\n低学习使用时间成本，易用性远高于同类三方库\n同时具有一定代码自维护能力减少debug次数";
    }

    @Override
    public Object getFunctionObject() {
        return SmartRun.class;
    }
}
