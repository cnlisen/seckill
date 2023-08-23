package com.lee.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.seckill.exception.GlobalException;
import com.lee.seckill.mapper.UserMapper;
import com.lee.seckill.pojo.User;
import com.lee.seckill.service.IUserService;
import com.lee.seckill.utils.CookieUtil;
import com.lee.seckill.utils.MD5Util;
import com.lee.seckill.utils.UUIDUtil;
import com.lee.seckill.vo.LoginVo;
import com.lee.seckill.vo.RespBean;
import com.lee.seckill.vo.RespBeanEnum;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * Seckill Proj
 *
 * @author LiSen
 * @since 2023-08-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    //@Resource
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //// 后端判断数据是否为空
        //if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
        //    return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        //}
        //// 判断手机号码格式是否正确
        //if(!ValidatorUtil.isMobile(mobile)){
        //    return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        //}
        // 根据手机号获取用户 (返回的是User pojo对象，即一个数据表对应的一个类实现，包含setter和getter方法)
        User user = userMapper.selectById(mobile);
        // 判断用户信息是否输入为空
        if(user == null){
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 判断密码是否正确
        if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        // 生成cookie
        String ticket = UUIDUtil.uuid();
        // 向Redis写入键值对数据（user:cookie, user class）
        redisTemplate.opsForValue().set("user:"+ticket, user);
        //request.getSession().setAttribute(ticket, user);
        // 向相应的response响应体中写入cookie
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return RespBean.success(ticket);
    }

    /**
     * @description: 根据 cookie 获取用户
     *
     * @param:
     * @return:
     */

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        // 如果request请求体中没有相应的cookie，则返回null
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
        // 如果cookie为空，则 set
        if(user == null){
            CookieUtil.setCookie(request, response, "userTicker", userTicket);
        }
        // 返回 Redis 中 key=user:cookie 相应的value=user
        return user;
    }


}
