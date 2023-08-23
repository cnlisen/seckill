package com.lee.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.seckill.mapper.GoodsMapper;
import com.lee.seckill.pojo.Goods;
import com.lee.seckill.service.IGoodsService;
import com.lee.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    GoodsMapper goodsMapper;
    /**
     * @description: 服务实现类
     */

    @Override
    public List<GoodsVo> findGoodVo() {
        return goodsMapper.findGoodsVo();
    }
    /**
     * @description: 获取商品详情
     *
     * @param:
     * @return:
     */

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
