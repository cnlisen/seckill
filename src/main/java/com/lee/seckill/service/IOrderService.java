package com.lee.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.seckill.pojo.Order;
import com.lee.seckill.pojo.User;
import com.lee.seckill.vo.GoodsVo;

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
public interface IOrderService extends IService<Order> {
    /**
     * @description: 秒杀
     *
     * @param:
     * @return:
     */

    Order seckill(User user, GoodsVo goods);
}