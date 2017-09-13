package com.zjj.nb.biz.dubbohystrix;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

/**
 * Created by jinju.zeng on 2017/9/13.
 * 对Dubbo的远程服务调用封装熔断器模式
 */
@Activate(group = Constants.CONSUMER)
public class HystrixFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        DubboHystrixCommand command=new DubboHystrixCommand(invoker,invocation);
        return command.execute();
    }
}
