package com.zjj.nb.biz;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by jinju.zeng on 17/2/23.
 */
public class NbServer {
    public static void main(String[] args) {
        Server server = new Server(8081);

        WebAppContext context = new WebAppContext();


        //项目部署根路径
        context.setContextPath("/nb");

        /*
        *  因为配置了 working directory
        *  所以下面配置相对路径即可
        * */
        context.setDescriptor("nb-web/src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("nb-web/src/main/webapp");

        context.setMaxFormContentSize(10485760);
        context.setParentLoaderPriority(true);
        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
