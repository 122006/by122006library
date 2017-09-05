package com.by122006library.Utils;

import android.support.annotation.Nullable;

import com.by122006library.Functions.mLog;

import java.lang.reflect.Field;

/**
 * Created by admin on 2017/6/6.
 */
public class DesignUtils {
    /**
     * 检查子类是否拥有某一属性
     * @param obj 当前对象名
     * @param attributeName 属性名
     *
     * @Deprecated 建议使用@Subclass和@Attribute注释完成此功能
     */
    @Nullable
    @Deprecated
    public static boolean checkSubclassAttribute(Object obj,String attributeName){
        try {
            Field field= ReflectionUtils.getField(obj,attributeName);
        } catch (NoSuchFieldException e) {
            mLog.e(mLog.getCallerLocation(),"你需要在子类中定义 \""+attributeName+"\" 属性");
            return false;
        }
        return true;
    }
    /**
     * 获得子类某一属性
     * @param obj 当前对象名
     * @param attributeName 属性名
     *
     *                      @Deprecated 建议使用@Subclass和@Attribute注释完成此功能
     */
    @Nullable
    public static Object getSubclassAttribute(Object obj,String attributeName){
        try {
            Field field= ReflectionUtils.getField(obj,attributeName);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            mLog.e(mLog.getCallerLocation(),"你需要在子类中定义 \""+attributeName+"\" 属性");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得子类某一属性
     * @param obj 当前对象名
     * @param attributeName 属性名
     *
     *                      @Deprecated 建议使用@Subclass和@Attribute注释完成此功能
     */
    @Nullable
    public static boolean getSubclassBoolean(Object obj,String attributeName,boolean defaultValue){
        try {
            Field field= ReflectionUtils.getField(obj,attributeName);
            try {
                return (boolean) field.get(obj);
            } catch (ClassCastException e) {
                mLog.e(mLog.getCallerLocation(),"子类中定义的 \""+attributeName+"\" 属性应该是boolean属性");
            }
        } catch (NoSuchFieldException e) {
            mLog.e(mLog.getCallerLocation(),"你需要在子类中定义 \""+attributeName+"\" 属性");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 设置子类某一属性
     * @param obj 当前对象名
     * @param attributeName 属性名
     * @param attributeValue    属性值
     *
     * @Deprecated 建议使用@Subclass和@Attribute注释完成此功能
     */
    @Nullable
    public static void setSubclassAttribute(Object obj,String attributeName,Object attributeValue){
        try {
            Field field= ReflectionUtils.getField(obj,attributeName);
            field.set(obj,attributeValue);
        } catch (NoSuchFieldException e) {
            mLog.e(mLog.getCallerLocation(),"你需要在子类中定义 \""+attributeName+"\" 属性");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
