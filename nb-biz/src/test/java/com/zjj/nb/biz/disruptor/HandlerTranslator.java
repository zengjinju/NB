package com.zjj.nb.biz.disruptor;

import com.lmax.disruptor.EventTranslator;

import java.util.Random;

/**
 * Created by admin on 2017/12/14.
 */
public class HandlerTranslator implements EventTranslator<ObjectEvent> {
	private Random random=new Random(2000);
	private long value;
	public HandlerTranslator(long value){
		this.value=value;
	}
	@Override
	public void translateTo(ObjectEvent objectEvent, long l) {
		System.out.println("thread"+Thread.currentThread().getName()+","+l);
		objectEvent.setPrice(l);
	}
}
