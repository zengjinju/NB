package com.zjj.nb.biz.util;

import com.google.common.collect.Maps;
import com.zjj.nb.biz.annotation.IgnoreCheckAnnotation;
import com.zjj.nb.dao.entity.userDAO;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zjj on 17/2/22.
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

    /**
     * 将实例对象封装成map
     * @param param
     * @return
     */
    public static Map<String, Object> param2Map(Object param) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            BeanInfo bean = Introspector.getBeanInfo(param.getClass());
            PropertyDescriptor[] pds = bean.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                String fieldName = pd.getName();
                if (!"class".equals(fieldName)) {
                    Object result = pd.getReadMethod().invoke(param);
                    if(result!=null) {
                        map.put(fieldName, result);
                    }
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

    /**
     * 对象序列化
     * @param t
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T t){
        ObjectOutputStream oos=null;
        ByteArrayOutputStream bytes=new ByteArrayOutputStream(1024);
        try {
            oos=new ObjectOutputStream(bytes);
            oos.writeObject(t);
            return bytes.toByteArray();
        } catch (IOException e) {
            log.error("对象序列化失败,"+e);
            e.printStackTrace();
        }finally {
            try {
                if(oos!=null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 对象反序列
     * @param bytes
     * @return
     */
    public static Object unserialize(final byte[] bytes){
        ObjectInputStream ois=null;
        ByteArrayInputStream in=new ByteArrayInputStream(bytes);
        try {
            ois=new ObjectInputStream(in);
            return ois.readObject();
        } catch (IOException e) {
            log.error("对象反序列化失败"+e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.error("对象反序列化失败"+e);
            e.printStackTrace();
        }finally {
            try {
                if(ois!=null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args){
        userDAO userDAO=new userDAO();
        userDAO.setUserName("zjj");
        userDAO.setUserPassword("123");
        //byte[] bytes=serialize(userDAO);
        String str="zjj";
        System.out.println(str.hashCode());
    }
}
