package com.jason.experiment.somecoinapp.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ReflectionUtils
 * Created by jason.
 */
public class ReflectionUtils {

    public static Object getPrivateFieldValue(String fieldName, Object target, Class<?> targetClass)
            throws NoSuchFieldException, SecurityException, NullPointerException, IllegalAccessException, ExceptionInInitializerError {
        Field targetField = targetClass.getDeclaredField(fieldName);
        targetField.setAccessible(true);
        return targetField.get(target);
    }

    public static Object getPrivateMethodResult(String methodName, Object target, Class<?> targetClass, Class<?>[] paramTypes, Object[] paramValues)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = targetClass.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(target, paramValues);
    }

    public static Object getPrivateObject(String fullClassName)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
        Class<?>       klass       = Class.forName(fullClassName);
        Constructor<?> konstructor = klass.getDeclaredConstructor();
        konstructor.setAccessible(true);
        return konstructor.newInstance();
    }

    public static Object getPrivateObject(String fullClassName, Class<?>[] constructorParamTypes, Object[] constructorParams)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
        Class<?>       klass       = Class.forName(fullClassName);
        Constructor<?> konstructor = klass.getDeclaredConstructor(constructorParamTypes);
        konstructor.setAccessible(true);
        return konstructor.newInstance(constructorParams);
    }
}
