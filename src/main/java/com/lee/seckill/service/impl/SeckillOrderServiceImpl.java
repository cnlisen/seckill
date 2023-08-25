package com.lee.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.seckill.mapper.SeckillOrderMapper;
import com.lee.seckill.pojo.SeckillOrder;
import com.lee.seckill.pojo.User;
import com.lee.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @description: 获取秒杀结果
     *
     * @param:
     * @return:
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        // 已经生成订单
        if(seckillOrder != null){
            return  seckillOrder.getOrderId();
        }
        // 未生成订单，库存也空了，即秒杀失败，没抢到
        else if (redisTemplate.hasKey("isStockEmpty"+goodsId)){
            return -1L;
        }
        // 未生成订单，库存也还有，说明还在排队处理
        else {
            return 0L;
        }
    }
}
