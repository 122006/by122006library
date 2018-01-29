package com.by122006library.web.CallBack;

import org.json.JSONObject;

/**
 * Created by admin on 2018/1/22.
 */

public interface JSONData_OnSuccess {
    /**
     * 业务成功
     *
     * @param data 返回的数据
     */
    public abstract void onSuccess(JSONObject data);
}
