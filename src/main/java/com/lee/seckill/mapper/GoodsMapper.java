package com.lee.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lee.seckill.pojo.Goods;
import com.lee.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * Seckill Proj
 *
 * @author LiSen
 * @since 2023-08-19
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    /**
     * @description: 获取商品列表
     *
     * @param:
     * @return:
     */
    List<GoodsVo> findGoodsVo();

    /**
     * @description: 获取商品详情
     *
     * @param:
     * @return:
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
