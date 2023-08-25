package com.lee.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lee.seckill.pojo.Order;
import com.lee.seckill.pojo.SeckillMessage;
import com.lee.seckill.pojo.SeckillOrder;
import com.lee.seckill.pojo.User;
import com.lee.seckill.rabbitmq.MQSender;
import com.lee.seckill.service.IGoodsService;
import com.lee.seckill.service.IOrderService;
import com.lee.seckill.service.ISeckillGoodsService;
import com.lee.seckill.service.ISeckillOrderService;
import com.lee.seckill.utils.JsonUtil;
import com.lee.seckill.vo.GoodsVo;
import com.lee.seckill.vo.RespBean;
import com.lee.seckill.vo.RespBeanEnum;
import com.sun.tools.jconsole.JConsoleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 秒杀相关
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /**
     * @description: 秒杀商品操作
     *
     * MacM1 优化前QPS : 1874
     * Linux 优化前QPS : 274
     *
     * MacM1 优化前QPS : 1802
     * Linux 优化前QPS :
     *
     *
     * @param:
     * @return:
     */
    @RequestMapping("/doSeckill2")
    public String doSecKill2(Model model, User user, Long goodsId){
        if(user == null){
            return "login";
        }
        model.addAttribute("user", user);
        // 返回两表联查的GoodsVo类型的对象结果
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);

        // 判断秒杀商品的库存
        if(goods.getStockCount() <= 0){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        // 根据 user_id 和 goods_id 获取相应的数据库对象（ MyBatis Plus 框架封装方法 ）
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        // 如果秒杀订单数据库中检索出相应的数据
        if(seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }

        // 秒杀商品（ 即去ServiceImpl层实现生成秒杀订单 ）
        Order order = orderService.seckill(user, goods);

        // 使用 model 向前端传输数据
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }



    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 使用Redis实现判重复抢购方法（目的：加快访问速度）
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        // 内存标记，减少Redis的访问
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // Redis预减库存
        Long stock = valueOperations.decrement("seckillGoods:"+goodsId); //decrement是原子性操作
        if(stock < 0){
            // 内存标记
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // 秒杀商品（ 即去ServiceImpl层实现生成秒杀订单 ）
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));

        // 快速返回请求（如；排队中）
        return RespBean.success(0);

        /*

        // 返回两表联查的GoodsVo类型的对象结果
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);

        // 判断秒杀商品的库存
        if(goods.getStockCount() <= 0){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // 根据 user_id 和 goods_id 获取相应的数据库对象（ MyBatis Plus 框架封装方法 ）
        // SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        // 使用Redis实现上述判重复抢购方法（目的：加快访问速度）
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());

        // 如果秒杀订单数据库中检索出相应的数据
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        // 秒杀商品（ 即去ServiceImpl层实现生成秒杀订单 ）
        Order order = orderService.seckill(user, goods);

        return RespBean.success(order);

         */
    }

    /**
     * @description: 获取秒杀结果
     *
     * @param:
     * @return: orderId:成功 -1:秒杀失败 0:排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);

    }

    /**
     * @description: 把商品库存数量加载到Redis
     *
     * @param:
     * @return:
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodVo();
        if(CollectionUtils.isEmpty(list)){
            return ;
        }
        list.forEach(goodsVo -> {
                    redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(), goodsVo.getStockCount());
                    EmptyStockMap.put(goodsVo.getId(), false);
                }
        );
    }
}
