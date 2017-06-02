package com.by122006.modelprojectby122006;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.zip.Inflater;

/**
 * Created by admin on 2017/5/27.
 */

public class HomeMenu {
    Activity activity;
    HomeMenu(Activity activity){
        this.activity=activity;
    }
    /**
     * 域布局
     */
    FrameLayout regionLayout;
    public void initRegionLayout(FrameLayout regionLayout){
        this.regionLayout=regionLayout;
        regionLayout.removeAllViews();
        regionLayout.addView(activity.getLayoutInflater().inflate(R.layout.homemenu,null));

    }

    ViewGroup homeLayout=null;
    public void setHomeLayout(ViewGroup viewGroup){
        homeLayout=viewGroup;
    }


     public void addMenuItem(){

     }

    public class MenuItem{

    }


}
