package com.zjj.nb.biz.util.applicationcontext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by jinju.zeng on 2017/3/14.
 */
@Service
public class ApplicationContextUtil implements ApplicationContextAware{

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext=applicationContext;
    }

    public static Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }

    public static <T> Map<String,T> getBeanByType(Class clazz){
        return applicationContext.getBeansOfType(clazz);
    }
}
