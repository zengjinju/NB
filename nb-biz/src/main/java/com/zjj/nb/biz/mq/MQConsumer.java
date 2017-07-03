package com.zjj.nb.biz.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jinju.zeng on 2017/7/3.
 */
@Service
public class MQConsumer {

    private static final String SERVER_ADDR = "localhost:9876";
    private static final String INSTANCE_NAME = "NB";
    private static final String GROUP = "nb-group";
    private static final Map<String, MqCallBack> map = new ConcurrentHashMap<>();

    @PostConstruct
    public void beforeinit() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(SERVER_ADDR);
        consumer.setInstanceName(INSTANCE_NAME);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt message : list) {
                    try {
                        System.out.println("received mq message ：" + new String(message.getBody(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.subscribe("test-nb", "TagA");
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void register(MqCallBack callBack) {
        Assert.notNull(callBack, "回调函数不能为空");
        Assert.notNull(callBack.getTags(), "回调中的标签不能为空");
        map.put(callBack.getTags(),callBack);
    }
}
