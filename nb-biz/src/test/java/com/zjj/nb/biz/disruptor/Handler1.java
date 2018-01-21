package com.zjj.nb.biz.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.UUID;

/**
 * Created by admin on 2017/12/14.
 */
public class Handler1 implements EventHandler<ObjectEvent> {
	@Override
	public void onEvent(ObjectEvent objectEvent, long l, boolean b) throws Exception {
		Thread.sleep(1000);
		objectEvent.setValue("event"+l);
		System.out.println("price"+objectEvent.getPrice());
	}
}
