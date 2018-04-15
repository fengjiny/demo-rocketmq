package com.example.fjy.mq.example;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ProducerTimer {
    /**
     * 生产者的组名
     */
    @Value("group2")
    private String producerGroup;

    /**
     * NameServer 地址
     */
    @Value("localhost:9876")
    private String namesrvAddr;


    @PostConstruct
    public void execute() {
        try {
            //生产者的组名
            DefaultMQProducer producer = new DefaultMQProducer(producerGroup);

            //指定NameServer地址，多个地址以 ; 隔开
            producer.setNamesrvAddr(namesrvAddr);
            producer.start();

            new Timer().schedule(new TimerTask() {
                int i = 0;
                @Override
                public void run() {
                    RocketMqContent content = new RocketMqContent();
                    content.setCityId(i++);
                    content.setDesc("城市" + i);
                    Message msg = new Message("PushTopic", "push", content.toString().getBytes());

                    //发送消息
                    SendResult result = null;
                    try {
                        result = producer.send(msg);
                    }  catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
                }
            }, 0, 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
