package com.zjj.nb.biz.mq;

/**
 * Created by admin on 2017/7/3.
 * mqconsumer监听到消息时的回调处理逻辑
 */
public interface MqCallBack {

    String getTags();

    void doCallBack();
}
