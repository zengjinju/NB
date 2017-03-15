package com.zjj.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Created by jinju.zeng on 2017/3/14.
 */
@Service
public class ApplicationContextUtil implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }
}
