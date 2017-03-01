package com.zjj;

import com.zjj.manager.LockCallBack;
import com.zjj.manager.RedisLock;
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

}
