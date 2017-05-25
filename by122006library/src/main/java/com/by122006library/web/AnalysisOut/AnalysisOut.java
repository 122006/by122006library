package com.by122006library.web.AnalysisOut;

import com.by122006library.MyException;
import com.by122006library.web.CallBack.WEBBaseCallBack;

import org.json.JSONObject;

/**
 * Created by admin on 2017/5/25.
 */

public abstract class AnalysisOut {
    public abstract JSONObject analysis(WEBBaseCallBack callback, String out) throws MyException;
}
