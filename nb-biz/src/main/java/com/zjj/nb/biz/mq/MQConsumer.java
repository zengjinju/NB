package com.zjj.nb.biz.mq;

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
    private static DefaultMQPushConsumer CONSUMER=null;

    private void init(final String tags) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(SERVER_ADDR);
        consumer.setInstanceName(INSTANCE_NAME);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt message : list) {
                    try {
                        System.out.println("received mq message ：" + new String(message.getBody(), "utf-8"));
                        MqCallBack callBack= map.get(tags);
                        //具体的回调业务处理(在客户端实现回调方法的具体逻辑)
                        if(callBack!=null){
                            callBack.doCallBack();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.subscribe("test-nb", tags);
            CONSUMER=consumer;
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    private void start(String tags) throws MQClientException {
        init(tags);
        CONSUMER.start();
    }

    /**
     * 客户端通过调用这个方法来注册相对应的消息监听回调处理逻辑
     * @param callBack
     * @throws MQClientException
     */
    public void register(MqCallBack callBack) throws MQClientException {
        Assert.notNull(callBack, "回调函数不能为空");
        Assert.notNull(callBack.getTags(), "回调中的标签不能为空");
        if (!map.containsKey(callBack.getTags())) {
            synchronized (map) {
                map.put(callBack.getTags(), callBack);
                start(callBack.getTags());
            }
        }
    }
}
