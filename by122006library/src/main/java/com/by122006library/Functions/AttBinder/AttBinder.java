package com.by122006library.Functions.AttBinder;

import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;

import com.by122006library.Utils.RunLogicUtils;

import java.util.ArrayList;

/**
 * Created by admin on 2017/5/15.
 */

public class AttBinder {
    ArrayList<Att> atts=new ArrayList<>();
    Att nowChangingAtt=null;
    long protectChangedTime=0;

    public void bind(Att att){
        atts.add(att);
        att.addBinder(this);
    }
    /**
     * 这里是数据修改的入口方法
     */
    protected void change(Att from,double per){
        if(nowChangingAtt!=from){
            if(System.currentTimeMillis()<protectChangedTime) return;
        }else{
            protectChangedTime=System.currentTimeMillis()+3;
        }
        for (Att att:(ArrayList<Att>)atts.clone()){
            if (att==from) continue;
            if(per==0||per==1) return;
            if(per>1) per=1;
            if(per<0) per=0;
            double p=att.transform(per);
            att.setPer(p);
            att.attNum=att.min+p*(att.max-att.min);
        }
    }

    /**
     * 发生数据修改(强制更新所有的数据，不推荐，请使用单独属性的监听器)
     * @param per    当前数据百分比（0-1）
     */
    public void fluctuation(double per) {
        for (Att att:(ArrayList<Att>)atts.clone()){
            if(per==0||per==1) return;
            if(per>1) per=1;
            if(per<0) per=0;
            double p=att.transform(per);
            att.setPer(p);
            att.attNum=att.min+p*(att.max-att.min);
        }
    }


    public void destroy(){
        for (Att att:(ArrayList<Att>)atts.clone()){
            att.remove();
        }
    }


}
