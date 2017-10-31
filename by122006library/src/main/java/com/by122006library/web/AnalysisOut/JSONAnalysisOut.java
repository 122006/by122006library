package com.by122006library.web.AnalysisOut;

import com.by122006library.Functions.mLog;
import com.by122006library.MyException;
import com.by122006library.web.CallBack.WEBBaseCallBack;
import com.by122006library.web.Web;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2017/5/25.
 */

public class JSONAnalysisOut extends AnalysisOut {
    static {
        mLog.setFileOutLog();
    }

    @Override
    public JSONObject analysis(WEBBaseCallBack callback, String out) throws MyException {
        int bodystartindex = out.indexOf("<body>");
        if (bodystartindex != -1) {
            int bodyendindex = out.indexOf("</body>");
            String data1 = "";
            if (bodyendindex != -1) {
                data1 = out.substring(out.indexOf("<body>") + 7,
                        bodyendindex).trim();
                mLog.i("返回<body>数据：" + data1);
                if (callback != null) callback.analyseBack(Web.RESULTSTYLE.Success, out, null);

                try {
                    return new JSONObject(data1);
                } catch (JSONException e) {
                    throw new MyException("返回数据格式错误");
                }
            } else {
                if (callback != null) callback.analyseBack(Web.RESULTSTYLE.Fail_WebException, out, null);
                mLog.e("<body>标签不完整,全部数据如下：" + out);
                throw new MyException("网络文件不完整");
            }
        } else {
            mLog.i("返回数据：" + out);
            if (callback != null) callback.analyseBack(Web.RESULTSTYLE.Success, out, null);

            try {
                return new JSONObject(out);
            } catch (JSONException e) {
                throw new MyException("返回数据格式错误");
            }
        }

    }
}

