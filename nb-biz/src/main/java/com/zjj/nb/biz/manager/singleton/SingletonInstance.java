package com.zjj.nb.biz.manager.singleton;

/**
 * Created by jinju.zeng on 2017/4/24.
 */
public class SingletonInstance {
    private static volatile SingletonInstance instance;

    /**
     * 获取单例
     * @return
     */
    public static SingletonInstance getInstance(){
        if(instance==null){
            synchronized (SingletonInstance.class){
                if(instance==null){
                    return new SingletonInstance();
                }
            }
        }
        return instance;
    }
}
