package com.by122006library.Utils;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by admin on 2017/3/1.
 */

public class ReflectionUtils {

    public static <T> T newInstance(Class<T> clazz, Object... parameters) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
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


}
