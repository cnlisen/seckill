package com.lee.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.seckill.pojo.SeckillOrder;
import com.lee.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * Seckill Proj
 *
 * @author LiSen
 * @since 2023-08-19
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {
    /**
     * @description: 获取秒杀结果
     *
     * @param:
     * @return: orderId:成功 -1:秒杀失败 0:排队中
     */
    Long getResult(User user, Long goodsId);
}
