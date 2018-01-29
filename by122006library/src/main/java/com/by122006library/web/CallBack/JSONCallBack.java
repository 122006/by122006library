package com.by122006library.web.CallBack;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.by122006library.Functions.mLog;
import com.by122006library.MyException;
import com.by122006library.web.Web;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2016/12/15.
 */

public class JSONCallBack extends WEBBaseCallBack implements JSONData_OnSuccess{
    static {
        mLog.setFileOutLog();
    }

    /**
     * 无参构造方法，请重写onSuccess（JSONObject）、onFail、onError方法
     */
    public JSONCallBack(){
        if (defaultMatchCheckList == null) init();
    }
    public JSONCallBack(JSONData_OnSuccess onSuccess,OnFail onFail,OnError onError){
        if (defaultMatchCheckList == null) init();
        this.jsonData_onSuccess=onSuccess;
        this.onError=new OnError() {
            @Override
            public void onError(String data, Web.RESULTSTYLE resultstyle) {

            }
        };
        this.onFail=new OnFail() {
            @Override
            public void onFail(@Nullable String data) {

            }
        };

    }
    public void init() {
        super.init();


    }
    JSONData_OnSuccess jsonData_onSuccess;

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

    @Override
    public void onSuccess(JSONObject data) {
        if (jsonData_onSuccess!=null)jsonData_onSuccess.onSuccess(data);
    }
}
