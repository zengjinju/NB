package com.zjj.util;

import com.google.common.collect.Maps;
import com.zjj.annotation.IgnoreCheckAnnotation;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jinju.zeng on 17/2/22.
 */
@Slf4j
public class BeanUtil {

    private static List<Class<?>> basicTypeList;

    static {
        basicTypeList = new ArrayList<>();
        basicTypeList.add(Short.class);
        basicTypeList.add(Byte.class);
        basicTypeList.add(Boolean.class);
        basicTypeList.add(Integer.class);
        basicTypeList.add(Long.class);
        basicTypeList.add(Double.class);
        basicTypeList.add(Float.class);
        basicTypeList.add(char.class);
        basicTypeList.add(List.class);
        basicTypeList.add(Set.class);
        basicTypeList.add(Map.class);
        basicTypeList.add(String.class);
    }

    public static void checkParamisNull(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length <= 0) {
            throw new RuntimeException(clazz.getName() + "对象里面不包含字段");
        }
        for (Field field : fields) {
            IgnoreCheckAnnotation ignoreCheck = field.getAnnotation(IgnoreCheckAnnotation.class);
            if (ignoreCheck != null && ignoreCheck.isIgnore()) {
                continue;
            }
            try {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                //获取get方法
                Method method = pd.getReadMethod();
                Class returnType = method.getReturnType();
                Object result = method.invoke(obj);
                if (result == null) {
                    throw new RuntimeException(String.format("%s字段为空", field.getName()));
                }
                if (!basicTypeList.contains(returnType)) {
                    checkParamisNull(result);
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static Map<String, Object> param2Map(Object param) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            BeanInfo bean = Introspector.getBeanInfo(param.getClass());
            PropertyDescriptor[] pds = bean.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                String fieldName = pd.getName();
                if (!"class".equals(fieldName)) {
                    Object result = pd.getReadMethod().invoke(param);
                    map.put(fieldName, result);
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return map;
    }
}
