package com.lee.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.seckill.pojo.User;
import com.lee.seckill.vo.LoginVo;
import com.lee.seckill.vo.RespBean;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * Seckill Proj
 *
 * @author LiSen
 * @since 2023-08-13
 */
public interface IUserService extends IService<User> {
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    // 根据 cookie 获取用户
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
}
