package com.zjj.nb.biz.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * Created by admin on 2018/1/24.
 */
@Service("serviceInterceptor")
public class ServiceInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return invocation.proceed();
	}
}
