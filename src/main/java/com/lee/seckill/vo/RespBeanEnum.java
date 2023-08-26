package com.lee.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @description: 公共返回对象枚举
 */

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    // 通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500, "服务端异常"),

    // 登录模块 5002xx
    LOGIN_ERROR(500210, "用户名或密码不正确"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    MOBLIE_NOT_EXIST(500213, "手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214, "更新密码失败"),
    SESSION_ERROR(500215, "用户不存在"),
    BIND_ERROR(500212, "参数校验异常"),
    // 秒杀模块 5005xx
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501, "该商品每人限购一件"),
    REQUEST_ILLEGAL(500502, "请求非法，请重新尝试"),
    // 订单模块 5003xx
    ORDER_NOT_EXIST(500300,"订单信息不存在"),
    ;
    private final Integer code;
    private final String message;

}
