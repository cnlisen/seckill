package com.lee.seckill.vo;

import com.lee.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 订单详情页面
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private Order order;
}
