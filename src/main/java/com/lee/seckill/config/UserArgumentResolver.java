package com.lee.seckill.config;

import com.lee.seckill.pojo.User;
import com.lee.seckill.service.IUserService;
import com.lee.seckill.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

/**
 * @description: 自定义用户参数
 */

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private IUserService userService;
    /**
     * 识别到哪些参数特征，才使用当前自定义解析器（识别的是Controller中的函数参数，当有以下类型的参数的时候才执行相应的解析器）
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    /**
     * @description: 自定义参数解析器：根据前端请求体中的cookie返回相应的user对象
     *
     * @param:
     * @return:
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        // 根据 请求体request 中获取key=userTicket的相应value
        String ticket = CookieUtil.getCookieValue(request,"userTicket");
        if(StringUtils.isEmpty(ticket)){
            return null;
        }
        // 返回的是User对象
        return userService.getUserByCookie(ticket, request, response);
    }
}
