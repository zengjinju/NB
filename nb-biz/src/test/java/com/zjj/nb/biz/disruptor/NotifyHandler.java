package com.zjj.nb.biz.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * Created by admin on 2017/12/14.
 */
public class NotifyHandler implements EventHandler<ObjectEvent> {
	@Override
	public void onEvent(ObjectEvent objectEvent, long l, boolean b) throws Exception {
		System.out.println("notify:"+objectEvent.toString());
	}
}
