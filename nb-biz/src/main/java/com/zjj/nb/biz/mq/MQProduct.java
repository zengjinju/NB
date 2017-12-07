package com.zjj.nb.biz.mq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jinju.zeng on 2017/7/3.
 */
@Slf4j
public class MQProduct {

    private static final String SERVER_ADDR = "localhost:9876";
    private static final String INSTANCE_NAME = "NB";
    private static final String GROUP = "nb-group";

    public static void main(String[] args) {
        final DefaultMQProducer producer = new DefaultMQProducer(GROUP);
        producer.setNamesrvAddr(SERVER_ADDR);
        producer.setInstanceName(INSTANCE_NAME);
        try {
            producer.start();
            Message msg = new Message("test-zjj", "TagB", "hello_zjj_abc_123".getBytes());
            SendResult result = producer.send(msg);
            log.info("mq send success，id:{},status:{}",result.getMsgId(),result.getSendStatus());
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                producer.shutdown();
            }
        }));

    }
}
