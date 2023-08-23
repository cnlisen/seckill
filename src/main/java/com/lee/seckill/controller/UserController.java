package com.lee.seckill.controller;


import com.lee.seckill.pojo.User;
import com.lee.seckill.vo.RespBean;
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
 * @since 2023-08-13
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * @description: 用户信息（测试）
     *
     * @param:
     * @return:
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }
}
