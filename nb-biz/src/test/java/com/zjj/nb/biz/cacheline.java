package com.zjj.nb.biz;

/**
 * Created by admin on 2017/12/16.
 */
public class cacheline {
	private static final long ITERATIONS = 500L * 1000L * 1000L;
	private static final int NUMS=3;
	private static VolationLong[] longs = new VolationLong[NUMS];

	static {
		for (int i = 0; i < NUMS; i++) {
			longs[i] = new VolationLong();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		long start = System.nanoTime();
		runTest();
		System.out.println(System.nanoTime()-start);
	}

	private static void runTest() throws InterruptedException {
		Thread[] threads = new Thread[NUMS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new TestRunnable(i));
		}
		for (Thread t : threads) {
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
	}


	private static class TestRunnable implements Runnable {

		private int index;

		public TestRunnable(int index) {
			this.index = index;
		}

		@Override
		public void run() {
			long i = ITERATIONS + 1;
			while (--i != 0) {
				longs[index].value=i;
			}
		}
	}

	private static class VolationLong {
		public volatile long value=0L;
		private long p1,p2,p3,p4,p5,p6,p7;
	}
}
