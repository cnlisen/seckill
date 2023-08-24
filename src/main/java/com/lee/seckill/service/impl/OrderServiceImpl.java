package com.lee.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.lee.seckill.vo.GoodsVo;
import com.lee.seckill.vo.OrderDetailVo;
import com.lee.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    /**
     * @description: 秒杀
     *
     * @param:
     * @return:
     */
    @Override
    public Order seckill(User user, GoodsVo goods) {
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        // 秒杀商品库存 减1
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        // 操作数据库最后记得更新！！！
        seckillGoodsService.updateById(seckillGoods);

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
}
