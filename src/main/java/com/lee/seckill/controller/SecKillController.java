package com.lee.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lee.seckill.pojo.Order;
import com.lee.seckill.pojo.SeckillOrder;
import com.lee.seckill.pojo.User;
import com.lee.seckill.service.IGoodsService;
import com.lee.seckill.service.IOrderService;
import com.lee.seckill.service.ISeckillGoodsService;
import com.lee.seckill.service.ISeckillOrderService;
import com.lee.seckill.vo.GoodsVo;
import com.lee.seckill.vo.RespBean;
import com.lee.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 秒杀相关
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;

    /**
     * @description: 秒杀商品操作
     *
     * MacM1 优化前QPS : 1874
     * Linux 优化前QPS : 274
     *
     * @param:
     * @return:
     */
    @RequestMapping("/doSeckill2")
    public String doSecKill2(Model model, User user, Long goodsId){
        if(user == null){
            return "login";
        }
        model.addAttribute("user", user);
        // 返回两表联查的GoodsVo类型的对象结果
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);

        // 判断秒杀商品的库存
        if(goods.getStockCount() <= 0){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        // 根据 user_id 和 goods_id 获取相应的数据库对象（ MyBatis Plus 框架封装方法 ）
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        // 如果秒杀订单数据库中检索出相应的数据
        if(seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }

        // 秒杀商品（ 即去ServiceImpl层实现生成秒杀订单 ）
        Order order = orderService.seckill(user, goods);

        // 使用 model 向前端传输数据
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }



    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // 返回两表联查的GoodsVo类型的对象结果
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);

        // 判断秒杀商品的库存
        if(goods.getStockCount() <= 0){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // 根据 user_id 和 goods_id 获取相应的数据库对象（ MyBatis Plus 框架封装方法 ）
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        // 如果秒杀订单数据库中检索出相应的数据
        if(seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        // 秒杀商品（ 即去ServiceImpl层实现生成秒杀订单 ）
        Order order = orderService.seckill(user, goods);

        return RespBean.success(order);
    }
}
