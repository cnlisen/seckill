package com.lee.seckill.rabbitmq;

import com.lee.seckill.pojo.*;
import com.lee.seckill.service.IGoodsService;
import com.lee.seckill.service.IOrderService;
import com.lee.seckill.utils.JsonUtil;
import com.lee.seckill.vo.GoodsVo;
import com.lee.seckill.vo.RespBean;
import com.lee.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @description: 消息消费者
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        //log.info("接收的消息"+message);

        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodId();

        // 判断库存数量
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount()<1){
            return;
        }

        // 使用Redis实现判重复抢购方法（目的：加快访问速度）
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return;
        }

        // 下单操作
        orderService.seckill(user, goodsVo);


    }


    //@RabbitListener(queues = "queue")
    //public void receive(Object msg){
    //    log.info("接受消息：" + msg);
    //}
    //
    //@RabbitListener(queues = "queue_fanout01")
    //public void receiveF01(Object msg){
    //    log.info("QUEUE01接受消息：" + msg);
    //}
    //@RabbitListener(queues = "queue_fanout02")
    //public void receiveF02(Object msg){
    //    log.info("QUEUE02接受消息：" + msg);
    //}
    //
    //@RabbitListener(queues = "queue_direct01")
    //public void receiveD01(Object msg){
    //    log.info("QUEUE01接受消息：" + msg);
    //}
    //@RabbitListener(queues = "queue_direct02")
    //public void receiveD02(Object msg){
    //    log.info("QUEUE02接受消息：" + msg);
    //}
    //
    //@RabbitListener(queues = "queue_topic01")
    //public void receiveT01(Object msg){
    //    log.info("QUEUE01接受消息：" + msg);
    //}
    //@RabbitListener(queues = "queue_topic02")
    //public void receiveT02(Object msg){
    //    log.info("QUEUE02接受消息：" + msg);
    //}
    //
    //@RabbitListener(queues = "queue_headers01")
    //public void receiveH01(Message msg){
    //    log.info("QUEUE01接受对象：" + msg);
    //    log.info("QUEUE01接受消息：" + new String(msg.getBody()));
    //}
    //@RabbitListener(queues = "queue_headers02")
    //public void receiveH02(Message msg){
    //    log.info("QUEUE02接受对象：" + msg);
    //    log.info("QUEUE02接受消息：" + new String(msg.getBody()));
    //}
}
