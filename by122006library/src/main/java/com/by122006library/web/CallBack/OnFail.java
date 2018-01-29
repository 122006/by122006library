package com.by122006library.web.CallBack;

import android.support.annotation.Nullable;

import com.by122006library.web.Web;

/**
 * Created by admin on 2018/1/22.
 */

public interface OnFail {
    /**
     * 服务器上返回失败
     *
     * @param data 返回的数据
     */
    public abstract void onFail(@Nullable String data);
}
