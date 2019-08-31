package com.zjj.nb.biz.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Maps;
import com.zjj.nb.biz.annotation.IgnoreCheckAnnotation;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextHelper;
import com.zjj.nb.dao.entity.userDAO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

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
                log.error(e.getMessage());
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
                    Method method = pd.getReadMethod();
                    //跳过安全检查
                    method.setAccessible(true);
                    Object result = method.invoke(param);
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

    public static Map<String,Object> getParams4Asm(Object param){
        Map<String,Object> map = Maps.newHashMap();
        //通过ASM字节码的方式获取类的增强类
        MethodAccess access = MethodAccess.get(param.getClass());
        String[] methodNames = access.getMethodNames();
        for (String methodName : methodNames){
            if (methodName.startsWith("get")){
                int index = access.getIndex(methodName);
                Object result = access.invoke(param,index);
                String fieldName = methodName.substring(3);
                char[] chars = fieldName.toCharArray();
                chars[0] = Character.toLowerCase(chars[0]);
                fieldName = String.valueOf(chars);
                map.put(fieldName,result);
            }
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

    public static Object getMapGenericObj(Field field, JSONObject jsonObject){
        Map map=null;
        ParameterizedType pt=(ParameterizedType) field.getGenericType();
        Type[] types=pt.getActualTypeArguments();
        Class keyClazz=(Class)types[0];//获取Map中key的类型
        Class valueClazz=(Class)types[1]; //获取Map中Value的类型
        if(jsonObject!=null &&jsonObject.size()!=0){
            map=new HashMap();
            for(String key : jsonObject.keySet()){
                Object value=jsonObject.get(key);
                if(basicTypeList.contains(valueClazz)){
                    value= TypeUtils.castToJavaBean(value,valueClazz);
                }
                map.put(key,value);
            }
        }
        return map;
    }

    public static void main(String[] args){
        Long time=1526018341000L;
        Date date=new Date(time);
        System.out.println(DateUtil.parseDateToString(date,"yyyy-MM-dd HH:mm:dd"));
        System.out.println(System.currentTimeMillis()/1000);

    }
}
