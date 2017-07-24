package com.by122006library.web;

import android.support.annotation.Nullable;

import com.by122006library.Functions.mLog;
import com.by122006library.MyException;
import com.by122006library.Utils.RunLogicUtils;
import com.by122006library.Utils.ThreadUtils;
import com.by122006library.item.ColorStyle;
import com.by122006library.web.AnalysisOut.AnalysisOut;
import com.by122006library.web.AnalysisOut.JSONAnalysisOut;
import com.by122006library.web.CallBack.WEBBaseCallBack;
import com.by122006library.web.WebViewShow.WebViewShow;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by admin on 2016/12/15.
 */

public class Web {
    static {
        mLog.setFileOutLog();
    }

    Web() {
    }

    /**
     * 根据请求进行异步网络通讯
     *
     * @param requster 请求包装类
     * @param callback 回调类 为空不回调
     */
    public static JSONObject doAsnyHttp(final RequestBuilder requster, @Nullable final WEBBaseCallBack callback,
                                        @Nullable
                                        final WebViewShow vs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doSynchroHttp(requster, callback, vs);
                } catch (MyException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }

    /**
     * 根据请求进行同步网络通讯
     *
     * @param requster 请求包装类
     * @param callback 回调类 为空不回调
     * @param vs       显示的控件类，该控件需要使用WebViewShow接口
     * @return 返回的正确数据，如果错误返回null
     * @throws MyException
     */
    @Nullable
    public static JSONObject doSynchroHttp(RequestBuilder requster, @Nullable WEBBaseCallBack callback, @Nullable final
    WebViewShow vs) throws MyException {
        mLog.i("网络请求：" + mLog.getCallerLocation());

        if (ThreadUtils.isUIThread()) throw new MyException("不能在UI线程中调用该方法");
        if (vs != null) ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vs.showLoading("正在获取中...", null);
            }
        });
        String str_url = requster.getUrl();//附加action
        if (RequestBuilder.getDefaultUrl() == null) throw new MyException("你需要为Url设置一个默认值");
        if (RequestBuilder.getDefaultEncode() == null) throw new MyException("你需要为编码Encode设置一个默认值");
        if (RequestBuilder.getDefaultHttpStyle() == -1)
            throw new MyException("你需要为HttpStyle(GET/POST)设置一个默认值");
        if (requster.getAction() == null || requster.getAction().length() == 0)
            mLog.e("action真的应该为null么");
        if (requster.getHttpStyle() == RequestBuilder.GET) str_url += "?" + requster.getData();

        if (requster.getHttpStyle() == RequestBuilder.POST && RequestBuilder.isUrlToken()) {
            str_url += "?" + RequestBuilder.getTokenKeyName() + "=" + RequestBuilder.getToken();
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add(String.format("%-8s= %s", "Action", requster.getAction()));
        list.add(String.format("%-8s= %s", "Style", requster.getHttpStyle() == RequestBuilder.GET ? "Get" : "Post"));
        list.add(String.format("%-8s= %s", "url", requster.getUrl()));
        list.add(String.format("%-8s= \"%s\"", "  +?", RequestBuilder.getTokenKeyName() + "=" + RequestBuilder.getToken()));
        if (requster.getHead() != null) for (String key : requster.getHead().keySet()) {
            list.add(String.format("%-8s= %s", key, requster.getHead().get(key)));
        }
        list.add(String.format("%-8s= %s", "TimeOut", requster.getTimeout()));
        list.add(String.format("%-8s= %s", "Encode", requster.getEncode()));
        list.add(String.format("%-8s= %s", "ReStart", requster.getReStartMaxTimes()));
        list.add("======= Request parameter =======");
        if (requster.getData() != null) for (String key : requster.request.keySet()) {
            try {
                list.add(String.format("%-8s = %s",key,String.valueOf(requster.request.get(key).substring(0, 30))) );
            } catch (Exception e) {
                list.add(String.format("%-8s = %s",key,String.valueOf( requster.request.get(key))));
            }
        }

        mLog.more("连接至网络", list.toArray());

        try {
            URL url = new URL(str_url);
            HttpURLConnection httpConn = (HttpURLConnection) url
                    .openConnection();

            httpConn.setRequestMethod(requster.getHttpStyle() == RequestBuilder.GET ? "GET" : "POST");
            // post方式不能使用缓存
//            httpConn.setUseCaches(requster.getHttpStyle() == RequestBuilder.GET);

            // 设置连接超时时间
            httpConn.setConnectTimeout(requster.getTimeout());
            httpConn.setReadTimeout(requster.getTimeout());

            if (requster.getHttpStyle() == RequestBuilder.POST) {
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                httpConn.setRequestProperty("Charset", requster.getEncode());
                if (RequestBuilder.getDefaultHead() != null)
                    for (String key : (Set<String>) RequestBuilder.getDefaultHead().keySet()) {
                        String value = RequestBuilder.getDefaultHead().get(key);
                        httpConn.setRequestProperty(key, value);
                    }
                if (requster.getHead() != null)
                    for (String key : (Set<String>) requster.getHead().keySet()) {
                        String value = requster.getHead().get(key);
                        httpConn.setRequestProperty(key, value);
                    }
                byte[] requestStringBytes = requster.getJSONData().toString().getBytes(requster.getEncode());
                OutputStream outputStream = httpConn.getOutputStream();
                outputStream.write(requestStringBytes);
                outputStream.close();
            }
            if (httpConn.getResponseCode() == 200) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len = 0;
                InputStream is = httpConn.getInputStream();
                while ((len = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                is.close();
                String out = new String(bos.toByteArray());
                AnalysisOut analysisOut = null;
                if (requster.getDefaultAnalysisOut() != null || requster.getAnalysisOut() != null) {
                    analysisOut = requster.getAnalysisOut() != null ? requster.getAnalysisOut() : requster
                            .getDefaultAnalysisOut();
                } else analysisOut = new JSONAnalysisOut();
                JSONObject jsonObject = null;
                try {
                    jsonObject = analysisOut.analysis(callback, out);
                    if (vs != null) vs.dismiss();
                } catch (final MyException e) {
                    if (vs != null) ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vs.showError(e.getMessage(), ColorStyle.getInitData());
                        }
                    });
                }

                return jsonObject;


            }
            mLog.e("网络出错：状态码=" + httpConn.getResponseCode());

            if (RunLogicUtils.getHereRunTimes((requster.getReStartMaxTimes() + 2) * requster.timeout) <= requster.getReStartMaxTimes()) {
                return doSynchroHttp(requster, callback, vs);
            }
            callback.analyseBack(httpConn.getResponseCode() == 404 ? RESULTSTYLE.Fail_NotFound : RESULTSTYLE
                    .Fail_WebException, null, null);
            if (vs != null) ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    vs.showError("网络出错且多次尝试重连无效", ColorStyle.getInitData());
                }
            });
            return null;

        } catch (final Exception e) {
            mLog.e("网络出错：" + String.valueOf(e));
            if (RunLogicUtils.getHereRunTimes((requster.getReStartMaxTimes() + 2) * requster.timeout) <= requster.getReStartMaxTimes()) {
                return doSynchroHttp(requster, callback, vs);
            }
            if (callback != null) try {
                callback.analyseBack(RESULTSTYLE.Fail_WebException, null, e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (vs != null) ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    vs.showError("网络出错：" + String.valueOf(e), ColorStyle.getInitData());
                }
            });
        }
        return null;
    }


    public enum RESULTSTYLE {
        Fail_WebException, Fail_UserCancel, Fail_ServiceRefuse, Fail_NotFound, Success
    }


}
