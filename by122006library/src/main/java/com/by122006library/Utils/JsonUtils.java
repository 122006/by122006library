package com.by122006library.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2017/3/23.
 */

public class JSONUtils {
    public static JSONObject createJSONObject(S... data){
        JSONObject jsonObject=new JSONObject();
        for(S s:data){
            try {
                jsonObject.put(s.key.toString(),s.value.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;

    }
    public static class S{
        public Object key,value;
        public S(Object key,Object value){
            this.key=key;
            this.value=value;
        }
    }


}
