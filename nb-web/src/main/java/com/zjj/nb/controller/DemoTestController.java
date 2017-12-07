package com.zjj.nb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.zjj.configmanager.manager.HostConfig;
import com.zjj.nb.biz.manager.ehcache.ICacheProxy;
import com.zjj.nb.biz.manager.ehcache.TestCacheBean;
import com.zjj.nb.biz.manager.hystrix.UserCommand;
import com.zjj.nb.biz.manager.redis.LockCallBack;
import com.zjj.nb.biz.manager.redis.RedisLock;
import com.zjj.nb.biz.manager.threadPool.ThreadPool;
import com.zjj.nb.biz.mq.MQConsumer;
import com.zjj.nb.biz.mq.MqCallBack;
import com.zjj.nb.biz.service.UserService;
import com.zjj.nb.biz.util.IPUtils;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextHelper;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextUtil;
import com.zjj.nb.dao.entity.userDAO;
import com.zjj.nb.dao.mapper.userDAOMapper;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jinju.zeng on 17/2/22.
 */
@Controller
@RequestMapping("demo")
public class DemoTestController {

    @Autowired
    private RedisLock redisLock;
    @Autowired
    private userDAOMapper userdaoMapper;
    @Autowired
    private MQConsumer mqConsumer;
    @Autowired
    private userDAOMapper userDAOMapper;
    @Autowired
    private ICacheProxy cacheProxy;

    @RequestMapping("test")
    public void test() {
        //DemoTestController userService= (DemoTestController) ApplicationContextHelper.getInstance().getBean(this.getClass());
        Map<String,UserService> map=ApplicationContextHelper.getInstance().getBeanOfType(UserService.class);
        System.out.println(map);
        System.out.println(ApplicationContextUtil.getBean("userServiceImpl"));
    }

    @RequestMapping("lock")
    public void lockTest() {
        Object obj = redisLock.lock("zjj", new LockCallBack() {
            @Override
            public Object runTask() {
                return testTime();
            }
        });
    }

    public Integer testTime() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @RequestMapping("insert")
    public void insert() {
        userDAO userdao = new userDAO();
        userdao.setUserName("zjj");
        userdao.setUserPassword("123456");
        userdao.setGmtCreate(DateTime.now().toDate());
        userdaoMapper.insertSelective(userdao);
    }

    @RequestMapping("get")
    @ResponseBody
    public Object get() {
        userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
        System.out.println(userdao);
        return userdao;
    }

    @RequestMapping(value = "post", method = RequestMethod.POST)
    @ResponseBody
    public Object post() {
        userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
        return userdao;
    }

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("ip")
    public void testIp(HttpServletRequest request) {
        String ip = IPUtils.getRemoteAddress(request);
        System.out.println(ip);
    }

    @RequestMapping("mq")
    public void testMq() throws MQClientException {
        mqConsumer.register(new MqCallBack() {
            @Override
            public String getTags() {
                return "TagA";
            }

            @Override
            public void doCallBack() {
                userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
                System.out.println(JSON.toJSONString(userdao));
            }
        });
    }

    @RequestMapping("mq1")
    public void testMq1() throws MQClientException {
        mqConsumer.register(new MqCallBack() {
            @Override
            public String getTags() {
                return "TagB";
            }

            @Override
            public void doCallBack() {
                userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
                System.out.println(JSON.toJSONString(userdao));
            }
        });
    }

    @RequestMapping("ehcache")
    public void ehcache(){
        System.out.println(cacheProxy.getValue(TestCacheBean.CACHE_KET));
    }

    @RequestMapping("hystrix")
    public void hystrixTest(){
        userDAO userdao=new UserCommand("zjj","123456").execute();
    }

    @RequestMapping("config")
    @ResponseBody
    public String getConfig(){
        while(true){
            String value=HostConfig.get("redis.port","");
            if(value!=null&&!"".equals(value)){
                return value;
            }
            System.out.println(System.currentTimeMillis());
        }
    }

}
