package com.zjj.nb.biz.dubbohystrix;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcResult;
import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;

/**
 * Created by admin on 2017/9/13.
 */
@Slf4j
public class DubboHystrixCommand extends HystrixCommand<Result> {
    private static final int DEFAULT_CORE_POOL_SIZE=20;

    private Invoker invoker;
    private Invocation invocation;

    public DubboHystrixCommand(Invoker invoker, Invocation invocation){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(String.format("%s_%d",invocation.getMethodName(),invocation.getArguments()==null?0:invocation.getArguments().length)))
                //.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("UserServicePool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(THREAD)
                        .withCircuitBreakerEnabled(true)
                        .withFallbackEnabled(true)//开启优雅降级
                        .withCircuitBreakerRequestVolumeThreshold(20)//10秒钟内至少19次请求失败，才开启熔断器模式
                        .withCircuitBreakerSleepWindowInMilliseconds(30)//熔断器中断请求30秒后会进入半打开状态,放部分流量过去重试
                        .withCircuitBreakerErrorThresholdPercentage(50)//错误率达到50开启熔断保护
                        .withExecutionTimeoutEnabled(false))//禁用超时机制
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(getCorePoolSize(invoker.getUrl()))));
        this.invoker=invoker;
        this.invocation=invocation;
    }
    @Override
    protected Result run() throws Exception {
        return invoker.invoke(invocation);
    }

    private static int getCorePoolSize(URL url){
        if(url!=null){
            int corePoolSize=url.getParameter("ThreadPoolCoreSize",DEFAULT_CORE_POOL_SIZE);
            return corePoolSize;
        }
        return DEFAULT_CORE_POOL_SIZE;
    }

    /**
     * 当服务调用失败的时候，执行这个方法
     * @return
     */
    @Override
    protected Result getFallback() {
        RpcResult result=new RpcResult();
        return result;
    }
}
