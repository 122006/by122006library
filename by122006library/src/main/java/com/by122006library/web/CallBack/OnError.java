package com.by122006library.web.CallBack;

import com.by122006library.web.Web;

/**
 * Created by admin on 2018/1/22.
 */

public interface OnError {
    /**
     * 网络原因造成的失败
     *
     * @param data        返回的数据
     * @param resultstyle 错误类型
     */
    public abstract void onError(String data, Web.RESULTSTYLE resultstyle);
}
