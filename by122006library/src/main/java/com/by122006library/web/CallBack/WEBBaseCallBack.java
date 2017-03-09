package com.by122006library.web.CallBack;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.View.CustomDialog;
import com.by122006library.MyException;
import com.by122006library.web.Web;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by admin on 2016/12/15.
 */

public abstract class WEBBaseCallBack {

    /**
     * 默认的匹配方法
     */
    public static ArrayList<MatchCheck> defaultMatchCheckList;
    /**
     * 特殊的匹配方法
     */
    public ArrayList<MatchCheck> currentMatchCheckList;

    public WEBBaseCallBack() {
        if (defaultMatchCheckList == null) init();
    }

    public static void registerDefaultMatchCheck(MatchCheck matchCheck) {
        if (defaultMatchCheckList == null) defaultMatchCheckList = new ArrayList<>();
        defaultMatchCheckList.add(0,matchCheck);
    }

    @CallSuper
    public void init() {
        registerDefaultMatchCheck(new MatchCheck() {
            boolean match(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                return resultstyle == Web.RESULTSTYLE.Success;
            }

            void action(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                onSuccess(data);
            }
        });
        registerDefaultMatchCheck(new MatchCheck() {
            boolean match(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                return resultstyle == Web.RESULTSTYLE.Fail_WebException;
            }

            void action(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) throws MyException {
                (new CustomDialog.Builder(BaseActivity.getContext())).setMessage("网络错误请检查网络设置").setTitle("网络错误").show();
                onError(data,resultstyle);
            }
        });
        registerDefaultMatchCheck(new MatchCheck() {
            boolean match(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                return resultstyle == Web.RESULTSTYLE.Fail_NotFound;
            }

            void action(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) throws MyException {
                (new CustomDialog.Builder(BaseActivity.getContext())).setMessage("链接不正确或服务器异常").setTitle("网络错误").show();
                onError(data,resultstyle);
            }
        });
        registerDefaultMatchCheck(new MatchCheck() {
            boolean match(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) {
                return resultstyle == Web.RESULTSTYLE.Fail_ServiceRefuse;
            }

            void action(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) throws MyException {
                onFail(data);
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
                if (matchCheck.match(resultstyle, data, obj)) {
                    matchCheck.action(resultstyle, data, obj);
                    return;
                }
            }
        } else {
            if (defaultMatchCheckList == null) return;
            for (MatchCheck matchCheck : (ArrayList<MatchCheck>) defaultMatchCheckList.clone()) {
                  if (matchCheck.match(resultstyle, data, obj)) {
                    matchCheck.action(resultstyle, data, obj);
                    return;
                }
            }
        }
    }

    /**
     * 业务成功
     *
     * @param data 返回的数据
     */
    public abstract void onSuccess(String data);

    /**
     * 网络原因造成的失败
     *
     * @param data 返回的数据
     * @param resultstyle   错误类型
     */
    public abstract void onError(String data,Web.RESULTSTYLE resultstyle);

    /**
     * 服务器上返回失败
     *
     * @param data 返回的数据
     */
    public abstract void onFail(@Nullable String data);


    /**
     * 规则匹配类
     */
    public abstract static class MatchCheck {
        abstract boolean match(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj);

        abstract void action(Web.RESULTSTYLE resultstyle, @Nullable String data, @Nullable Object obj) throws MyException;

        public int getCode(@Nullable JSONObject json) {
            if (json == null) return -1;
            return json.optInt("code");
        }
    }



}

