package com.zjj.nb.biz.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.impl.consumer.PullResultExt;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jinju.zeng on 2017/7/3.
 */
@Service
public class MQConsumer implements InitializingBean {

    private static final String SERVER_ADDR = "localhost:9876";
    private static final String INSTANCE_NAME = "NB";
    private static final String GROUP = "nb-group";
    private static final Map<String, MqCallBack> map = new ConcurrentHashMap<>();
    private static DefaultMQPushConsumer CONSUMER=null;

    private void start(String tags) throws MQClientException {
//        init(tags,consumer);
//        consumer.start();
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

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(SERVER_ADDR);
        consumer.setInstanceName(INSTANCE_NAME);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt message : list) {
                    try {
                        System.out.println("received mq message ：" + new String(message.getBody(), "utf-8"));
                        MqCallBack callBack= map.get(message.getTags());
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
            //接受topic为test-nb下的所有tag的消息
            consumer.subscribe("test-zjj", "TagB");
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * pull 模式拉取
     */
    private void pullMessage() {
        DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer("topic");
        pullConsumer.setMessageModel(MessageModel.CLUSTERING);
        try {
            Set<MessageQueue> set = fetchMessageQueuesForPullOperation("topic", pullConsumer);
            for (MessageQueue mq : set) {
                System.out.println("Consume from the queue: " + mq);
                //	long offset = consumer.fetchConsumeOffset(mq, true);
                //	PullResultExt pullResult =(PullResultExt)consumer.pull(mq, null, getMessageQueueOffset(mq), 32);
                //消息未到达默认是阻塞10秒，private long consumerPullTimeoutMillis = 1000 * 10;
                PullResultExt pullResult = (PullResultExt) pullConsumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
                putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                switch (pullResult.getPullStatus()) {
                    case FOUND:
                        List<MessageExt> messageExtList = pullResult.getMsgFoundList();
                        for (MessageExt m : messageExtList) {
                            System.out.println(new String(m.getBody()));
                        }
                        break;
                    case NO_MATCHED_MSG:
                        break;
                    case NO_NEW_MSG:
                        break;
                    case OFFSET_ILLEGAL:
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {

        }
    }

    public Set<MessageQueue> fetchMessageQueuesForPullOperation(String topic,DefaultMQPullConsumer consumer)
            throws MQClientException, InterruptedException {
        DefaultMQPullConsumer pullConsumer = consumer; // please init
        long fetchQueueTimeoutMillis = 5000l;
        long fetchQueueNextDelayTimeMillis = 200l;

        Set<MessageQueue> msgQueues = null;
        switch (pullConsumer.getMessageModel()) {
            //广播模式
            case BROADCASTING:
                msgQueues = pullConsumer.fetchSubscribeMessageQueues(topic);
                break;
                //集群模式,获取每个消费者下的队列
            case CLUSTERING:
                msgQueues = pullConsumer.fetchMessageQueuesInBalance(topic);
                // 未获取到负载均衡的时候，等待fetchQueueNextDelayTimeMillis毫秒重新获取，直到超时
                long timeout = 0L;
                while (CollectionUtils.isEmpty(msgQueues) && timeout < fetchQueueTimeoutMillis) {
                    Thread.sleep(fetchQueueNextDelayTimeMillis);
                    timeout += fetchQueueNextDelayTimeMillis;
                    msgQueues = pullConsumer.fetchMessageQueuesInBalance(topic);
                }
                break;
            default:
                break;
        }
        return msgQueues;
    }

    private Map<MessageQueue,Long> offsetTable = new HashMap<>();
    private void putMessageQueueOffset(MessageQueue mq, long offset) {
        offsetTable.put(mq, offset);
    }

    private long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offsetTable.get(mq);
        if (offset != null)
            return offset;
        return 0;
    }
}
