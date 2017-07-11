package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.view.View;

import com.by122006library.Functions.SmartRun;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */

public class SF_SmartRun2 extends SF {
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
        return "SmartRun2";
    }

    @Override
    public String getFunctionChName() {
        return "智能线程2";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"开发中", "延伸", "Hook","weishu", "APT", "注解", "动态", "极简代码"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "类似于1版。\n缺点：只能在非匿名类方法使用，和1版部分冲突，不能使用AS热更新和分包技术\n但是技术含量是真的高。。。";
    }

    @Override
    public Object getFunctionObject() {
        return SmartRun.class;
    }
}
