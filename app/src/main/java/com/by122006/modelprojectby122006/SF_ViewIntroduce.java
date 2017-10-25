package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import com.by122006library.Functions.ViewIntroduce.IntroducesMap;
import com.by122006library.Functions.ViewIntroduce.ViewIntroduce;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */

public class SF_ViewIntroduce extends SF {
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    public ArrayList<SMethod> specialMethods() {

        ArrayList<SMethod> list = new ArrayList<SMethod>();
        SMethod sMethod = SMethod.debug();
        sMethod.setDescrite("圆形控件引导");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========圆形控件引导============");
                println("为控件产生圆形遮罩");
                println("ViewIntroduce.create(控件View).setText(\"产生一个圆形控件引导\").setShapeStyle(ViewIntroduce.ShapeStyle.Circle).show();", Color.RED);
                println("显示的文字位置可以根据控件智能调整;", Color.GRAY);
                ViewIntroduce.create(findViewById(R.id.refresh)).setText("产生一个圆形控件引导").setShapeStyle(ViewIntroduce.ShapeStyle.Circle).show();
            }
        });
        list.add(sMethod);

        sMethod = SMethod.debug();
        sMethod.setDescrite("矩形控件引导");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========矩形控件引导============");
                println("为控件产生矩形遮罩");
                println("ViewIntroduce.create(控件View).setShapeStyle(ViewIntroduce.ShapeStyle.Rect).show();", Color.RED);
                println("显示的文字位置可以根据控件智能调整;", Color.GRAY);
                ViewIntroduce.create(findViewById(R.id.refresh)).setText("产生一个矩形控件引导").setShapeStyle(ViewIntroduce.ShapeStyle.Rect).show();
            }
        });
        list.add(sMethod);


        sMethod = SMethod.debug();
        sMethod.setDescrite("圆角矩形引导");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========圆角矩形引导============");
                println("为控件产生圆角矩形遮罩");
                println("ViewIntroduce.create(控件View).setShapeStyle(ViewIntroduce.ShapeStyle.RoundRect).show();", Color.RED);
                println("显示的文字位置可以根据控件智能调整;", Color.GRAY);
                ViewIntroduce.create(findViewById(R.id.refresh)).setText("产生一个圆角矩形控件引导").setShapeStyle(ViewIntroduce.ShapeStyle.RoundRect).show();
            }
        });
        list.add(sMethod);


        sMethod = SMethod.debug();
        sMethod.setDescrite("顺序引导");

        sMethod.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                println("\n===========顺序引导============");
                println("生成多个控件的引导，顺序显示");
                println("ViewIntroduce i1=ViewIntroduce.create(控件View).setText(\"控件引导1\").setShapeStyle(ViewIntroduce.ShapeStyle.Circle);", 0xffbb0000);
                println("ViewIntroduce i2=ViewIntroduce.create(控件View).setText(\"控件引导2\").setShapeStyle(ViewIntroduce.ShapeStyle.Rect);",0xffbb0000);
                println("ViewIntroduce i3=ViewIntroduce.create(控件View).setText(\"控件引导3\").setShapeStyle(ViewIntroduce.ShapeStyle.RoundRect);", 0xffbb0000);
                println("IntroducesMap.create(i1,i2,i3).show();", Color.RED);
                ViewIntroduce i1=ViewIntroduce.create(findViewById(R.id.refresh)).setText("控件引导1").setShapeStyle(ViewIntroduce.ShapeStyle.Circle);
                ViewIntroduce i2=ViewIntroduce.create(findViewById(R.id.clear)).setText("控件引导2").setShapeStyle(ViewIntroduce.ShapeStyle.Rect);
                ViewIntroduce i3=ViewIntroduce.create(findViewById(R.id.layout_refresh_durtime)).setText("控件引导3").setShapeStyle(ViewIntroduce.ShapeStyle.RoundRect);
                IntroducesMap.create(i1,i2,i3).show();
            }
        });
        list.add(sMethod);


        return list;
    }

    @Override
    public String getFunctionEnName() {
        return "ViewIntroduce";
    }

    @Override
    public String getFunctionChName() {
        return "动态功能引导";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"引导", "链式调用", "智能适配", "简洁", "组合功能"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "库核心内容，提供了循环任务延迟任务的管理功能。\n可以定义任务的开始延迟、循环次数、间隔、所在线程、覆盖模式。\n支持任务的批量管理、自动释放";
    }

    @Override
    public Object getFunctionObject() {
        return null;
    }
}
