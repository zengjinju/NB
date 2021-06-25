package com.zjj.nb.biz;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by jinju.zeng on 17/2/23.
 */
public class NbServer {
    public static void main(String[] args) {
        Server server = new Server(8089);

        WebAppContext context = new WebAppContext();


        //项目部署根路径
        context.setContextPath("/nb");

        /*
        *  因为配置了 working directory
        *  所以下面配置相对路径即可
        * */
        context.setDescriptor("./nb-web/src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("./nb-web/src/main/webapp");
        //解决静态资源修改后不能重新加载的问题
        context.setDefaultsDescriptor("./nb-web/src/TestExample/resources/webdefault.xml");

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
