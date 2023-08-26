package com.lee.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.seckill.exception.GlobalException;
import com.lee.seckill.mapper.OrderMapper;
import com.lee.seckill.pojo.Order;
import com.lee.seckill.pojo.SeckillGoods;
import com.lee.seckill.pojo.SeckillOrder;
import com.lee.seckill.pojo.User;
import com.lee.seckill.service.IGoodsService;
import com.lee.seckill.service.IOrderService;
import com.lee.seckill.service.ISeckillGoodsService;
import com.lee.seckill.service.ISeckillOrderService;
import com.lee.seckill.utils.MD5Util;
import com.lee.seckill.utils.UUIDUtil;
import com.lee.seckill.vo.GoodsVo;
import com.lee.seckill.vo.OrderDetailVo;
import com.lee.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * Seckill Proj
 *
 * @author LiSen
 * @since 2023-08-19
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @description: 秒杀
     *
     * @param:
     * @return:
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        // 秒杀商品库存 减1
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        // 操作数据库最后记得更新！！！
        //seckillGoodsService.updateById(seckillGoods);
        //Boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().set("stock_count",
        //        seckillGoods.getStockCount()).eq("id", seckillGoods.getId()).gt("stock_count", 0));
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=stock_count-1")
                        .eq("goods_id", goods.getId())
                        .gt("stock_count", 0));
        //if(!seckillGoodsResult){
        //    return null;
        //}

        // 判断是否还有库存
        if(seckillGoods.getStockCount() < 1){
            valueOperations.set("isStockEmpty"+goods.getId(), "0");
            return null;
        }


        // 生成正常订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        // 将用户抢购商品的订单写入Redis中，减少访问数据库的次数，从而加快访问速度。
        valueOperations.set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);
        return order;
    }
    /**
     * @description: 订单详情
     *
     * @param:
     * @return:
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }

    /**
     * @description: 获取秒杀地址
     *
     * @param:
     * @return:
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        // 将用户id和商品id一一对应生成的id存至Redis中（为了后期秒杀商品进行校验）
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }
    /**
     * @description: 校验秒杀地址
     *
     * @param:
     * @return:
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if( user==null || goodsId<0 || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:"+user.getId()+":"+goodsId);
        return path.equals(redisPath);
    }
}
