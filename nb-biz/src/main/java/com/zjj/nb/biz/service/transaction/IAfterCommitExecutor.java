package com.zjj.nb.biz.service.transaction;

/**
 * Created by admin on 2018/4/2.
 */
public interface IAfterCommitExecutor {

	public void execute(IAfterTransactionHandler handler);
}
