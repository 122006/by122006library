package com.by122006library.Utils;

import android.util.Log;

import com.by122006library.Functions.mLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by admin on 2017/3/1.
 */

public class ReflectionUtils {

    public static <T> T newInstance(Class<T> clazz, Object... parameters) throws IllegalAccessException,
            InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            mLog.i(parameters[i].getClass().getName());
            classes[i] = parameters[i].getClass();
        }
        Constructor c1 = clazz.getDeclaredConstructor(classes);
        c1.setAccessible(true);
        T t =(T) clazz.cast(c1.newInstance(parameters)) ;
        return t;
    }

//    public static class Parameter {
//        Class clazz;
//        Object obj;
//
//        Parameter(Class clazz, Object obj) {
//            this.clazz = clazz;
//            this.obj = obj;
//        }
//
//        static Class[] getParametersClasses(Parameter[] parameters) {
//            Class[] classes = new Class[parameters.length];
//            for (int i = 0; i < parameters.length; i++) {
//                classes[i] = parameters[i].clazz;
//            }
//            return classes;
//        }
//
//        static Object[] getParametersObjects(Parameter[] parameters) {
//            Object[] objects = new Object[parameters.length];
//            for (int i = 0; i < parameters.length; i++) {
//                objects[i] = parameters[i].obj;
//            }
//            return objects;
//        }
//    }

    public static void setFieldValue(Object obj, String attname, Object value) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Field field = null;
        try {
            field = clazz.getField(attname);
            field.setAccessible(true);
            field.set(obj, value);//设置对象中，私有变量的值
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> T getFieldValue(Object obj, String attname, Class<T> valueClazz) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Field field = null;
        try {
            field = clazz.getField(attname);
            field.setAccessible(true);
            if(Modifier.isStatic(field.getModifiers())) obj=clazz;
            return (T) valueClazz.cast(field.get(obj));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Object obj, String attname) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Field field = null;
        field = clazz.getField(attname);
        field.setAccessible(true);
        return field;
    }


    public static Field[] getFieldArray(Object obj) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Field[] fields=clazz.getFields();
        for(Field field:fields){
            field.setAccessible(true);
        }
        return fields;
    }
    public static Field[] getDeclaredFields(Object obj) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Field[] fields=clazz.getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
        }
        return fields;
    }
    public static Field[] getAllFields(Object obj) throws NoSuchFieldException {
        Field[] fields1=getDeclaredFields(obj);
        Field[] fields2=getFieldArray(obj);
        Field[] all=new Field[fields1.length+fields2.length];
        for(int i=0;i<all.length;i++){
            all[i]=i<fields1.length?fields1[i]:fields2[i-fields1.length];
        }
        return all;
    }
    public static Method[] getMethodArray(Object obj) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Method[] methods=clazz.getMethods();
        for(Method method:methods){
            method.setAccessible(true);
        }
        return methods;
    }
    public static Method[] getDeclaredMethods(Object obj) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Method[] methods=clazz.getDeclaredMethods();
        for(Method method:methods){
            method.setAccessible(true);
        }
        return methods;
    }
    public static Method[] getAllMethods(Object obj) throws NoSuchFieldException {
        Method[] methods1=getDeclaredMethods(obj);
        Method[] methods2=getMethodArray(obj);
        Method[] all=new Method[methods1.length+methods2.length];
        for(int i=0;i<all.length;i++){
            all[i]=i<methods1.length?methods1[i]:methods2[i-methods1.length];
        }
        return all;
    }
    public static Method getMethod(Object obj, String methodName,Class<?>... parameterTypes) throws  NoSuchMethodException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Method method = null;
        method = clazz.getMethod(methodName,parameterTypes);
        method.setAccessible(true);
        mLog.isNull(method);
        return method;
    }
    public static Method getDeclaredMethod(Object obj, String methodName,Class<?>... parameterTypes) throws  NoSuchMethodException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Method method = null;
        method = clazz.getDeclaredMethod(methodName,parameterTypes);
        method.setAccessible(true);
        mLog.isNull(method);
        return method;
    }

    public static String getSmallClassName(String string){
        int index=string.lastIndexOf(".");
        if (index==-1){
            return string;
        }else{
            Log.println(4,"getSmallClassName", string.substring(index+1));
            return string.substring(index+1);
        }
    };

}
