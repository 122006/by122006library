package com.by122006library.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by admin on 2017/3/1.
 */

public class ReflectionUtils {

    public static <T> T newInstance(Class<T> clazz, Object... parameters) throws IllegalAccessException,
            InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            classes[i] = parameters[i].getClass();
        }
        Constructor c1 = clazz.getDeclaredConstructor(classes);
        c1.setAccessible(true);
        T t = (T) c1.newInstance(parameters);
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
            field = clazz.getDeclaredField(attname);
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
            field = clazz.getDeclaredField(attname);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Object obj, String attname) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Field field = null;
        field = clazz.getDeclaredField(attname);
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
    public static Method[] getMethodArray(Object obj) throws NoSuchFieldException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Method[] methods=clazz.getMethods();
        for(Method method:methods){
            method.setAccessible(true);
        }
        return methods;
    }
    public static Method getMethod(Object obj, String attname) throws  NoSuchMethodException {
        Class clazz = obj instanceof Class? (Class) obj : obj.getClass();
        Method method = null;
        method = clazz.getDeclaredMethod(attname);
        method.setAccessible(true);
        return method;
    }



}
