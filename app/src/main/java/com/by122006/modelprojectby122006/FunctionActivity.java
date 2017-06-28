package com.by122006.modelprojectby122006;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.by122006.annotation.Attribute;
import com.by122006.annotation.Subclass;
import com.by122006library.Activity.BaseActivity;

import static com.by122006.modelprojectby122006.R.color.material_blue_grey_950;

/**
 * Created by admin on 2017/6/12.
 */
public abstract class FunctionActivity extends BaseActivity {
    boolean FLAG_ACT_NO_TITLE = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getFunctionEnName()+" "+getFunctionChName());
    }

    Object functionObject;
    public abstract String getFunctionEnName();
    public abstract String getFunctionChName();
    public abstract String[] getFuncitonTags();
    public abstract String getFuncitonDescribe();
    public abstract void loadFuncitonCachePhoto(ImageView iv);
    public abstract void clickItem();

    public abstract Object getFunctionObject();


}
