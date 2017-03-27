package com.by122006library.View;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.by122006library.R;

/**
 * Created by admin on 2017/3/27.
 */

public class TitleBar {
    FrameLayout frameLayout;
    TitleBar(FrameLayout frameLayout){
        this.frameLayout=frameLayout;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        ((TextView)frameLayout.findViewById(R.id.title)).setText(title);
    }
    String title;
    public void show(){
        frameLayout.setVisibility(View.VISIBLE);
    }
    public void gone(){
        frameLayout.setVisibility(View.GONE);
    }
}
