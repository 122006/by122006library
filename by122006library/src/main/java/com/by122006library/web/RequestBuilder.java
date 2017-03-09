package com.by122006library.web;


import android.support.annotation.IntDef;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

/**
 * Created by admin on 2016/12/15.
 */
public class RequestBuilder {
    static final public int GET = 0, POST = 1;
    private static String token = null;
    public HashMap<String, String> request;
    int timeout = 20 * 1000;
    String encode = "UTF-8";
    private String url;
    private int httpStyle = 1;
    private String action;

    public RequestBuilder() {
        request = new HashMap<>();
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        RequestBuilder.token = token;
    }

    public int getHttpStyle() {
        return httpStyle;
    }

    public RequestBuilder setHttpStyle(int httpStyle) {
        this.httpStyle = httpStyle;
        return this;
    }

    public String getUrl() {
        return url + getAction();
    }

    public RequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getAction() {
        return action;
    }

    public RequestBuilder setAction(String action) {
        this.action = action;
        return this;
    }

    public RequestBuilder addAtt(String key, Object value) {
        request.put(key, value.toString());
        return this;
    }

    public RequestBuilder addAtt(String key, float value) {
        request.put(key, value + "");
        return this;
    }

    public RequestBuilder addAtt(String key, int value) {
        request.put(key, value + "");
        return this;
    }

    public RequestBuilder addAtt(String key, boolean value) {
        request.put(key, value + "");
        return this;
    }

    public String getData() {
        String data = "";
        if (token != null) data +=
                "token" + "=" + token;
        for (String key : request.keySet()) {
            String value = request.get(key);
            if (data.length() != 0) {
                data += "&";
            }
            data += key + "=" + value;
        }
        return data;
    }

    public JSONObject getJSONData() {
        JSONObject json = new JSONObject();
        for (String key : request.keySet()) {
            String value = request.get(key);
            try {
                json.put(key, value);
            } catch (JSONException e) {
            }
        }
        return json;
    }

    public RequestBuilder get() {
        httpStyle = RequestBuilder.GET;
        return this;
    }

    public RequestBuilder post() {
        httpStyle = RequestBuilder.POST;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public RequestBuilder setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getEncode() {
        return encode;
    }

    public RequestBuilder setEncode(String encode) {
        this.encode = encode;
        return this;
    }

    @IntDef({GET, POST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HTTP_STYLE {
    }

}
