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

    private static final int DEFAULT_CORE_POOL_SIZE=20;

    /**
     * 入参,用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String pwd;

    private UserService userService;

    public UserCommand(String name,String pwd) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("UserCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("UserServicePool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(THREAD)
                        .withCircuitBreakerEnabled(true)
                        .withCircuitBreakerRequestVolumeThreshold(20)//10秒钟内至少19次请求失败，才开启熔断器模式
                        .withCircuitBreakerSleepWindowInMilliseconds(30)//熔断器中断请求30秒后会进入半打开状态,放部分流量过去重试
                        .withCircuitBreakerErrorThresholdPercentage(50)//错误率达到50开启熔断保护
                        .withExecutionTimeoutEnabled(false))//禁用超时机制
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                                                 .withCoreSize(DEFAULT_CORE_POOL_SIZE)));
        this.userName=name;
        this.pwd=pwd;
        this.userService = (UserService) ApplicationContextHelper.getInstance().getBean(UserService.class);
    }

    @Override
    protected userDAO run() throws Exception {
        return userService.selectByUserNameAndPwd(userName,pwd);
    }
}
