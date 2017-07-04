package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.support.annotation.FloatRange;
import android.view.View;

import com.by122006library.Functions.AttBinder.Att;
import com.by122006library.Functions.AttBinder.AttBinder;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */

public class SF_AttBinder extends SF {
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;
    AttBinder binder=new AttBinder();

    @Override
    public ArrayList<SMethod> specialMethods() {
        ArrayList<SMethod> list = new ArrayList<SMethod>();
        SMethod sMethod = SMethod.debug();
        sMethod.setDescrite("初始化AttBinder");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========初始化AttBinder");
                println("AttBinder binder=new AttBinder();");
            }
        });
        list.add(sMethod);

        sMethod = SMethod.debug();
        sMethod.setDescrite("自定义属性器");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========新建一个自定义属性器");
                Att att=new Att(1,0){
                    public double measureAtt() {
                        return 0;
                    }
                    public Att setAttNum(double num) {
                        return null;
                    }
                    public double transform(@FloatRange(from = 0, to = 1) double per) {
                        return 0;
                    }
                    public void setOutSideListener() {
                    }
                };
                println(" Att att=new Att(1,0){");
                println("     public double measureAtt() {");
                println("         //通过该属性的真实值测量方法，返回属性值",Color.BLUE);
                println("         return double;");
                println("     }");
                println("     public Att setAttNum(double num) {");
                println("         //设置该属性值的方法",Color.BLUE);
                println("         return this;");
                println("     }");
                println("     public double transform(double per) {");
                println("         //属性值的自定义偏移方法，为该属性设定独立属性值用",Color.BLUE);
                println("         //不修改请返回per本身",Color.BLUE);
                println("         return per;");
                println("     }");
                println("     public void setOutSideListener() {");
                println("         //设置外部监听，并在获得属性后调用fluctuation(num)方法",Color.BLUE);
                println("     }");
                println(" };");
                println("//setReverse(): 设置倒序",Color.BLUE);
                println("//属性的max参数和min参数的顺序会自动设置，属性会从小到大变化，如果要反向，请使用倒序",Color.BLUE);


            }
        });
        list.add(sMethod);


        sMethod = SMethod.debug();
        sMethod.setDescrite("View属性器");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========新建一个View属性器");
                println("ViewAtt viewAtt=new ViewAtt(binding.getNowV(), ViewAtt.AttStyle.Alpha,1,0);");
                println("//支持属性: Other, Height, Width,Alpha,LeftMargin,RightMargin,TopMargin,BottomMargin, Left, Right, Top, Bottom, ScrollX, " +
                        "ScrollY,TranslationX,TranslationY",Color.BLUE);
            }
        });
        list.add(sMethod);

        sMethod = SMethod.debug();
        sMethod.setDescrite("时间属性器");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========新建一个时间属性器");
                println("//时间属性器本身为数据入口，并且在完成后自动移除",Color.BLUE);
                println("//生成后会立即计数",Color.BLUE);
                println("TimeAtt timeAtt=new TimeAtt(3000);");
                println("//在一个AttBinder中只能存在一个TimeAtt属性器",Color.BLUE);
                println("//attBinder.getTimeAtt(): 你可以使用该方法获得绑定器内的时间属性器",Color.BLUE);
                println("//addTimeProgressAction(TimeProgressAction action): 你需要通过此方法来添加进度监听",Color.BLUE);
            }
        });
        list.add(sMethod);
        
        sMethod = SMethod.debug();
        sMethod.setDescrite("绑定至AttBinder");
        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n=========绑定至AttBinder");
                println("binder.bind(att1,att2,att3);");
                println("//绑定后会立即生效",Color.BLUE);
            }
        });
        list.add(sMethod);

        return list;
    }

    @Override
    public String getFunctionEnName() {
        return "AttBinder";
    }

    @Override
    public String getFunctionChName() {
        return "属性绑定器";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"常用", "原创", "动画", "链式", "简明"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "一个适用于但不限于动画效果的工具类\n" +
                "可以绑定多个控件或属性，同时进行修改以进行同步复杂的动画";
    }

    @Override
    public Object getFunctionObject() {
        return AttBinder.class;
    }
}
