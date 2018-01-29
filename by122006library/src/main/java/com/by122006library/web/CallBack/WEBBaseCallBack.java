package com.by122006library.web.CallBack;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.Functions.mLog;
import com.by122006library.MyException;
import com.by122006library.Utils.ThreadUtils;
import com.by122006library.View.CustomDialog;
import com.by122006library.web.Web;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by admin on 2016/12/15.
 */

public class WEBBaseCallBack implements OnSuccess, OnFail, OnError {
    /**
     * 默认的过滤方法
     */
    public static ArrayList<MatchCheck> defaultMatchCheckList;

    static {
        mLog.setFileOutLog();
    }

    /**
     * 特殊的过滤方法
     */
    public ArrayList<MatchCheck> currentMatchCheckList;
    OnSuccess onSuccess;
    OnFail onFail;
    OnError onError;


    /**
     * 无参构造方法，请重写onSuccess、onFail、onError方法
     */
    public WEBBaseCallBack() {
        if (defaultMatchCheckList == null) init();
        this.onSuccess=new OnSuccess() {
            @Override
            public void onSuccess(String data) {

            }
        };
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
    public WEBBaseCallBack(OnSuccess onSuccess) {
        if (defaultMatchCheckList == null) init();
        this.onSuccess=onSuccess;
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
    public WEBBaseCallBack(OnSuccess onSuccess, OnError onError) {
        if (defaultMatchCheckList == null) init();
        this.onSuccess=onSuccess;
        this.onError=onError;
        this.onFail=new OnFail() {
            @Override
            public void onFail(@Nullable String data) {

            }
        };
    }
    public WEBBaseCallBack(OnSuccess onSuccess, OnError onError,OnFail onFail) {
        if (defaultMatchCheckList == null) init();
        this.onSuccess=onSuccess;
        this.onError=onError;
        this.onFail=onFail;
    }


    public static void registerDefaultMatchCheck(MatchCheck matchCheck) {
        if (defaultMatchCheckList == null) defaultMatchCheckList = new ArrayList<>();
        defaultMatchCheckList.add(0, matchCheck);
    }

    /**
     * 在这里初始化默认过滤方法
     */
    @CallSuper
    public void init() {
        registerDefaultMatchCheck(new MatchCheck() {
            public boolean match(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                return resultstyle == Web.RESULTSTYLE.Success;
            }

            public void action(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                clazz.onSuccess(data);
            }
        });
        registerDefaultMatchCheck(new MatchCheck() {
            public boolean match(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                return resultstyle == Web.RESULTSTYLE.Fail_WebException;
            }

            public void action(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) throws
                    MyException {

                try {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            (new CustomDialog.Builder(BaseActivity.optContext())).setMessage("网络错误,请检查网络设置").setTitle("网络错误").show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                clazz.onError(data, resultstyle);
            }
        });
        registerDefaultMatchCheck(new MatchCheck() {
            public boolean match(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                return resultstyle == Web.RESULTSTYLE.Fail_NotFound;
            }

            public void action(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) throws
                    MyException {
                (new CustomDialog.Builder(BaseActivity.getContext())).setMessage("链接不正确或服务器异常").setTitle("网络错误").show();
                clazz.onError(data, resultstyle);
            }
        });
        registerDefaultMatchCheck(new MatchCheck() {
            public boolean match(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                return resultstyle == Web.RESULTSTYLE.Fail_ServiceRefuse;
            }

            public void action(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) throws
                    MyException {
                clazz.onFail(data);
            }
        });
    }

    ;

    /**
     * 分析并分发正确返回数据
     *
     * @param data 返回的数据
     */
    @CallSuper
    public void analyseBack(Web.RESULTSTYLE resultstyle, String data, Object obj) throws MyException {
        if (currentMatchCheckList != null) {
            for (MatchCheck matchCheck : (ArrayList<MatchCheck>) currentMatchCheckList.clone()) {
                if (matchCheck.match(this, resultstyle, data, obj)) {
                    matchCheck.action(this, resultstyle, data, obj);
                    return;
                }
            }
        } else {
            if (defaultMatchCheckList == null) return;
            for (MatchCheck matchCheck : (ArrayList<MatchCheck>) defaultMatchCheckList.clone()) {
                if (matchCheck.match(this, resultstyle, data, obj)) {
                    matchCheck.action(this, resultstyle, data, obj);
                    return;
                }
            }
        }
        mLog.e("一个没有被任何拦截器处理的返回！");
    }

    @Override
    public void onSuccess(String data) {
        if (onSuccess!=null)onSuccess.onSuccess(data);
    }

    @Override
    public void onError(String data, Web.RESULTSTYLE resultstyle) {
        if (onError!=null)onError.onError(data,resultstyle);
    }

    @Override
    public void onFail(@Nullable String data) {
        if (onFail!=null)onFail.onFail(data);
    }


    /**
     * 规则匹配类
     */
    public abstract static class MatchCheck {
        public abstract boolean match(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj);

        public abstract void action(WEBBaseCallBack clazz, Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) throws
                MyException;

        public int getCode(@Nullable JSONObject json) {
            if (json == null) return -1;
            return json.optInt("code");
        }
    }


}

