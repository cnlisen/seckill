package com.lee.seckill.vo;


import com.lee.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends Goods {
    private BigDecimal seckillPrice;
    // 秒杀商品库存
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
