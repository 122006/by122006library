package com.by122006library.Functions.ViewIntroduce;

import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by admin on 2017/9/5.
 */

public class IntroducesMap {
    ArrayList<ViewIntroduce> map;
    public IntroducesMap(ViewIntroduce... introduces){
        map=new ArrayList<>();
        Collections.addAll(map,introduces);
    }
    public IntroducesMap addViewIntroduce(ViewIntroduce... introduces){
        Collections.addAll(map,introduces);
        return this;
    }
    public void show(){
        if (map.isEmpty()) return;
        for(int i=0;i<map.size()-1;i++){
            final int fi=i;
            map.get(i).setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    map.get(fi).show();
                }
            });
        }

    }

}
