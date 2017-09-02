package com.zjj.nb.biz.manager.hystrix;

import com.netflix.hystrix.*;
import com.zjj.nb.biz.service.UserService;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextHelper;
import com.zjj.nb.dao.entity.userDAO;

import static com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;

/**
 * Created by admin on 2017/8/30.
 * 熔断器模式
 */
public class UserCommand extends HystrixCommand<userDAO> {

    private String name;
    private UserService userService;

    public UserCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("UserCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("UserServicePool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(THREAD)
                        .withCircuitBreakerEnabled(true)
                ));
        this.name=name;
        this.userService = (UserService) ApplicationContextHelper.getInstance().getBean(UserService.class);
    }

    @Override
    protected userDAO run() throws Exception {
        return null;
    }
}
