package com.lee.seckill.controller;


import com.lee.seckill.pojo.User;
import com.lee.seckill.service.IOrderService;
import com.lee.seckill.vo.OrderDetailVo;
import com.lee.seckill.vo.RespBean;
import com.lee.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * Seckill Proj
 *
 * @author LiSen
 * @since 2023-08-19
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    /**
     * @description: 订单详情
     *
     * @param:
     * @return:
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }
}
