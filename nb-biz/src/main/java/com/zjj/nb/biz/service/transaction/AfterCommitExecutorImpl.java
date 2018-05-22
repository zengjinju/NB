package com.zjj.nb.biz.service.transaction;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2018/4/2.
 * 数据库事务提交后处理器
 */
@Service
public class AfterCommitExecutorImpl extends TransactionSynchronizationAdapter implements IAfterCommitExecutor {
	private static final ThreadLocal<List<IAfterTransactionHandler>> RUNNABLES=new ThreadLocal<>();
	private static final ExecutorService threadPool= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	@Override
	public void execute(IAfterTransactionHandler handler) {
		List<IAfterTransactionHandler> list=RUNNABLES.get();
		if(CollectionUtils.isEmpty(list)){
			list=new ArrayList<>();
			RUNNABLES.set(list);
			TransactionSynchronizationManager.registerSynchronization(this);
		}
		list.add(handler);
	}

	@Override
	public void afterCommit() {
		System.out.println("success");
		List<IAfterTransactionHandler> list=RUNNABLES.get();
		for(IAfterTransactionHandler handler : list){
			threadPool.execute(handler);
		}
	}

	@Override
	public void afterCompletion(int status) {
		RUNNABLES.remove();
	}
}
