package com.zjj.nb.biz.manager.singleton;

/**
 * Created by jinju.zeng on 2017/4/24.
 */
public class SingletonInstance {
    //使用voltile类型的变量，可以禁止重排序
    private static volatile SingletonInstance instance;

    /**
     * 获取单例(双重检查锁定)
     *
     * @return
     */
    public static SingletonInstance getInstance() {
        if (instance == null) {
            synchronized (SingletonInstance.class) {
                if (instance == null) {
                    instance = new SingletonInstance();
                }
            }
        }
        return instance;
    }
}
