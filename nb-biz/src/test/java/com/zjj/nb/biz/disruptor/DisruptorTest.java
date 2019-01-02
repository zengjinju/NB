package com.zjj.nb.biz.disruptor;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by admin on 2017/12/14.
 */
public class DisruptorTest {
	public static void main(String[] args){
		long begtime=System.currentTimeMillis();
		ExecutorService executor= Executors.newFixedThreadPool(4);
		AtomicInteger threadId = new AtomicInteger(0);
		Disruptor<ObjectEvent> disruptor=new Disruptor<ObjectEvent>(ObjectEvent.FACTORY, 256, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("test consumer thread"+threadId.getAndIncrement());
				return t;
			}
		}, ProducerType.SINGLE, new YieldingWaitStrategy());
		//使用disruptor创建消费者组c1,c2(c1,c2并行执行)
//		EventHandlerGroup<ObjectEvent> group=disruptor.handleEventsWith(new Handler1(),new DbHandler());
		Handler1 handler1 = new Handler1();
		DbHandler dbHandler = new DbHandler();
		NotifyHandler notifyHandler = new NotifyHandler();
		//先执行handler1,dbHandler,notifyHandler顺序执行
		disruptor.handleEventsWith(handler1);
		disruptor.after(handler1).handleEventsWith(dbHandler);
		disruptor.after(dbHandler).handleEventsWith(notifyHandler);
		//启动disruptor
		disruptor.start();
		CountDownLatch latch=new CountDownLatch(1);
		for(int i=0;i<1;i++) {
			executor.execute(new HandlerRunnable(disruptor, latch));
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		disruptor.shutdown();
		System.out.println("耗时"+(System.currentTimeMillis()-begtime));
	}

	 static class HandlerRunnable implements Runnable{

		private CountDownLatch latch;
		private Disruptor<ObjectEvent> disruptor;
		public HandlerRunnable(Disruptor<ObjectEvent> disruptor,CountDownLatch latch){
			this.disruptor=disruptor;
			this.latch=latch;
		}

		@Override
		public void run() {
			for(int i=0;i<32;i++){
				HandlerTranslator translator=new HandlerTranslator((long)i);
				disruptor.publishEvent(translator);
			}
			latch.countDown();
		}
	}
}
