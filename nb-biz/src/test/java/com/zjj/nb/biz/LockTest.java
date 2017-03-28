package com.zjj.nb.biz;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by admin on 17/2/26.
 */
public class LockTest implements Lock {

    private Sync sync = new Sync();

    private final static class Sync extends AbstractQueuedSynchronizer {
        private Integer count;

        public Sync() {
        }

//        public int tryAcquireShared(int arg) {
//            for (; ; ) {
//                int state = getState();
//                int newState = state - 1;
//                if (newState < 0 || compareAndSetState(state, newState)) {
//                    return newState;
//                }
//            }
//        }
//
//        public boolean tryReleaseShared(int arg) {
//            for (; ; ) {
//                int state = getState();
//                int newState = state + 1;
//                if (newState > 2) {
//                    return false;
//                }
//                if (compareAndSetState(state, newState)) {
//                    return true;
//                }
//            }
//        }

        public boolean tryAcquire(int arg) {
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, arg)) {
                    return true;
                }
            }
            return false;
        }

        public boolean tryRelease(int arg) {
            int c = getState();
            int next = c - 1;
            setState(next);
            return true;
        }

    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
