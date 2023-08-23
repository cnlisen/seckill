package com.lee.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.seckill.pojo.Goods;
import com.lee.seckill.vo.GoodsVo;

import java.util.List;

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
public interface IGoodsService extends IService<Goods> {
    /**
     * @description: 获取商品列表
     *
     * @param:
     * @return:
     */
    List<GoodsVo> findGoodVo();
    /**
     * @description: 获取商品详情
     *
     * @param:
     * @return:
     */

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
