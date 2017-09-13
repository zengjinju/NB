package com.zjj.nb.listener;

import com.zjj.nb.biz.util.applicationcontext.ApplicationContextHelper;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * Created by jinju.zeng on 2017/8/30.
 *
 * 自定义的ContextLoaderListerer，目的是在创建ServletContext的时候封装
 * 初始化applicationContextHelper，用来在应用中获取bean
 */
public class WebAppContextLoaderListener extends ContextLoaderListener {

    private ApplicationContextHelper applicationContextHelper=ApplicationContextHelper.getInstance();

    public WebAppContextLoaderListener(){
        super();
    }
//    public WebAppContextLoaderListener(WebApplicationContext event){
//        super(event);
//    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);

        WebApplicationContext webApplicationContext= WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
        /**
         * 这种方式的设置有缺陷，这种方式是只有在IOC容器完成初始化，和所有bean的创建，依赖注入，初始化后才把IOC容器
         * 设置到applicationContextHelper中的applicationContext中。所以如果在bean的初始化方法中调用applicationContextHelper
         * 获取某个bean会出现NullPointException
         */
        applicationContextHelper.setApplicationContext(webApplicationContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
    }
}
