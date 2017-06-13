package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.os.Bundle;

import com.by122006.SubclassAttribute;
import com.by122006.annotation.Attribute;
import com.by122006.annotation.Subclass;
import com.by122006library.Activity.BaseActivity;

import static com.by122006.modelprojectby122006.R.color.material_blue_grey_950;

/**
 * Created by admin on 2017/6/12.
 */

@Subclass(att = {@Attribute(name = "title", type = String.class, defaultValue = "No Title!"),
        @Attribute(name = "bgColor", type = int.class, defaultValue = "0x7f0a0071")})
public class FunctionActivity extends BaseActivity {
    boolean FLAG_ACT_NO_TITLE = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getMenuTitle());
    }

    public String getMenuTitle(){
        return SubclassAttribute.with(this).getTitle();
    }

}