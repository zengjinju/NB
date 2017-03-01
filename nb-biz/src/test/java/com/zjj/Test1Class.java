package com.zjj;

import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.sql.ResultSet;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class Test1Class {

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Test1Class() {

    }

    public Test1Class(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void main(String[] args) throws Exception {
        //TestDO testDO = TestDO.getInstance();
        BasicInterface testDO = new TestDO();
//        testDO.setId(1);
//        testDO.setName("zjj");
        //constructorTest(testDO);
        //fieldTest(testDO);
        //methodTest(testDO);
    }

    public static void constructorTest(Object obj) throws NoSuchMethodException {
        Class clazz = obj.getClass();
        Constructor[] c1 = clazz.getDeclaredConstructors();
        for (Constructor constructor : c1) {
            System.out.println(constructor);
        }
        Constructor c2 = clazz.getDeclaredConstructor();
        System.out.println(c2);

    }

    public static void fieldTest(Object obj) throws IllegalAccessException {
        Class clazz = obj.getClass();
        //Field[] fields = clazz.getDeclaredFields();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            field.setAccessible(true);
            Object value = field.get(obj);
            System.out.println(fieldName + ":" + value);
        }
    }

    public static void methodTest(Object obj) throws Exception {
        Class clazz = obj.getClass();
//        Method method=clazz.getMethod("setId",Integer.class);
//        method.invoke(obj,2);
        Method method = clazz.getMethod("testCode");
        Class declaringClass = method.getDeclaringClass();
        if (declaringClass.isInterface()) {
            System.out.println("");
        }
    }
}
