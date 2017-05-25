package com.by122006library.Utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by admin on 2017/5/18. 代码解析器
 */
@Deprecated
public class StringCodeParsing {
    public static String analysis(Object obj, String string) throws ExceptionCodeParsing {
        splitCode(string);
        return "de";
    }

    public static String splitCode(String string) {
        return "de";
    }


    public class ExceptionCodeParsing extends Exception {

    }

    public class Element {
        String oStr;
        String value;
        ArrayList<Element> parent;

        public Element(@NonNull ArrayList<Element> parent) {
            this.parent = parent;
        }

        public void init(String data) {
            oStr = data;
            value = oStr + "";
        }

        /**
         * 化简并得到该String的最终值
         *
         * @param obj
         * @return
         */
        public String simplify(Object obj) {
            value = oStr + "";
            value = simplifying(obj, value);
            return value;
        }

        /**
         * 化简并得到该String的最终值
         *
         * @param obj
         * @return
         */
        public String simplifying(Object obj, String value) {
            Object thisObj = obj;
            while (value.contains("(")) {
                int x = 0;
                int p, q;
                do {
                    p = value.indexOf("(");
                    q = value.indexOf(")");
                } while (false);

                String inStr = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
                value.replace(inStr, simplifying(obj, inStr));
            }

//            ReflectionUtils.getFieldValue(obj);

            return value;
        }

        public String getValue(Object thisObj) {
            return "xxxxx";
        }


    }


}
