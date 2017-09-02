package com.zjj.nb.biz.util.applicationcontext;

import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Created by jinju.zeng on 2017/8/30.
 * 单例的
 */
public class ApplicationContextHelper {
    private  ApplicationContext applicationContext;
    private static ApplicationContextHelper applicationContextHelper=new ApplicationContextHelper();

    private ApplicationContextHelper(){
    }

    public static ApplicationContextHelper getInstance(){
        return applicationContextHelper;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object getBean(String name){
        return applicationContext.getBean(name);
    }

    public Object getBean(Class clazz){
        return applicationContext.getBean(clazz);
    }

    public <T> Map<String,T> getBeanOfType(Class<T> clazz){
        return applicationContext.getBeansOfType(clazz);
    }
}
