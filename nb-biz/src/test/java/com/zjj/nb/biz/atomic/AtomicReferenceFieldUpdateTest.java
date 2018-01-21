package com.zjj.nb.biz.atomic;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Created by admin on 2018/1/9.
 */
public class AtomicReferenceFieldUpdateTest {
	private static final AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdateTest,Integer[]> updater=AtomicReferenceFieldUpdater.newUpdater(AtomicReferenceFieldUpdateTest.class,Integer[].class,"numbers");
	protected volatile Integer[] numbers=new Integer[20];
	private static ExecutorService executorService= Executors.newFixedThreadPool(3);

	public static void main(String[] args){
		final AtomicReferenceFieldUpdateTest test=new AtomicReferenceFieldUpdateTest();
		final CountDownLatch latch=new CountDownLatch(1000000);
		long start=System.nanoTime();

		for(int i=0;i<1000000;i++) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					Integer[] number1;
					Integer[] number2;
					do {
						number1 = updater.get(test);
						number2 = Arrays.copyOf(number1, number1.length);
						if (number2[1] == null) {
							number2[1] = 0;
						} else {
							number2[1]++;
						}
					} while (!updater.compareAndSet(test, number1, number2));
					latch.countDown();
				}
			});


		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(test.numbers[1]+"...."+(System.nanoTime()-start));
	}
}
