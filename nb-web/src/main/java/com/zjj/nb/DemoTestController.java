package com.zjj.nb;

import com.zjj.nb.biz.manager.LockCallBack;
import com.zjj.nb.biz.manager.RedisLock;
import com.zjj.nb.dao.entity.userDAO;
import com.zjj.nb.dao.mapper.userDAOMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("test")
    public void test() {
        System.out.println("abc");
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
    public void insert(){
        userDAO userdao=new userDAO();
        userdao.setUserName("zjj");
        userdao.setUserPassword("123456");
        userdao.setGmtCreate(DateTime.now().toDate());
        userdaoMapper.insertSelective(userdao);
    }

}
