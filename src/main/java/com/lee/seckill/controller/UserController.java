package com.lee.seckill.controller;


import com.lee.seckill.pojo.User;
import com.lee.seckill.rabbitmq.MQSender;
import com.lee.seckill.vo.RespBean;
import org.springframework.amqp.AmqpIllegalStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * Seckill Proj
 *
 * @author LiSen
 * @since 2023-08-13
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;
    /**
     * @description: 用户信息（测试）
     *
     * @param:
     * @return:
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

    ///**
    // * @description: 测试发送RabbitMQ消息
    // *
    // * @param:
    // * @return:
    // */
    //@RequestMapping("/mq")
    //@ResponseBody
    //public void mq(){
    //    mqSender.sendF("Hello");
    //}
    //
    //@RequestMapping("/mq/fanout")
    //@ResponseBody
    //public void mqF(){
    //    mqSender.sendF("Hello");
    //}
    //
    //@RequestMapping("/mq/direct01")
    //@ResponseBody
    //public void mqD01(){
    //    mqSender.sendD01("Hello,Red");
    //}
    //
    //@RequestMapping("/mq/direct02")
    //@ResponseBody
    //public void mqD02(){
    //    mqSender.sendD02("Hello,Green");
    //}
    //
    //@RequestMapping("/mq/topic01")
    //@ResponseBody
    //public void mqT01(){
    //    mqSender.sendT01("Hello,Red");
    //}
    //@RequestMapping("/mq/topic02")
    //@ResponseBody
    //public void mqT02(){
    //    mqSender.sendT02("Hello,Green");
    //}
    //
    //@RequestMapping("/mq/headers01")
    //@ResponseBody
    //public void mqH01(){
    //    mqSender.sendH01("Hello,Headers01");
    //}
    //@RequestMapping("/mq/headers02")
    //@ResponseBody
    //public void mqH02(){
    //    mqSender.sendH02("Hello,Headers02");
    //}
}
