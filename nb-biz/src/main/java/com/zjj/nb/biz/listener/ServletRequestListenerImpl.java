package com.zjj.nb.biz.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by admin on 2017/7/30.
 * 用来监听ServletRequest创建和销毁的响应监听器
 */
public class ServletRequestListenerImpl implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ServletContext context=sre.getServletContext();
        HttpServletRequest request=(HttpServletRequest)sre.getServletRequest();
        Long start=(Long) context.getAttribute("startTime");
        String uri=request.getRequestURI();
        System.out.println("当前请求URL："+uri+"的耗时时间："+(System.nanoTime()-start)/1000);
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletContext context=sre.getServletContext();
        context.setAttribute("startTime",System.nanoTime());
    }
}
