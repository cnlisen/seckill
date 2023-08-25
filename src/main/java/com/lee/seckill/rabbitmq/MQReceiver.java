package com.lee.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @description: 消息消费者
 */
@Service
@Slf4j
public class MQReceiver {

    @RabbitListener(queues = "queue")
    public void receive(Object msg){
        log.info("接受消息：" + msg);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receiveF01(Object msg){
        log.info("QUEUE01接受消息：" + msg);
    }
    @RabbitListener(queues = "queue_fanout02")
    public void receiveF02(Object msg){
        log.info("QUEUE02接受消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct01")
    public void receiveD01(Object msg){
        log.info("QUEUE01接受消息：" + msg);
    }
    @RabbitListener(queues = "queue_direct02")
    public void receiveD02(Object msg){
        log.info("QUEUE02接受消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic01")
    public void receiveT01(Object msg){
        log.info("QUEUE01接受消息：" + msg);
    }
    @RabbitListener(queues = "queue_topic02")
    public void receiveT02(Object msg){
        log.info("QUEUE02接受消息：" + msg);
    }

    @RabbitListener(queues = "queue_headers01")
    public void receiveH01(Message msg){
        log.info("QUEUE01接受对象：" + msg);
        log.info("QUEUE01接受消息：" + new String(msg.getBody()));
    }
    @RabbitListener(queues = "queue_headers02")
    public void receiveH02(Message msg){
        log.info("QUEUE02接受对象：" + msg);
        log.info("QUEUE02接受消息：" + new String(msg.getBody()));
    }
}
