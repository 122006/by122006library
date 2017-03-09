package com.by122006library.web.CallBack;

import android.support.annotation.CallSuper;

import com.by122006library.MyException;
import com.by122006library.web.Web;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2016/12/15.
 */

public abstract class JSONCallBack extends WEBBaseCallBack {
    JSONCallBack(){
        super();

    }
    public void init() {
        super.init();


    }

    @Override
    @CallSuper
    public void analyseBack(Web.RESULTSTYLE resultstyle, String data,Object object) throws MyException {
        super.analyseBack(resultstyle, data, object);

    }


    @Override
    final public void onSuccess(String data) {
        try {
            onSuccess(new JSONObject(data));
        } catch (JSONException e) {
            onError(data, Web.RESULTSTYLE.Fail_ServiceRefuse);
        }
    }
    public abstract void onSuccess(JSONObject json);

}
