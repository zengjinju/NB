package com.zjj.nb.biz.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.UUID;

/**
 * Created by admin on 2017/12/14.
 */
public class DbHandler implements EventHandler<ObjectEvent>, WorkHandler<ObjectEvent> {
	@Override
	public void onEvent(ObjectEvent objectEvent, long l, boolean b) throws Exception {
		this.onEvent(objectEvent);
	}

	@Override
	public void onEvent(ObjectEvent objectEvent) throws Exception {
		//做业务逻辑
		objectEvent.setId(UUID.randomUUID().toString());
		System.out.println(objectEvent.getId());
	}
}

