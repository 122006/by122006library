package com.by122006library.web;

import android.support.annotation.Nullable;

import com.by122006library.MyException;
import com.by122006library.Utils.ThreadUtils;
import com.by122006library.Utils.mLog;
import com.by122006library.web.CallBack.WEBBaseCallBack;
import com.by122006library.web.ViewShow.ViewShow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 2016/12/15.
 */

public class Web {

    Web() {
    }

    /**
     * 根据请求进行异步网络通讯
     *
     * @param requster 请求包装类
     * @param callback 回调类 为空不回调
     */
    public static void doAsnyHttp(final RequestBuilder requster, @Nullable final WEBBaseCallBack callback, @Nullable
    final ViewShow vs) {
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
    }

    /**
     * 根据请求进行同步网络通讯
     *
     * @param requster 请求包装类
     * @param callback 回调类 为空不回调
     * @return 返回的正确数据，如果错误返回null
     */
    @Nullable
    public static JSONObject doSynchroHttp(RequestBuilder requster, @Nullable WEBBaseCallBack callback, @Nullable
            ViewShow vs) throws MyException {
        if (ThreadUtils.isUIThread()) throw new MyException("不能在UI线程中调用该方法");
        if (vs != null) vs.showLoading("正在获取中...", null);
        String str_url = requster.getUrl();
        if (requster.getDefaultUrl() == null) throw new MyException("你需要为Url设置一个默认值");
        if (requster.getDefaultEncode() == null) throw new MyException("你需要为编码Encode设置一个默认值");
        if (requster.getDefaultHttpStyle() == -1) throw new MyException("你需要为HttpStyle(GET/POST)设置一个默认值");
        mLog.i("连接至网址：url=" + str_url);
        try {
            URL url = new URL(str_url);
            HttpURLConnection httpConn = (HttpURLConnection) url
                    .openConnection();
            // 设置提交方式
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod(requster.getHttpStyle() == RequestBuilder.GET ? "GET" : "POST");
            // post方式不能使用缓存
            httpConn.setUseCaches(requster.getHttpStyle() == RequestBuilder.GET);
            httpConn.setInstanceFollowRedirects(true);
            // 设置连接超时时间
            httpConn.setConnectTimeout(requster.getTimeout());
            httpConn.setReadTimeout(requster.getTimeout());
            httpConn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Charset", requster.getEncode());
            mLog.i("action=\"" + requster.getAction() + "\"");
            mLog.i("post数据=\"" + requster.getData() + "\"");
            byte[] requestStringBytes = requster.getData().getBytes(requster.getEncode());
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(requestStringBytes);
            outputStream.close();
            mLog.i("StatusCode=" + httpConn.getResponseCode());
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
                int bodystartindex = out.indexOf("<body>");
                if (bodystartindex != -1) {
                    int bodyendindex = out.indexOf("</body>");
                    String data1 = "";
                    if (bodyendindex != -1) {
                        data1 = out.substring(out.indexOf("<body>") + 7,
                                bodyendindex).trim();
                        mLog.i("返回<body>数据：" + data1);
                        if (callback != null) callback.analyseBack(RESULTSTYLE.Success, out, null);
                        if (vs != null) vs.dismiss();
                        return new JSONObject(data1);
                    } else {
                        mLog.e("<body>标签不完整,全部数据如下：" + out);
                        if (callback != null) callback.analyseBack(RESULTSTYLE.Fail_WebException, out, null);
                        if (vs != null) vs.showError("返回数据格式错误", null);
                        return null;
                    }
                } else {
                    mLog.i("返回数据：" + out);
                    if (callback != null) callback.analyseBack(RESULTSTYLE.Success, out, null);
                    if (vs != null) vs.dismiss();
                    try {
                        return new JSONObject(out);
                    } catch (JSONException e) {
                        mLog.e("网络出错：JSON解析出错");
                        return null;
                    }
                }
            }
            mLog.e("网络出错：状态码=" + httpConn.getResponseCode());
            callback.analyseBack(httpConn.getResponseCode() == 404 ? RESULTSTYLE.Fail_NotFound : RESULTSTYLE
                    .Fail_WebException, null, null);
            return null;

        } catch (Exception e) {
            mLog.e("网络出错：" + String.valueOf(e));
            if (callback != null) try {
                callback.analyseBack(RESULTSTYLE.Fail_WebException, null, e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }


    public enum RESULTSTYLE {
        Fail_WebException, Fail_UserCancel, Fail_ServiceRefuse, Fail_NotFound, Success
    }

}
